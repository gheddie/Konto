package de.gravitex.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingResultModelRow {
	
	private int runningIndex;

	private BigDecimal amount;
	
	private LocalDate date;
	
	private String text;
	
	private AccountingResultModelRow() {
		// ...
	}

	public static AccountingResultModelRow fromValues(int runningIndex, BigDecimal amount, LocalDate date, String text) {
		AccountingResultModelRow accountingResultModelRow = new AccountingResultModelRow();
		accountingResultModelRow.setRunningIndex(runningIndex);
		accountingResultModelRow.setAmount(amount);
		accountingResultModelRow.setDate(date);
		accountingResultModelRow.setText(text);
		return accountingResultModelRow;
	}

	public String[] asTableRow() {
		return new String[] {String.valueOf(runningIndex), date.toString(), amount.toString(), text};
	}
}