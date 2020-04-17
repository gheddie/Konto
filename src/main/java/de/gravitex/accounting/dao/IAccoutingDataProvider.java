package de.gravitex.accounting.dao;

import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;

public interface IAccoutingDataProvider {

	HashMap<MonthKey, List<AccountingRow>> readAccountingData();
}