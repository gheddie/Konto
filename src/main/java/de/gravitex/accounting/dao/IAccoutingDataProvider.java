package de.gravitex.accounting.dao;

import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.util.MonthKey;

public interface IAccoutingDataProvider {
	
	public static final String RESOURCE_PLANNING_FOLDER = "rp";

	public static final String MODALITIES_PROPERTIES = "modalities.properties";

	HashMap<MonthKey, List<AccountingRow>> readAccountingData();
}