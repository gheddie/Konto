package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingData {

	private HashMap<MonthKey, AccountingMonth> data = new HashMap<MonthKey, AccountingMonth>();

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
	
	public Set<String> getDistinctPartners() {
		Set<String> result = new HashSet<String>();
		for (AccountingRow accountingRow : getAllEntriesSorted()) {
			if (accountingRow.getPartner() != null && accountingRow.getPartner().length() > 0) {
				result.add(accountingRow.getPartner());				
			}
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
}