package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.util.CategoryResultPrinter;
import lombok.Data;

@Data
public class AccountingMonth {
	
	private String monthKey;
	
	private List<AccountingRow> rowObjects;

	private CategoryResultPrinter printer;

	private AccountingMonth() {

	}

	public static AccountingMonth fromValues(String monthKey, List<AccountingRow> rowObjects) {
		AccountingMonth aMonth = new AccountingMonth();
		aMonth.setMonthKey(monthKey);
		aMonth.setRowObjects(rowObjects);
		return aMonth;
	}

	private HashMap<String, List<AccountingRow>> getSortedByWhat() {
		HashMap<String, List<AccountingRow>> result = new HashMap<String, List<AccountingRow>>();
		for (AccountingRow rowObject : rowObjects) {
			if (result.get(rowObject.getCategory()) == null) {
				result.put(rowObject.getCategory(), new ArrayList());
			}
			result.get(rowObject.getCategory()).add(rowObject);
		}
		return result;
	}

	public List<AccountingRow> getRowObjectsByCategory(String category) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : rowObjects) {
			if (accountingRow.hasCategory(category)) {
				result.add(accountingRow);
			}
		}
		return result;
	}

	public Set<String> getDistinctCategories() {
		HashSet<String> result = new HashSet<String>();
		for (AccountingRow accountingRow : rowObjects) {
			result.add(accountingRow.getCategory());
		}
		return result;
	}
}