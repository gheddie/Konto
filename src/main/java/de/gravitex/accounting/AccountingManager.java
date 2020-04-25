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

import org.apache.log4j.Logger;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.enumeration.BudgetEvaluationResult;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.SubAccountReferenceCheck;
import de.gravitex.accounting.exception.AccountingManagerException;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilteredValueReceiver;
import de.gravitex.accounting.gui.AlertMessagesBuilder;
import de.gravitex.accounting.logic.BudgetModel;
import de.gravitex.accounting.logic.MonthlyBudgetCategoryResult;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.util.OverlapChecker;
import de.gravitex.accounting.util.TimelineProjectonResult;
import de.gravitex.accounting.util.TimelineProjector;
import de.gravitex.accounting.validation.SubAccountValidation;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingManager extends FilteredValueReceiver<AccountingRow> {

	private static final Logger logger = Logger.getLogger(AccountingManager.class);

	public static final String UNDEFINED_CATEGORY = "Undefiniert";

	private AccountManagerSettings accountManagerSettings;

	private Income income;

	private String mainAccountKey;

	private AccountingData mainAccount;

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
			throw new GenericAccountingException("no payment modality found for category '" + categoryKey + "'!!", null,
					AccountingError.NO_PM_FOR_CATEGORY);
		}
		return paymentModality;
	}

	public HashMap<MonthKey, Properties> prepareBudgets(LocalDate startingDate, boolean complete) {

		HashMap<MonthKey, Properties> extendedProperties = new HashMap<MonthKey, Properties>();
		HashMap<MonthKey, Set<BudgetEvaluation>> failuresPerMonth = new HashMap<MonthKey, Set<BudgetEvaluation>>();
		for (Category category : mainAccount.getDistinctCategories()) {
			category.setPaymentModality(mainAccount.getPaymentModalitys().get(category.getCategory()));
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
				extendedProperties.put(monthKey,
						completeBudgetPlanning(monthKey, additionalBudgetEvaluations, complete));
			}
		}

		return extendedProperties;
	}

	private Properties completeBudgetPlanning(MonthKey monthKey, Set<BudgetEvaluation> additionalBudgetEvaluations,
			boolean complete) {

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

	public PaymentModality initPaymentModality(MonthKey monthKey, String category) {
		PaymentModality paymentModality = getPaymentModality(category);
		paymentModality.reset();
		paymentModality.setMonthKey(monthKey);
		paymentModality.setCategory(category);
		return paymentModality;
	}

	public AccountingResultMonthModel getAccountingResultMonthModel(MonthKey monthKey) {
		AccountingMonth accountingMonth = getMainAccount().getAccountingMonth(monthKey);
		AccountingResultMonthModel result = new AccountingResultMonthModel();
		result.setMonthKey(monthKey);
		for (String category : accountingMonth.getDistinctCategories()) {
			result.addCategoryModel(getAccountingResultCategoryModel(monthKey, category));
		}
		return result;
	}

	public AccountingResultCategoryModel getAccountingResultCategoryModel(MonthKey monthKey, String category) {

		AccountingMonth accountingMonth = getMainAccount().getAccountingMonth(monthKey);
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
			throw new GenericAccountingException("request limit --> both month key and category must be set!!", null,
					null);
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
			throw new GenericAccountingException("request limit --> no value set for budget planning for month ["
					+ monthKey + "] available and category [" + category + "]!!", null, null);
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
		getMainAccount().acceptFilter(filterValue.getAttributeName(), filterValue.getValue());
	}

	public List<AccountingRow> getFilteredEntries() {
		return getMainAccount().getFilteredEntriesSorted();
	}

	@Override
	protected List<AccountingRow> loadAllItems() {
		return getMainAccount().getFilteredEntriesSorted();
	}

	public AccountManagerSettings getSettings() {
		return accountManagerSettings;
	}

	public HashMap<MonthKey, BudgetPlanning> getBudgetPlannings() {
		return getMainAccount().getBudgetPlannings();
	}

	public List<AccountingRow> getSubEntries(String categoryKey, Integer mainRunningIndex) {

		AccountingData subAccount = getSubAccount(categoryKey);
		if (subAccount == null) {
			return new ArrayList<AccountingRow>();
		}
		subAccount.acceptFilter(AccountingData.ATTR_MAIN_ACCOUNT, getMainAccount().getAccountKey())
				.acceptFilter(AccountingData.ATTR_MAIN_ACCOUNT_REFERENCE, mainRunningIndex);
		return subAccount.getFilteredEntriesSorted();
	}

	public AccountingData getSubAccount(String categoryKey) {
		return getMainAccount().getSubAccount(categoryKey);
	}

	public SubAccountValidation checkSubEntries(AccountingRow mainAccountingRow) {

		if (mainAccountingRow == null) {
			throw new AccountingManagerException("can not check sub entries for a NULL main accouting row!!");
		}
		if (mainAccount == null) {
			throw new AccountingManagerException("can not check sub entries without a main account set!!");
		}
		if (!mainAccount.getSubAccountReferences().containsKey(mainAccountingRow.getCategory())) {
			return SubAccountValidation.fromValues(SubAccountReferenceCheck.NONE, null, null);
		}
		SubAccountReferenceCheck check = null;
		List<AccountingRow> subEntries = getSubEntries(mainAccountingRow.getCategory(),
				mainAccountingRow.getRunningIndex());
		BigDecimal subEntriesSum = new BigDecimal(0);
		for (AccountingRow subEntry : subEntries) {
			subEntriesSum = subEntriesSum.add(subEntry.getAmount());
		}
		if (subEntriesSum.negate().equals(mainAccountingRow.getAmount())) {
			check = SubAccountReferenceCheck.VALID;
		} else {
			check = SubAccountReferenceCheck.INVALID;
		}
		return SubAccountValidation.fromValues(check, mainAccountingRow.getAmount(), subEntriesSum);
	}

	public String getSubAccountName(String category) {
		return getMainAccount().getSubAccountName(category);
	}

	public BudgetModel getBudgetModel(MonthKey aMonthKey, boolean aFixedBudgetEntriesOnly,
			boolean aShowUnbudgetedtEntries, boolean aShowRealBudgetEntries) {

		HashMap<String, BigDecimal> categorySumsInMonth = getCategorySums(aMonthKey);
		HashMap<String, MonthlyBudgetCategoryResult> budgetValuesForMonth = new HashMap<String, MonthlyBudgetCategoryResult>();
		BudgetPlanning budgetPlanningForMonth = getBudgetPlannings().get(aMonthKey);
		BigDecimal spentAmount = new BigDecimal(0);
		for (String categoryKey : categorySumsInMonth.keySet()) {
			if (getPaymentModality(categoryKey).isOutgoing()) {
				budgetValuesForMonth.put(categoryKey, MonthlyBudgetCategoryResult.fromValues(categoryKey,
						budgetPlanningForMonth.getAmountForCategory(categoryKey), categorySumsInMonth.get(categoryKey)));
				spentAmount = spentAmount.add(categorySumsInMonth.get(categoryKey).abs());
			}
		}
		return BudgetModel.fromValues(aMonthKey, budgetValuesForMonth, getAvailableIncome(aMonthKey), spentAmount);
	}

	public HashMap<String, BigDecimal> getCategorySums(MonthKey monthKey) {
		AccountingMonth monthData = getMainAccount().getAccountingMonth(monthKey);
		if (monthData == null) {
			return null;
		}
		HashMap<String, BigDecimal> categorySums = new HashMap<String, BigDecimal>();
		for (String category : monthData.getDistinctCategories()) {
			BigDecimal categorySum = new BigDecimal(0);
			for (AccountingRow accountingRow : monthData.getRowObjectsByCategory(category)) {
				categorySum = categorySum.add(accountingRow.getAmount());
			}
			categorySums.put(category, categorySum);
		}
		return categorySums;
	}
}