package de.gravitex.accounting.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;

public class AccountingDao {

	public static Set<String> getAllCategories(AccountingData accountingData) {
		Set<String> allCategories = new HashSet<String>();
		AccountingMonth accountingMonth = null;
		for (MonthKey key : accountingData.keySet()) {
			accountingMonth = accountingData.get(key);
			allCategories.addAll(accountingMonth.getDistinctCategories());
		}
		return allCategories;
	}

	public static List<AccountingRow> getAllEntriesForCategory(AccountingData accountingData, String category) {
		List<AccountingRow> allEntriesForCategory = new ArrayList<AccountingRow>();
		for (MonthKey key : accountingData.keySet()) {
			for (AccountingRow accountingRow : accountingData.get(key).getRowObjectsByCategory(category)) {
				allEntriesForCategory.add(accountingRow);
			}
		}
		Collections.sort(allEntriesForCategory);
		return allEntriesForCategory;
	}
}