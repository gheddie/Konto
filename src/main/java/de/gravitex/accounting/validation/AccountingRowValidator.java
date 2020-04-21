package de.gravitex.accounting.validation;

import java.util.Set;

import de.gravitex.accounting.AccountingRow;

public interface AccountingRowValidator {

	Set<RowValidationResult> getErrors(AccountingRow accountingRow);
}