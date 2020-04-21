package de.gravitex.accounting.exception;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.validation.RowValidationResult;

public class ValidatingAccountingException extends AccountingException {

	private static final long serialVersionUID = -6113405599675302280L;
	
	private RowValidationResult[] rowValidationResults;

	public ValidatingAccountingException(String message, AccountingRow accountingRow, RowValidationResult... rowValidationResults) {
		super(message, accountingRow);
		this.rowValidationResults = rowValidationResults;
	}
}