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

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.enumeration.BudgetEvaluationResult;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.impl.EqualFilter;
import de.gravitex.accounting.filter.interfacing.FilteredValueReceiver;
import de.gravitex.accounting.gui.AlertMessagesBuilder;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.util.OverlapChecker;
import de.gravitex.accounting.util.TimelineProjectonResult;
import de.gravitex.accounting.util.TimelineProjector;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingManager implements FilteredValueReceiver {
	
	public static final String ATTR_PARTNER = "partner";
	public static final String ATTR_CATEGORY = "category";
	public static final String ATTR_ALARM = "alarm";
	public static final String ATTR_DATE = "date";

	public static final String UNDEFINED_CATEGORY = "Undefiniert";
	
	private AccountingData accountingData;
	
	private HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
	
	private HashMap<MonthKey, BudgetPlanning> budgetPlannings = new HashMap<MonthKey, BudgetPlanning>();

	private AccountManagerSettings accountManagerSettings;

	private Income income;
	
	private static final EntityFilter<AccountingRow> entityFilter = new EntityFilter<AccountingRow>();
	static {
		entityFilter.registerFilter(new EqualFilter(ATTR_CATEGORY));
		entityFilter.registerFilter(new EqualFilter(ATTR_PARTNER));
	}
	
	public AccountingManager withAccountingData(AccountingData accountingData) {
		this.accountingData = accountingData;
		accountingData.validate();
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
	
	public AccountingManager withIncome(Income aIncome) {
		this.income = aIncome;
		return this;
	}
	
	private boolean budgetPlanningAvailableFor(MonthKey monthKey, String category) {
		if (budgetPlannings == null) {
			return false;
		}
		BudgetPlanning budgetPlanningForMonth = budgetPlannings.get(monthKey);
		if (budgetPlanningForMonth == null) {
			return false;
		}
		return (budgetPlanningForMonth.getProperties().keySet().contains(category));
	}
	
	public List<BudgetEvaluation> evaluateBudgetProjection(Category category, LocalDate startingDate) {

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
		MonthKey actualAppearance = MonthKey.fromValues(startingDate.getMonthValue(), startingDate.getYear());
		boolean budgetPlanningAvailable = false;
		while (months < accountManagerSettings.getProjectionDurationInMonths()) {
			actualAppearance = AccountingUtil.nextMonthlyTimeStamp(actualAppearance, PaymentPeriod.MONTH);
			budgetPlanningAvailable = budgetPlanningAvailableFor(actualAppearance, category.getCategory());
			months += PaymentPeriod.MONTH.getDurationInMonths();
			if (timelineProjectonResult.hasTimeStamp(actualAppearance)) {
				// budget planning should be there...
				System.out.println("projecting: " + actualAppearance + " *");
				if (!budgetPlanningAvailable) {
					evaluationResult.add(BudgetEvaluation.fromValues(category.getCategory(), actualAppearance,
							BudgetEvaluationResult.MISSING_BUDGET));
				}
			} else {
				// budget planning should NOT be there...
				System.out.println("projecting: " + actualAppearance);
				if (budgetPlanningAvailable) {
					evaluationResult.add(BudgetEvaluation.fromValues(category.getCategory(), actualAppearance,
							BudgetEvaluationResult.MISPLACED_BUDGET));
				}
			}
		}
		return evaluationResult;
	}
	
	public AccountingRow getLastAppearanceOfCategory(String category) {
		
		List<AccountingRow> allEntries = getAllEntriesForCategory(category);
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
	
	public HashMap<MonthKey, Properties> prepareBudgets(LocalDate startingDate, boolean complete) {

		HashMap<MonthKey, Properties> extendedProperties = new HashMap<MonthKey, Properties>();
		HashMap<MonthKey, Set<BudgetEvaluation>> failuresPerMonth = new HashMap<MonthKey, Set<BudgetEvaluation>>();
		for (Category category : accountingData.getDistinctCategories()) {
			category.setPaymentModality(paymentModalitys.get(category.getCategory()));
			List<BudgetEvaluation> evaluation = evaluateBudgetProjection(category, startingDate);
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
				extendedProperties.put(monthKey, completeBudgetPlanning(monthKey, additionalBudgetEvaluations, complete));
			}
		}

		return extendedProperties;
	}
	
	private Properties completeBudgetPlanning(MonthKey monthKey, Set<BudgetEvaluation> additionalBudgetEvaluations, boolean complete) {
		
		Properties existingBudgetPlannings = null;
		if (complete) {
			BudgetPlanning budgetPlanningForMonth = budgetPlannings.get(monthKey);
			existingBudgetPlannings = budgetPlanningForMonth.getProperties();
			if (existingBudgetPlannings == null) {
				// not existing
				existingBudgetPlannings = new Properties();	
			}
		} else {
			// only the missing!!
			existingBudgetPlannings = new Properties();	
		}
		for (BudgetEvaluation budgetEvaluation : additionalBudgetEvaluations) {
			BigDecimal amount = getLastAppearanceOfCategory(budgetEvaluation.getCategory()).getAmount();
			existingBudgetPlannings.put(budgetEvaluation.getCategory(), String.valueOf(Math.abs(amount.intValue())));
		}
		return existingBudgetPlannings;
	}
	
	private MonthKey getInitialAppeareanceOfCategory(String category) {
		List<AccountingRow> allEntries = getAllEntriesForCategory(category);
		if (allEntries == null || allEntries.size() == 0) {
			return null;
		}
		LocalDate initalDate = allEntries.get(0).getDate();
		return MonthKey.fromValues(initalDate.getMonthValue(), initalDate.getYear());
	}

	public BigDecimal getAvailableIncome(MonthKey monthKey) {
		return income.getIncomeForMonth(monthKey);
	}

	public List<AccountingRow> getAllEntriesForCategory(String category) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : accountingData.getAllEntriesSorted()) {
			if (accountingRow.getCategory().equals(category)) {
				result.add(accountingRow);
			}
		}
		Collections.sort(result);
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
	
	public AccountingResultMonthModel getAccountingResultMonthModel(MonthKey monthKey) {
		AccountingMonth accountingMonth = accountingData.get(monthKey);
		AccountingResultMonthModel result = new AccountingResultMonthModel();
		result.setMonthKey(monthKey);
		for (String category : accountingMonth.getDistinctCategories()) {
			result.addCategoryModel(getAccountingResultCategoryModel(monthKey, category));
		}
		return result;
	}
	
	public AccountingResultCategoryModel getAccountingResultCategoryModel(MonthKey monthKey, String category) {
		
		AccountingMonth accountingMonth = accountingData.get(monthKey);
		List<AccountingRow> rowsByCategory = accountingMonth.getRowObjectsByCategory(category);
		AccountingResultCategoryModel categoryModel = new AccountingResultCategoryModel();
		categoryModel.setMonthKey(monthKey);
		categoryModel.setCategory(category);
		List<AccountingResultModelRow> accountingResultModelRows = new ArrayList<AccountingResultModelRow>();
		BigDecimal sum = new BigDecimal(0);
		for (AccountingRow accountingRow : rowsByCategory) {
			accountingResultModelRows.add(AccountingResultModelRow.fromValues(accountingRow.getRunningIndex(),
					accountingRow.getAmount(), accountingRow.getDate(), accountingRow.getText()));
			sum = sum.add(accountingRow.getAmount());
		}
		categoryModel.setAccountingResultModelRows(accountingResultModelRows);
		categoryModel.setSum(sum);
		// initPaymentModality(monthKey, category);
		Integer limit = requestLimit(monthKey, category);
		categoryModel.setBudget(limit != null ? new BigDecimal(limit) : null);
		return categoryModel;
	}
	
	public Integer requestLimit(MonthKey monthKey, String category) {
		
		if (monthKey == null || category == null) {
			throw new AccountingException("request limit --> both month key and category must be set!!", null, null);
		}
		Properties properties = budgetPlannings.get(monthKey).getProperties();
		if (properties == null) {
			throw new AccountingException(
					"request limit --> no budget planning available for month key [" + monthKey + "]!!", null, null);
		}
		Object entry = properties.get(category);
		if (entry == null) {
			return null;
			/*
			 * throw new
			 * AccountingException("request limit --> no budget planning available for category ["
			 * + category + "] in month key [" + monthKey + "]!!", null, null);
			 */
		}
		String value = String.valueOf(properties.get(category));
		if (value == null || value.length() == 0) {
			throw new AccountingException("request limit --> no value set for budget planning for month [" + monthKey
					+ "] available and category [" + category + "]!!", null, null);
		}
		int limit = 0;
		try {
			limit = Integer.parseInt(String.valueOf(value));
		} catch (Exception e) {
			throw new AccountingException(
					"request limit --> unparsable numeric value [" + value + "] set for budget planning for month ["
							+ monthKey + "] available and category [" + category + "]!!",
					null, null);
		}
		return limit;
	}

	public boolean isCategoryPeriodically(String category) {
		return getPaymentModality(category).isPeriodically();
	}

	public void checkValidities(String category) {
		if (!getPaymentModality(category).isPeriodically()) {
			return;
		}
		List<AccountingRow> entries = getAllEntriesForCategory(category);
		OverlapChecker checker = new OverlapChecker();
		for (AccountingRow accountingRow : entries) {
			checker.withPeriod(accountingRow.getValidFrom(), accountingRow.getValidUntil());
		}
		if (!checker.check()) {
			AlertMessagesBuilder builder = new AlertMessagesBuilder();
			if (checker.isInvalidPeriodFlag()) {
				builder.withMessage(AlertMessageType.ERROR, "Mindestens ein ungültiger Zeitraum!!");
			}
			if (checker.isOverlapFlag()) {
				builder.withMessage(AlertMessageType.ERROR, "Überschneidung von Zeiträumen!!");
			}
			if (checker.getRemainingDays().size() > 0) {
				builder.withMessage(AlertMessageType.ERROR,
						"Zeiträumen-Lücken von insgesamt " + checker.getRemainingDays().size() + " Tagen entdeckt!!");
			}
			throw new AccountingException("check validity error!!", AccountingError.NO_VALID_PERIOD, null,
					builder.getAlertMessages());			
		}
	}

	public List<Category> getPeriodicalPaymentCategories() {
		List<Category> result = new ArrayList<Category>();
		Set<Category> distinctCategories = getAccountingData().getDistinctCategories();
		for (Category category : distinctCategories) {
			if (getPaymentModality(category.getCategory()).isPeriodically()) {
				result.add(category);
			}
		}
		return result;
	}

	@Override
	public void receiveFilterValue(FilterValue filterValue) {
		System.out.println("receiveFilterValue: " + filterValue);
		entityFilter.setFilter(filterValue.getAttributeName(), filterValue.getValue());
	}

	public List<AccountingRow> getFilteredEntries() {
		return entityFilter.filterItems(getAllEntries());
	}
	
	public Set<Category> getAllCategories(AccountingData accountingData, AccountingManager manager) {
		Set<Category> allCategories = new HashSet<Category>();
		AccountingMonth accountingMonth = null;
		for (MonthKey key : accountingData.keySet()) {
			accountingMonth = accountingData.get(key);
			Set<String> distinctCategories = accountingMonth.getDistinctCategories();
			for (String category : distinctCategories) {
				allCategories.add(Category.fromValues(category, manager.getPaymentModality(category)));	
			}
		}
		return allCategories;
	}
	
	public Set<String> getAllPartners(AccountingData accountingData) {
		Set<String> allPartners = new HashSet<String>();
		for (AccountingMonth accountingMonth : accountingData.getAccountingMonths()) {
			for (AccountingRow accountingRow : accountingMonth.getRowObjects()) {
				if (accountingRow.getPartner() != null && accountingRow.getPartner().length() > 0) {
					allPartners.add(accountingRow.getPartner());
				}
			}
		}
		return allPartners;
	}

	public List<AccountingRow> getAllEntries() {
		List<AccountingRow> allEntries = new ArrayList<AccountingRow>();
		for (MonthKey key : accountingData.keySet()) {
			for (AccountingRow accountingRow : accountingData.get(key).getRowObjects()) {
				allEntries.add(accountingRow);
			}
		}
		Collections.sort(allEntries);
		return allEntries;
	}

	@Override
	public List<?> loadDistinctItems(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}
}