package de.gravitex.accounting.exception;

import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.gui.AlertMessage;
import lombok.Data;

@Data
public class AccountingException extends RuntimeException {

	private static final long serialVersionUID = 5428035104385968283L;

	private AccountingError accountingError;

	private AccountingRow accountingRow;

	private List<AlertMessage> alertMessages;

	public AccountingException(String message, AccountingError anAccountingError, AccountingRow anAccountingRow, List<AlertMessage> anAlertMessages) {
		super(message);
		this.accountingError = anAccountingError;
		this.accountingRow = anAccountingRow;
		this.alertMessages = anAlertMessages;
	}

	public AccountingException(String message, AccountingError anAccountingError, AccountingRow anAccountingRow) {
		this(message, anAccountingError, anAccountingRow, null);
	}

	public String toString() {
		return getClass().getSimpleName() + " (" + getMessage() + ") [ERROR:" + accountingError + "] in {"
				+ accountingRow + "}!!";
	}
	
	public boolean hasAlertMessages() {
		return (alertMessages != null && alertMessages.size() > 0);
	}

	public StringBuffer asStringBuffer() {
		StringBuffer buffer = new StringBuffer();
		for (AlertMessage alertMessage : alertMessages) {
			buffer.append(alertMessage + "\n");
		}
		return buffer;
	}
}