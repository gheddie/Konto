package de.gravitex.accounting.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.wrapper.Category;

public class AccountingDao {

	public static Set<Category> getAllCategories(AccountingData accountingData) {
		Set<Category> allCategories = new HashSet<Category>();
		AccountingMonth accountingMonth = null;
		for (MonthKey key : accountingData.keySet()) {
			accountingMonth = accountingData.get(key);
			Set<String> distinctCategories = accountingMonth.getDistinctCategories();
			for (String category : distinctCategories) {
				allCategories.add(Category.fromValues(category, AccountingManager.getInstance().getPaymentModality(category)));	
			}
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