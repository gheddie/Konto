package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.modality.PaymentModalityFactory;
import de.gravitex.accounting.util.ResultPrinter;
import lombok.Data;

@Data
public class AccountingMonth {
	
	private String monthKey;
	
	private List<AccountingRow> rowObjects;

	private ResultPrinter printer;

	private AccountingMonth() {

	}

	public static AccountingMonth fromValues(String monthKey, List<AccountingRow> rowObjects) {
		AccountingMonth aMonth = new AccountingMonth();
		aMonth.setMonthKey(monthKey);
		aMonth.setRowObjects(rowObjects);
		return aMonth;
	}

	public void print(boolean onlyExceeded) {
		System.out.println(" ######################## "+monthKey+" ######################## ");
		BigDecimal totalPlusMinusAmountOfMonth = new BigDecimal(0);
		HashMap<String, List<AccountingRow>> sortedByCategory = getSortedByWhat();
		for (String categoryKey : sortedByCategory.keySet()) {
			printer = ResultPrinter.fromValues(categoryKey, PaymentModalityFactory.getPaymentModality(categoryKey));
			for (AccountingRow obj : sortedByCategory.get(categoryKey)) {
				printer.addRow(obj);
			}
			printer.print(onlyExceeded);
			totalPlusMinusAmountOfMonth = totalPlusMinusAmountOfMonth.add(printer.getTotalAmount());
		}
		System.out.println();
		System.out.println(" ---------------> total +/- in month: " + totalPlusMinusAmountOfMonth );
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
}