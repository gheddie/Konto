package de.gravitex.accounting.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingSingleton;
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
				allCategories.add(Category.fromValues(category, AccountingSingleton.getInstance().getPaymentModality(category)));	
			}
		}
		return allCategories;
	}
	
	public static Set<String> getAllPartners(AccountingData accountingData) {
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

	public static List<AccountingRow> getAllEntriesForPartner(AccountingData accountingData, String partner) {
		List<AccountingRow> allEntriesForPartner = new ArrayList<AccountingRow>();
		for (MonthKey key : accountingData.keySet()) {
			for (AccountingRow accountingRow : accountingData.get(key).getRowObjectsByPartner(partner)) {
				allEntriesForPartner.add(accountingRow);
			}
		}
		Collections.sort(allEntriesForPartner);
		return allEntriesForPartner;
	}

	public static List<AccountingRow> getAllEntries(AccountingData accountingData) {
		List<AccountingRow> allEntries = new ArrayList<AccountingRow>();
		for (MonthKey key : accountingData.keySet()) {
			for (AccountingRow accountingRow : accountingData.get(key).getRowObjects()) {
				allEntries.add(accountingRow);
			}
		}
		Collections.sort(allEntries);
		return allEntries;
	}
}