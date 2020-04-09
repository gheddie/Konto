package de.gravitex.accounting.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.modality.PaymentModalityDefinition;
import lombok.Data;

@Data
public class CategoryResultPrinter {

	private List<AccountingRow> accountingRows = new ArrayList<AccountingRow>();

	private String category;

	private PaymentModalityDefinition paymentModalityDefinition;

	private CategoryResultPrinter() {
		// ...
	}

	public static CategoryResultPrinter fromValues(String aCategory, PaymentModalityDefinition aPaymentModalityDefinition) {
		CategoryResultPrinter resultPrinter = new CategoryResultPrinter();
		resultPrinter.setCategory(aCategory);
		resultPrinter.setPaymentModalityDefinition(aPaymentModalityDefinition);
		return resultPrinter;
	}

	public void addRow(AccountingRow accountingRow) {
		accountingRows.add(accountingRow);
		paymentModalityDefinition.addAmount(accountingRow.getAmount());
	}

	// TODO onlyExceeded...
	public void print(boolean onlyExceeded) {

		System.out.println(
				" ------------------------------------ " + category + " ------------------------------------ ");
		Collections.sort(accountingRows);
		for (AccountingRow accountingRow : accountingRows) {
			System.out.println(formatRow(accountingRow));
		}
		System.out.println(paymentModalityDefinition.getCalculationFooter().toString());
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

	public BigDecimal getTotalAmount() {
		return paymentModalityDefinition.getTotalAmount();
	}
}