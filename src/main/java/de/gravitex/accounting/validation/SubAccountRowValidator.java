package de.gravitex.accounting.validation;

import java.util.HashSet;
import java.util.Set;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.modality.PaymentModality;

public class SubAccountRowValidator extends MainAccountRowValidator {
	
	@Override
	public Set<RowValidationResult> getErrors(AccountingRow accountingRow, PaymentModality paymentModality) {
		
		Set<RowValidationResult> errors = super.getErrors(accountingRow, paymentModality);
		Set<RowValidationResult> result = new HashSet<RowValidationResult>();
		result.addAll(errors);
		
		if (accountingRow.getMainAccount() == null || accountingRow.getMainAccount().length() == 0) {
			result.add(RowValidationResult.fromValues(AccountingError.INVALID_MAINACCOUNT, AlertMessageType.ERROR));
		}

		// TODO
		/*
		if (accountingRow.getMainAccountReference() == null || accountingRow.getMainAccountReference().length() == 0) {
			result.add(RowValidationResult.fromValues(AccountingError.INVALID_MAINACCOUNT_REF, AlertMessageType.WARNING));
		}
		*/
		
		return result;
	}
}