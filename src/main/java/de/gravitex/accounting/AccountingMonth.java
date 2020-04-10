package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.modality.PaymentModality;
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

	public StringBuffer print(boolean onlyExceeded) {
		StringBuffer result = new StringBuffer();
		result.append(" ######################## "+monthKey+" ######################## " + "\n");
		result.append("\n");
		BigDecimal totalPlusMinusAmountOfMonth = new BigDecimal(0);
		HashMap<String, List<AccountingRow>> sortedByCategory = getSortedByWhat();
		for (String categoryKey : sortedByCategory.keySet()) {
			PaymentModality paymentModality = AccountingManager.getInstance().getPaymentModality(categoryKey);
			paymentModality.reset();
			paymentModality.setMonthKey(monthKey);
			paymentModality.setCategory(categoryKey);
			paymentModality.prepare();
			printer = CategoryResultPrinter.fromValues(categoryKey, paymentModality, monthKey);
			for (AccountingRow obj : sortedByCategory.get(categoryKey)) {
				printer.addRow(obj);
			}
			StringBuffer buffer = printer.print(onlyExceeded);
			result.append(buffer);
			totalPlusMinusAmountOfMonth = totalPlusMinusAmountOfMonth.add(printer.getTotalAmount());
		}
		result.append("\n");
		result.append(" ---------------> total +/- in month: " + totalPlusMinusAmountOfMonth + "\n");
		return result;
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