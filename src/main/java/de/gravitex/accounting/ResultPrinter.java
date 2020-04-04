package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ResultPrinter {

	private List<AccountingRow> accountingRows = new ArrayList<AccountingRow>();
	
	private BigDecimal totalAmount = new BigDecimal(0);
	
	private String category;

	private ResultPrinter() {
		// ...
	}

	public static ResultPrinter fromCategory(String aCategory) {
		ResultPrinter resultPrinter = new ResultPrinter();
		resultPrinter.setCategory(aCategory);
		return resultPrinter;
	}

	public void addRow(AccountingRow accountingRow) {
		accountingRows.add(accountingRow);
		totalAmount = totalAmount.add(accountingRow.getAmount());
	}

	public void print() {
		System.out.println(" ------------------------------------ " + category + " ------------------------------------ ");
		for (AccountingRow accountingRow : accountingRows) {
			System.out.println(formatRow(accountingRow));
		}
		System.out.println("SUMME ------> " + totalAmount);
	}

	private String formatRow(AccountingRow accountingRow) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(inflate(accountingRow.getAmount(), 25));
		buffer.append(inflate(accountingRow.getDate(), 25));
		buffer.append(inflate(accountingRow.getText(), 25));
		return buffer.toString();
	}

	private String inflate(Object value, int minLength) {
		String str = String.valueOf(value);
		if (str.length() > minLength) {
			return str;
		}
		String result = str;
		for (int i = 0; i < minLength - str.length(); i++) {
			result += " ";
		}
		return result;
	}
}