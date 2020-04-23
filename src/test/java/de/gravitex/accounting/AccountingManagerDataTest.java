package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class AccountingManagerDataTest {

	protected AccountingRow getAccountingRow(Integer runningIndex, LocalDate date, String category, BigDecimal amount) {
		AccountingRow accountingRow = new AccountingRow();
		accountingRow.setRunningIndex(runningIndex);
		accountingRow.setDate(date);
		accountingRow.setCategory(category);
		accountingRow.setAmount(amount);
		return accountingRow;
	}
}