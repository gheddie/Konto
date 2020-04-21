package de.gravitex.accounting.validation;

import java.util.Set;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.modality.PaymentModality;

public interface AccountingRowValidator {

	Set<RowValidationResult> getErrors(AccountingRow accountingRow, PaymentModality paymentModality);
}