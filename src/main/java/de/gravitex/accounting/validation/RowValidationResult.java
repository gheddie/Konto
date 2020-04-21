package de.gravitex.accounting.validation;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AlertMessageType;
import lombok.Data;

@Data
public class RowValidationResult {

	private AccountingError accountingError;
	
	private AlertMessageType alertMessageType;
	
	private RowValidationResult() {
		super();
	}
	
	public static RowValidationResult fromValues(AccountingError anAccountingError, AlertMessageType anAlertMessageType) {
		RowValidationResult rowValidationResult = new RowValidationResult();
		rowValidationResult.setAccountingError(anAccountingError);
		rowValidationResult.setAlertMessageType(anAlertMessageType);
		return rowValidationResult;
	}
}