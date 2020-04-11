package de.gravitex.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Vector;

import lombok.Data;

@Data
public class AccountingResultModelRow {

	private BigDecimal amount;
	
	private LocalDate date;
	
	private String text;
	
	private AccountingResultModelRow() {
		// ...
	}

	public static AccountingResultModelRow fromValues(BigDecimal amount, LocalDate date, String text) {
		AccountingResultModelRow accountingResultModelRow = new AccountingResultModelRow();
		accountingResultModelRow.setAmount(amount);
		accountingResultModelRow.setDate(date);
		accountingResultModelRow.setText(text);
		return accountingResultModelRow;
	}

	public String[] asTableRow() {
		return new String[] {date.toString(), amount.toString(), text};
	}
}