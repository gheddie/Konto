package de.gravitex.accounting;

import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.enumeration.BudgetEvaluationResult;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilteredValueReceiver;
import de.gravitex.accounting.filter.impl.DateRangeFilter;
import de.gravitex.accounting.filter.impl.EqualFilter;
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

public class AccountingManager extends FilteredValueReceiver<AccountingRow> {
	
	private static final Logger logger = Logger.getLogger(AccountingManager.class);
	
	public static final String ATTR_PARTNER = "partner";
	public static final String ATTR_CATEGORY = "category";
	public static final String ATTR_ALARM = "alarm";
	public static final String ATTR_DATE = "date";

	public static final String UNDEFINED_CATEGORY = "Undefiniert";

	private AccountManagerSettings accountManagerSettings;

	private Income income;

	private HashMap<String, AccountingData> accountingDataMap = new HashMap<String, AccountingData>();
	
	private String mainAccountKey;
	
	private static final EntityFilter<AccountingRow> entityFilter = new EntityFilter<AccountingRow>();
	static {
		entityFilter.registerFilter(new EqualFilter(ATTR_CATEGORY));
		entityFilter.registerFilter(new EqualFilter(ATTR_PARTNER));
		entityFilter.registerFilter(new DateRangeFilter(ATTR_DATE));
	}
	
	public AccountingManager withAccountingData(AccountingData accountingData) {
		
		accountingData.validate();
		if (accountingData.getAccountingType().equals(AccountingType.MAIN_ACCOUNT)) {
			mainAccountKey = accountingData.getAccountKey();
		}
		accountingDataMap.put(accountingData.getAccountKey(), accountingData);
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
		
		HashMap<MonthKey, BudgetPlanning> budgetPlannings = getMainAccount().getBudgetPlannings();
		
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
			logger.info("payment modiality '" + category.getPaymentModality().getClass().getSimpleName()
					+ "' is not projectable -- returning!!");
			return new ArrayList<BudgetEvaluation>();
		}

		logger.info(" --- projecting [" + category.getCategory() + "] ----: "
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
				logger.info("projecting: " + actualAppearance + " *");
				if (!budgetPlanningAvailable) {
					evaluationResult.add(BudgetEvaluation.fromValues(category.getCategory(), actualAppearance,
							BudgetEvaluationResult.MISSING_BUDGET));
				}
			} else {
				// budget planning should NOT be there...
				logger.info("projecting: " + actualAppearance);
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
		PaymentModality paymentModality = getMainAccount().getPaymentModalitys().get(categoryKey);
		if (paymentModality == null) {
			throw new GenericAccountingException("no payment modality found for category '" + categoryKey + "'!!",
					null, AccountingError.NO_PM_FOR_CATEGORY);
		}
		return paymentModality;
	}
	
	public HashMap<MonthKey, Properties> prepareBudgets(LocalDate startingDate, boolean complete) {

		HashMap<MonthKey, Properties> extendedProperties = new HashMap<MonthKey, Properties>();
		HashMap<MonthKey, Set<BudgetEvaluation>> failuresPerMonth = new HashMap<MonthKey, Set<BudgetEvaluation>>();
		for (Category category : getMainAccount().getDistinctCategories()) {
			category.setPaymentModality(getMainAccount().getPaymentModalitys().get(category.getCategory()));
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
			BudgetPlanning budgetPlanningForMonth = getMainAccount().getBudgetPlannings().get(monthKey);
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
		for (AccountingRow accountingRow : getMainAccount().getAllEntriesSorted()) {
			if (accountingRow.getCategory().equals(category)) {
				result.add(accountingRow);
			}
		}
		Collections.sort(result);
		return result;
	}

	public List<AccountingRow> getAllEntriesForPartner(String partner) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : getMainAccount().getAllEntriesSorted()) {
			if (accountingRow.getPartner() != null && accountingRow.getPartner().equals(partner)) {
				result.add(accountingRow);
			}
		}
		return result;
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
		AccountingMonth accountingMonth = getMainAccount().get(monthKey);
		AccountingResultMonthModel result = new AccountingResultMonthModel();
		result.setMonthKey(monthKey);
		for (String category : accountingMonth.getDistinctCategories()) {
			result.addCategoryModel(getAccountingResultCategoryModel(monthKey, category));
		}
		return result;
	}
	
	public AccountingResultCategoryModel getAccountingResultCategoryModel(MonthKey monthKey, String category) {
		
		AccountingMonth accountingMonth = getMainAccount().get(monthKey);
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
		Integer limit = requestLimit(monthKey, category);
		categoryModel.setBudget(limit != null ? new BigDecimal(limit) : null);
		return categoryModel;
	}
	
	public Integer requestLimit(MonthKey monthKey, String category) {
		
		if (monthKey == null || category == null) {
			throw new GenericAccountingException("request limit --> both month key and category must be set!!", null, null);
		}
		Properties properties = getMainAccount().getBudgetPlannings().get(monthKey).getProperties();
		if (properties == null) {
			throw new GenericAccountingException(
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
			throw new GenericAccountingException("request limit --> no value set for budget planning for month [" + monthKey
					+ "] available and category [" + category + "]!!", null, null);
		}
		int limit = 0;
		try {
			limit = Integer.parseInt(String.valueOf(value));
		} catch (Exception e) {
			throw new GenericAccountingException(
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
			throw new GenericAccountingException("check validity error!!", null, builder.getAlertMessages(),
					AccountingError.NO_VALID_PERIOD);			
		}
	}

	public List<Category> getPeriodicalPaymentCategories() {
		List<Category> result = new ArrayList<Category>();
		Set<Category> distinctCategories = getMainAccount().getDistinctCategories();
		for (Category category : distinctCategories) {
			if (getPaymentModality(category.getCategory()).isPeriodically()) {
				result.add(category);
			}
		}
		return result;
	}

	@Override
	public void receiveFilterValue(FilterValue filterValue) {
		logger.info("receiveFilterValue: " + filterValue);
		entityFilter.setFilter(filterValue.getAttributeName(), filterValue.getValue());
	}

	public List<AccountingRow> getFilteredEntries() {
		return entityFilter.filterItems(getAllEntries());
	}
	
	public List<AccountingRow> getAllEntries() {
		List<AccountingRow> allEntries = new ArrayList<AccountingRow>();
		AccountingData mainAccount = getMainAccount();
		for (MonthKey key : mainAccount.keySet()) {
			for (AccountingRow accountingRow : mainAccount.get(key).getRowObjects()) {
				allEntries.add(accountingRow);
			}
		}
		Collections.sort(allEntries);
		return allEntries;
	}

	@Override
	protected List<AccountingRow> loadAllItems() {
		return getMainAccount().getAllEntriesSorted();
	}

	public AccountManagerSettings getAccountManagerSettings() {
		return accountManagerSettings;
	}
	
	public AccountingData getMainAccount() {
		return accountingDataMap.get(mainAccountKey);
	}

	public HashMap<MonthKey, BudgetPlanning> getBudgetPlannings() {
		return getMainAccount().getBudgetPlannings();
	}
}