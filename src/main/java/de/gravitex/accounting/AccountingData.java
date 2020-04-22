package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.enumeration.AccountingType;
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
	
	// reference to main account if accounting type = 'SUB_ACCOUNT'
	private String mainAccount;

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
}