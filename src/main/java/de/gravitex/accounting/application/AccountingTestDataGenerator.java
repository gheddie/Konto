package de.gravitex.accounting.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;

public class AccountingTestDataGenerator {

	private static final String CAT_A = "CAT_A";

	public static AccountingData generateAccountingTestData() {
		
		AccountingData result = new AccountingData();

		MonthKey monthKey = MonthKey.fromValues(1, 1994);
		AccountingMonth accountingMonth = AccountingMonth.fromValues(monthKey, getRowObjects());
		result.put(monthKey, accountingMonth);
		
		return result;
	}

	private static List<AccountingRow> getRowObjects() {
		
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		
		result.add(getAccountingRow());
		
		return result;
	}

	private static AccountingRow getAccountingRow() {
		
		AccountingRow accountingRow = new AccountingRow();
		
		// set values
		accountingRow.setRunningIndex(1);
		accountingRow.setDate(LocalDate.of(1994, 1, 1));
		accountingRow.setCategory(CAT_A);
		accountingRow.setAmount(new BigDecimal(14));
		
		return accountingRow;
	}
}