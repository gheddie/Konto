package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.application.AccountingLoader;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterDefinition;
import de.gravitex.accounting.filter.impl.BooleanFilter;
import de.gravitex.accounting.filter.impl.DateRangeFilter;
import de.gravitex.accounting.filter.impl.EqualFilter;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingData {

	private HashMap<MonthKey, AccountingMonth> data = new HashMap<MonthKey, AccountingMonth>();
	
	private HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
	
	private HashMap<MonthKey, BudgetPlanning> budgetPlannings = new HashMap<MonthKey, BudgetPlanning>();
	
	private HashMap<String, String> subAccountReferences = new HashMap<String, String>();
	
	private AccountingType accountingType;
	
	private String accountKey;
	
	private HashMap<String, AccountingData> subAccounts = new HashMap<String, AccountingData>();
	
	public static final String ATTR_PARTNER = "partner";
	public static final String ATTR_CATEGORY = "category";
	public static final String ATTR_DATE = "date";
	public static final String ATTR_ALARM = "alarm";
	
	public static final String ATTR_MAIN_ACCOUNT = "mainAccount";
	public static final String ATTR_MAIN_ACCOUNT_REFERENCE = "mainAccountReference";
	
	private EntityFilter<AccountingRow> entityFilter;
	
	private static final List<FilterDefinition> mainAccountFilters = new ArrayList<FilterDefinition>();
	static {
		mainAccountFilters.add(FilterDefinition.fromValues(ATTR_CATEGORY, EqualFilter.class));
		mainAccountFilters.add(FilterDefinition.fromValues(ATTR_PARTNER, EqualFilter.class));
		mainAccountFilters.add(FilterDefinition.fromValues(ATTR_DATE, DateRangeFilter.class));
		mainAccountFilters.add(FilterDefinition.fromValues(ATTR_ALARM, BooleanFilter.class));
	}
	
	private static final List<FilterDefinition> subAccountFilters = new ArrayList<FilterDefinition>();
	static {
		subAccountFilters.add(FilterDefinition.fromValues(ATTR_MAIN_ACCOUNT, EqualFilter.class));
		subAccountFilters.add(FilterDefinition.fromValues(ATTR_MAIN_ACCOUNT_REFERENCE, EqualFilter.class));
	}
	
	private AccountingLoader accountingLoader = new AccountingLoader();

	public Set<MonthKey> keySet() {
		return data.keySet();
	}

	public AccountingMonth get(MonthKey key) {
		return data.get(key);
	}

	public void put(MonthKey key, AccountingMonth accountingMonth) {
		data.put(key, accountingMonth);
	}

	public Collection<AccountingMonth> getAccountingMonths() {
		return data.values();
	}

	public Set<Category> getDistinctCategories() {
		Set<Category> result = new HashSet<Category>();
		for (AccountingRow accountingRow : getAllEntriesSorted()) {
			result.add(Category.fromValues(accountingRow.getCategory(), null));
		}
		return result;
	}
	
	public List<AccountingRow> getFilteredEntriesSorted() {
		List<AccountingRow> result = getAllEntriesSorted();
		assertEntityFilterSet();
		return entityFilter.filterItems(result);
	}

	public List<AccountingRow> getAllEntriesSorted() {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (MonthKey monthKey : data.keySet()) {
			result.addAll(data.get(monthKey).getRowObjects());
		}
		Collections.sort(result);
		return result;
	}

	public void validate() {
		for (AccountingMonth accountingMonth : data.values()) {
			accountingMonth.validate(accountingType, paymentModalitys);
		}
	}

	public AccountingData getSubAccount(String categoryKey) {
		if (!subAccountReferences.containsKey(categoryKey)) {
			return null;
		}
		AccountingData subAccount = assertSubAccountPresent(getSubAccountKey(categoryKey));
		return subAccount;
	}

	private String getSubAccountKey(String categoryKey) {
		return subAccountReferences.get(categoryKey);
	}

	public AccountingData assertSubAccountPresent(String accountKey) {
		if (subAccounts.get(accountKey) == null) {
			subAccounts.put(accountKey, accountingLoader.loadAccountingData(accountKey, AccountingType.SUB_ACCOUNT));
		}
		return subAccounts.get(accountKey);
	}

	public AccountingData acceptFilter(String attributeName, Object value) {
		assertEntityFilterSet();
		entityFilter.setFilter(attributeName, value);
		return this;
	}

	private void assertEntityFilterSet() {
		if (entityFilter != null) {
			return;
		}
		entityFilter = new EntityFilter<AccountingRow>();
		switch (accountingType) {
		case MAIN_ACCOUNT:
			for (FilterDefinition filterDefinition : mainAccountFilters) {
				entityFilter.registerFilter(filterDefinition);
			}
			break;
		case SUB_ACCOUNT:
			for (FilterDefinition filterDefinition : subAccountFilters) {
				entityFilter.registerFilter(filterDefinition);
			}
			break;
		}
	}

	public String getSubAccountName(String category) {
		return subAccountReferences.get(category);
	}
}