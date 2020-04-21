package de.gravitex.accounting.validation;

import java.util.HashSet;
import java.util.Set;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.modality.PaymentModality;

public class MainAccountRowValidator implements AccountingRowValidator {

	@Override
	public Set<RowValidationResult> getErrors(AccountingRow accountingRow, PaymentModality paymentModality) {
		
		Set<RowValidationResult> result = new HashSet<RowValidationResult>();
		
		// wrong amount (e.g. outgoing modality with positive amount)
		if (!paymentModality.checkAmount(accountingRow)) {
			result.add(RowValidationResult.fromValues(AccountingError.INVALID_AMOUNT, AlertMessageType.ERROR));
		}
		
		// no running index
		if (accountingRow.getRunningIndex() == null) {
			result.add(RowValidationResult.fromValues(AccountingError.NO_RUNNING_INDEX, AlertMessageType.ERROR));
		}
		// no date
		if (accountingRow.getDate() == null) {
			result.add(RowValidationResult.fromValues(AccountingError.NO_DATE, AlertMessageType.ERROR));
		}
		if (accountingRow.getCategory() == null) {
			result.add(RowValidationResult.fromValues(AccountingError.NO_CATEGORY, AlertMessageType.ERROR));
		}
		if (accountingRow.getAmount() == null) {
			result.add(RowValidationResult.fromValues(AccountingError.NO_AMOUNT, AlertMessageType.ERROR));
		}
		// undefined without without a text
		if (accountingRow.getCategory().equals(AccountingManager.UNDEFINED_CATEGORY)
				&& (accountingRow.getText() == null || accountingRow.getText().length() == 0)) {
			result.add(RowValidationResult.fromValues(AccountingError.UNDEF_NO_TEXT, AlertMessageType.ERROR));
		}
		
		return result;
	}
}