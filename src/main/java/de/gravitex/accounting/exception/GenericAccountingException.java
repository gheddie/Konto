package de.gravitex.accounting.exception;

import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.gui.AlertMessage;
import lombok.Data;

@Data
public class GenericAccountingException extends AccountingException {

	private static final long serialVersionUID = 5428035104385968283L;

	private AccountingError[] accountingError;

	private List<AlertMessage> alertMessages;

	public GenericAccountingException(String message, AccountingRow anAccountingRow, List<AlertMessage> anAlertMessages, AccountingError... anAccountingErrors) {
		super(message, anAccountingRow);
		this.accountingError = anAccountingErrors;
		this.alertMessages = anAlertMessages;
	}

	public GenericAccountingException(String message, AccountingRow anAccountingRow, AccountingError... anAccountingErrors) {
		this(message, anAccountingRow, null, anAccountingErrors);
	}

	public String toString() {
		return getClass().getSimpleName() + " (" + getMessage() + ") [ERROR:" + accountingError + "] in {"
				+ getAccountingRow() + "}!!";
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