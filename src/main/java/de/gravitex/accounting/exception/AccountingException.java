package de.gravitex.accounting.exception;

import de.gravitex.accounting.AccountingRow;

public abstract class AccountingException extends RuntimeException {

	private static final long serialVersionUID = -8422380740238783078L;
	
	private AccountingRow accountingRow;

	public AccountingException(String message, AccountingRow accountingRow) {
		super(message);
		this.accountingRow = accountingRow;
	}
	
	public AccountingRow getAccountingRow() {
		return accountingRow;
	}
}