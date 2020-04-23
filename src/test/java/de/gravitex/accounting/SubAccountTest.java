package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.gravitex.accounting.util.MonthKey;

public class SubAccountTest extends AccountingManagerDataTest {

	@Test
	public void testSubAccount() {

		AccountingManager accountingManager = new AccountingManager();
		HashMap<Integer, AccountingRow> accoutingRowsMain = createAccoutingRowsMain();
		AccountingData accountingDataMain = toAccountingData(accoutingRowsMain);
		accountingManager.setMainAccount(accountingDataMain);
		
		AccountingRow mainAccountingRow = accoutingRowsMain.get(1);
		accountingManager.checkSubEntries(mainAccountingRow);
	}

	private HashMap<Integer, AccountingRow> createAccoutingRowsMain() {
		
		HashMap<Integer, AccountingRow> accountingRowHash = new HashMap<Integer, AccountingRow>();
		addToHash(getAccountingRow(1, LocalDate.of(2020, 1, 13), "AAA", new BigDecimal(13)), accountingRowHash);
		return accountingRowHash;
	}
	
	private void addToHash(AccountingRow accountingRow, HashMap<Integer, AccountingRow> hash) {
		hash.put(accountingRow.getRunningIndex(), accountingRow);
	}

	private AccountingData toAccountingData(HashMap<Integer, AccountingRow> accoutingRows) {
		
		AccountingData accountingData = new AccountingData();
		HashMap<MonthKey, List<AccountingRow>> dividedRows = new HashMap<MonthKey, List<AccountingRow>>();
		MonthKey monthKey = null;
		for (AccountingRow accountingRow : accoutingRows.values()) {
			monthKey = MonthKey.fromDate(accountingRow.getDate());
			if (dividedRows.get(monthKey) == null) {
				dividedRows.put(monthKey, new ArrayList<AccountingRow>());
			}
			dividedRows.get(monthKey).add(accountingRow);
		}
		HashMap<MonthKey, AccountingMonth> months = new HashMap<MonthKey, AccountingMonth>();
		for (MonthKey key : dividedRows.keySet()) {
			months.put(monthKey, AccountingMonth.fromValues(key, dividedRows.get(monthKey)));
		}
		accountingData.setData(months);
		return accountingData;
	}
}