package de.gravitex.accounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.exception.ValidatingAccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.validation.MainAccountRowValidator;
import de.gravitex.accounting.validation.RowValidationResult;
import de.gravitex.accounting.validation.AccountingRowValidator;
import de.gravitex.accounting.validation.SubAccountRowValidator;
import lombok.Data;

@Data
public class AccountingMonth {
	
	private static final HashMap<AccountingType, AccountingRowValidator> rowValidators = new HashMap<AccountingType, AccountingRowValidator>();
	static {
		rowValidators.put(AccountingType.MAIN_ACCOUNT, new MainAccountRowValidator());
		rowValidators.put(AccountingType.SUB_ACCOUNT, new SubAccountRowValidator());
	}
	
	private MonthKey monthKey;
	
	private List<AccountingRow> rowObjects;

	private AccountingMonth() {
		super();
	}

	public static AccountingMonth fromValues(MonthKey monthKey, List<AccountingRow> rowObjects) {
		AccountingMonth aMonth = new AccountingMonth();
		aMonth.setMonthKey(monthKey);
		aMonth.setRowObjects(rowObjects);
		return aMonth;
	}

	public List<AccountingRow> getRowObjectsByCategory(String category) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : rowObjects) {
			if (accountingRow.hasCategory(category)) {
				result.add(accountingRow);
			}
		}
		return result;
	}
	
	public List<AccountingRow> getRowObjectsByPartner(String partner) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : rowObjects) {
			if (accountingRow.hasPartner(partner)) {
				result.add(accountingRow);
			}
		}
		return result;
	}

	public Set<String> getDistinctCategories() {
		HashSet<String> result = new HashSet<String>();
		for (AccountingRow accountingRow : rowObjects) {
			result.add(accountingRow.getCategory());
		}
		return result;
	}

	public void validate(AccountingType accountingType, HashMap<String, PaymentModality> paymentModalitys) {
		
		for (AccountingRow accountingRow : rowObjects) {
			AccountingRowValidator rowValidator = rowValidators.get(accountingType);
			PaymentModality paymentModality = paymentModalitys.get(accountingRow.getCategory());
			Set<RowValidationResult> errors = rowValidator.getErrors(accountingRow, paymentModality);
			if (errors != null && errors.size() > 0) {
				RowValidationResult[] errorArray = errors.toArray(new RowValidationResult[] {});
				ValidatingAccountingException validatingAccountingException = new ValidatingAccountingException("error on validating accounting month ["+monthKey+"]!!", accountingRow, errorArray);
				throw validatingAccountingException;
			}
		}
	}
}