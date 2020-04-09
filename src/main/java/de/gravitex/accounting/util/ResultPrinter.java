package de.gravitex.accounting.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.modality.PaymentModality;
import lombok.Data;

@Data
public class ResultPrinter {

	private List<AccountingRow> accountingRows = new ArrayList<AccountingRow>();
	
	private BigDecimal totalAmount = new BigDecimal(0);
	
	private String category;
	
	private PaymentModality budgetPlanning;

	private ResultPrinter() {
		// ...
	}

	public static ResultPrinter fromValues(String aCategory, PaymentModality aBudgetPlanning) {
		ResultPrinter resultPrinter = new ResultPrinter();
		resultPrinter.setCategory(aCategory);
		resultPrinter.setBudgetPlanning(aBudgetPlanning);
		return resultPrinter;
	}

	public void addRow(AccountingRow accountingRow) {
		accountingRows.add(accountingRow);
		totalAmount = totalAmount.add(accountingRow.getAmount());
	}

	// TODO onlyExceeded... 
	public void print(boolean onlyExceeded) {
		
		if (!budgetPlanning.amountExceeded(totalAmount) && onlyExceeded) {
			return;
		}
		
		System.out.println(" ------------------------------------ " + category + " ------------------------------------ ");
		// sort by date
		
		Collections.sort(accountingRows);
		for (AccountingRow accountingRow : accountingRows) {
			System.out.println(formatRow(accountingRow));
		}
		System.out.println();
		if (budgetPlanning == null) {
			System.out.println("SUMME ------> " + totalAmount);			
		} else {
			if (budgetPlanning.amountExceeded(totalAmount)) {
				System.out.println(
						"SUMME ------> " + totalAmount + " [BUDGET EXCEEDED (" + budgetPlanning.getLimit() + ")]");	
			} else {
				System.out.println("SUMME ------> " + totalAmount + " [IN BUDGET (" + budgetPlanning.getLimit() + ")]");
			}
		}
	}

	private String formatRow(AccountingRow accountingRow) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(inflate(accountingRow.getAmount(), 25));
		buffer.append(inflate(accountingRow.getDate(), 25));
		buffer.append(inflate(accountingRow.getText() != null ? accountingRow.getText() : "", 25));
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