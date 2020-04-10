package de.gravitex.accounting.exception;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import lombok.Data;

@Data
public class AccountingException extends RuntimeException {

	private static final long serialVersionUID = 5428035104385968283L;
	
	private AccountingError accountingError;
	
	private AccountingRow accountingRow;

	public AccountingException(String message, AccountingError anAccountingError, AccountingRow anAccountingRow) {
		super(message);
		this.accountingError = anAccountingError;
		this.accountingRow = anAccountingRow;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " (" + getMessage() + ") [ERROR:" + accountingError + "] in {"
				+ accountingRow + "}!!";
	}
}