package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.util.CategoryResultPrinter;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class AccountingMonth {
	
	private MonthKey monthKey;
	
	private List<AccountingRow> rowObjects;

	private CategoryResultPrinter printer;

	private AccountingMonth() {

	}

	public static AccountingMonth fromValues(MonthKey monthKey, List<AccountingRow> rowObjects) {
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
	
	public List<AccountingRow> getRowObjectsByPartner(String partner) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : rowObjects) {
			if (accountingRow.hasPartner(partner)) {
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

	public void validate() {
		AccountingError error = null;
		for (AccountingRow accountingRow : rowObjects) {
			error = accountingRow.getError();
			if (error != null) {
				throw new AccountingException("error on validating accounting month!!", error, accountingRow);
			}
		}
	}
}