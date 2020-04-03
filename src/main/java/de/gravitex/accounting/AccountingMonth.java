package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class AccountingMonth {
	
	private String monthKey;
	
	private List<AccountingRow> rowObjects;

	private AccountingMonth() {

	}

	public static AccountingMonth fromValues(String monthKey, List<AccountingRow> rowObjects) {
		AccountingMonth aMonth = new AccountingMonth();
		aMonth.setMonthKey(monthKey);
		aMonth.setRowObjects(rowObjects);
		return aMonth;
	}

	public void printSortedRows(boolean showObjects) {
		System.out.println(" ------ "+monthKey+" ------ ");
		BigDecimal totalPlusMinusAmount = new BigDecimal(0);
		HashMap<String, List<AccountingRow>> sortedByWhat = getSortedByWhat();
		for (String whatKey : sortedByWhat.keySet()) {
			BigDecimal totalWHATAmount = new BigDecimal(0);
			System.out.println(" ###### "+whatKey+" ###### ");
			for (AccountingRow obj : sortedByWhat.get(whatKey)) {
				if (showObjects) {
					System.out.println(obj.getAmount() + " [" + obj.getCategory() + "] (" + obj.getDate() + ") ["
							+ AccountingUtil.getMonthKey(obj.getDate()) + "]");	
				}
				totalWHATAmount = totalWHATAmount.add(obj.getAmount());
			}
			System.out.println(" ---------------> total WHAT : " + totalWHATAmount);
			totalPlusMinusAmount = totalPlusMinusAmount.add(totalWHATAmount);
		}
		System.out.println(" ---------------> total +/- : " + totalPlusMinusAmount );
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
}