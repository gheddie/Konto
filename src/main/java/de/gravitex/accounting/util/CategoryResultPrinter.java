package de.gravitex.accounting.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.modality.PaymentModality;
import lombok.Data;

@Data
public class CategoryResultPrinter {

	private List<AccountingRow> accountingRows = new ArrayList<AccountingRow>();

	private String category;
	
	private String monthKey;

	private PaymentModality paymentModalityDefinition;

	private CategoryResultPrinter() {
		// ...
	}

	public static CategoryResultPrinter fromValues(String aCategory, PaymentModality aPaymentModalityDefinition, String aMonthKey) {
		CategoryResultPrinter resultPrinter = new CategoryResultPrinter();
		resultPrinter.setCategory(aCategory);
		resultPrinter.setPaymentModalityDefinition(aPaymentModalityDefinition);
		resultPrinter.setMonthKey(aMonthKey);
		return resultPrinter;
	}

	public void addRow(AccountingRow accountingRow) {
		accountingRows.add(accountingRow);
		paymentModalityDefinition.addAmount(accountingRow.getAmount());
	}

	// TODO onlyExceeded...
	public StringBuffer print(boolean onlyExceeded) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" ------------------------------------ " + category + " ------------------------------------ " + "\n");
		Collections.sort(accountingRows);
		for (AccountingRow accountingRow : accountingRows) {
			buffer.append(formatRow(accountingRow) + "\n");
		}
		buffer.append(paymentModalityDefinition.getCalculationFooter().toString() + "\n");
		return buffer;
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