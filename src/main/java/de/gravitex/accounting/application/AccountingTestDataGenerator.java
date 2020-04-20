package de.gravitex.accounting.application;

import java.util.ArrayList;
import java.util.List;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;

public class AccountingTestDataGenerator {

	public static AccountingData generateAccountingTestData() {
		
		AccountingData result = new AccountingData();

		MonthKey monthKey = MonthKey.fromValues(1, 1994);
		AccountingMonth accountingMonth = AccountingMonth.fromValues(monthKey, getRowObjects());
		result.put(monthKey, accountingMonth);
		
		return result;
	}

	private static List<AccountingRow> getRowObjects() {
		
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		
		result.add(new AccountingRow());
		
		return result;
	}
}