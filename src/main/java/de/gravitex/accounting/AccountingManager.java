package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.gravitex.accounting.dao.AccountingDao;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.BudgetEvaluationResult;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.util.TimelineProjectonResult;
import de.gravitex.accounting.util.TimelineProjector;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingManager {
	
	private static final BigDecimal AVAILABLE_INCOME = new BigDecimal(2400);
	
	private AccountingData accountingData;
	
	private HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
	
	private HashMap<MonthKey, BudgetPlanning> budgetPlannings = new HashMap<MonthKey, BudgetPlanning>();

	private AccountManagerSettings accountManagerSettings;

	public AccountingManager withAccountingData(AccountingData accountingData) {
		this.accountingData = accountingData;
		return this;
	}

	public AccountingManager withBudgetPlannings(HashMap<MonthKey, BudgetPlanning> budgetPlannings) {
		this.budgetPlannings = budgetPlannings;
		return this;
	}

	public AccountingManager withPaymentModalitys(HashMap<String, PaymentModality> paymentModalitys) {
		this.paymentModalitys = paymentModalitys;
		return this;
	}
	
	public AccountingManager withSettings(AccountManagerSettings accountManagerSettings) {
		this.accountManagerSettings = accountManagerSettings;
		return this;
	}
	
	private boolean budgetPlanningPresentFor(MonthKey monthKey) {
		return budgetPlannings.keySet().contains(monthKey);
	}

	private boolean budgetPlanningAvailableFor(MonthKey monthKey, String category) {
		BudgetPlanning budgetPlanningForMonth = budgetPlannings.get(monthKey);
		if (budgetPlanningForMonth == null) {
			return false;
		}
		return (budgetPlanningForMonth.getProperties().keySet().contains(category));
	}
	
	public List<BudgetEvaluation> evaluateBudgetProjection(Category category) {

		if (!category.getPaymentModality().isProjectable()) {
			System.out.println("payment modiality '" + category.getPaymentModality().getClass().getSimpleName()
					+ "' is not projectable -- returning!!");
			return new ArrayList<BudgetEvaluation>();
		}

		System.out.println(" --- projecting [" + category.getCategory() + "] ----: "
				+ category.getPaymentModality().getClass().getSimpleName());

		List<BudgetEvaluation> evaluationResult = new ArrayList<BudgetEvaluation>();

		MonthKey initialAppeareance = getInitialAppeareanceOfCategory(category.getCategory());
		if (initialAppeareance == null) {
			return null;
		}

		TimelineProjectonResult timelineProjectonResult = TimelineProjector
				.fromValues(initialAppeareance, category.getPaymentModality().getPaymentPeriod(),
						accountManagerSettings.getProjectionDurationInMonths())
				.getResult();
		// search in budget plannings...
		int months = 0;
		LocalDate now = LocalDate.now();
		MonthKey actualAppearance = MonthKey.fromValues(now.getMonthValue(), now.getYear());
		boolean budgetPlanningAvailable = false;
		while (months < accountManagerSettings.getProjectionDurationInMonths()) {
			actualAppearance = AccountingUtil.nextMonthlyTimeStamp(actualAppearance, PaymentPeriod.MONTH);
			budgetPlanningAvailable = budgetPlanningAvailableFor(actualAppearance, category.getCategory());
			months += PaymentPeriod.MONTH.getDurationInMonths();
			if (timelineProjectonResult.hasTimeStamp(actualAppearance)) {
				// budget planning should be there...
				System.out.println("projecting: " + actualAppearance + " *");
				if (!budgetPlanningAvailable) {
					if (budgetPlanningPresentFor(actualAppearance)) {
						evaluationResult.add(BudgetEvaluation.fromValues(category.getCategory(), actualAppearance,
								BudgetEvaluationResult.MISSING_BUDGET));
					}
				}
			} else {
				// budget planning should NOT be there...
				System.out.println("projecting: " + actualAppearance);
				if (budgetPlanningAvailable) {
					if (budgetPlanningPresentFor(actualAppearance)) {
						evaluationResult.add(BudgetEvaluation.fromValues(category.getCategory(), actualAppearance,
								BudgetEvaluationResult.MISPLACED_BUDGET));
					}
				}
			}
		}
		return evaluationResult;
	}
	
	public AccountingRow getLastAppearanceOfCategory(String category) {
		
		List<AccountingRow> allEntries = AccountingDao.getAllEntriesForCategory(accountingData, category);
		if (allEntries == null || allEntries.size() == 0) {
			return null;
		}
		return allEntries.get(allEntries.size() - 1);
	}
	
	public PaymentModality getPaymentModality(String categoryKey) {
		PaymentModality paymentModality = paymentModalitys.get(categoryKey);
		if (paymentModality == null) {
			throw new AccountingException("no payment modality found for category '" + categoryKey + "'!!",
					AccountingError.NO_PM_FOR_CATEGORY, null);
		}
		return paymentModality;
	}
	
	public HashMap<MonthKey, Properties> prepareBudgets() {

		HashMap<MonthKey, Properties> extendedProperties = new HashMap<MonthKey, Properties>();
		HashMap<MonthKey, Set<BudgetEvaluation>> failuresPerMonth = new HashMap<MonthKey, Set<BudgetEvaluation>>();
		for (Category category : accountingData.getDistinctCategories()) {
			category.setPaymentModality(paymentModalitys.get(category.getCategory()));
			List<BudgetEvaluation> evaluation = evaluateBudgetProjection(category);
			for (BudgetEvaluation budgetEvaluation : evaluation) {
				if (budgetEvaluation.getBudgetEvaluationResult().equals(BudgetEvaluationResult.MISSING_BUDGET)) {
					if (failuresPerMonth.get(budgetEvaluation.getMonthKey()) == null) {
						failuresPerMonth.put(budgetEvaluation.getMonthKey(), new HashSet<BudgetEvaluation>());
					}
					failuresPerMonth.get(budgetEvaluation.getMonthKey()).add(budgetEvaluation);
				}
			}
		}

		Set<BudgetEvaluation> additionalBudgetEvaluations = null;
		Set<MonthKey> keySet = failuresPerMonth.keySet();
		List<MonthKey> monthKeyList = new ArrayList<MonthKey>(keySet);
		Collections.sort(monthKeyList);
		for (MonthKey monthKey : monthKeyList) {
			additionalBudgetEvaluations = failuresPerMonth.get(monthKey);
			if (additionalBudgetEvaluations.size() > 0) {
				extendedProperties.put(monthKey, completeBudgetPlanning(monthKey, additionalBudgetEvaluations));
			}
		}

		return extendedProperties;
	}
	
	private Properties completeBudgetPlanning(MonthKey monthKey, Set<BudgetEvaluation> additionalBudgetEvaluations) {

		// get existing categories
		// Properties existingBudgetPlannings =
		// budgetPlannings.get(monthKey).getProperties();

		// only the missing!!
		Properties existingBudgetPlannings = new Properties();

		for (BudgetEvaluation budgetEvaluation : additionalBudgetEvaluations) {
			existingBudgetPlannings.put(budgetEvaluation.getCategory(),
					getLastAppearanceOfCategory(budgetEvaluation.getCategory()).getAmount().toString());
		}
		return existingBudgetPlannings;
	}
	
	private MonthKey getInitialAppeareanceOfCategory(String category) {
		List<AccountingRow> allEntries = AccountingDao.getAllEntriesForCategory(accountingData, category);
		if (allEntries == null || allEntries.size() == 0) {
			return null;
		}
		LocalDate initalDate = allEntries.get(0).getDate();
		return MonthKey.fromValues(initalDate.getMonthValue(), initalDate.getYear());
	}

	public BigDecimal getAvailableIncome(MonthKey monthKey) {
		// TODO
		return AVAILABLE_INCOME;
	}

	public List<AccountingRow> getAllEntriesForCategory(String category) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : accountingData.getAllEntriesSorted()) {
			if (accountingRow.getCategory().equals(category)) {
				result.add(accountingRow);
			}
		}
		return result;
	}

	public List<AccountingRow> getAllEntriesForPartner(String partner) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : accountingData.getAllEntriesSorted()) {
			if (accountingRow.getPartner() != null && accountingRow.getPartner().equals(partner)) {
				result.add(accountingRow);
			}
		}
		return result;
	}

	public Set<String> getDistinctPartners() {
		return accountingData.getDistinctPartners();
	}
	
	public PaymentModality initPaymentModality(MonthKey monthKey, String category) {
		PaymentModality paymentModality = getPaymentModality(category);
		paymentModality.reset();
		paymentModality.setMonthKey(monthKey);
		paymentModality.setCategory(category);
		paymentModality.prepare();
		return paymentModality;
	}
}