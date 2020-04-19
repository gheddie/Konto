package de.gravitex.accounting.provider;

import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.BudgetPlanning;
import de.gravitex.accounting.Income;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.util.MonthKey;

public interface IAccoutingDataProvider {
	
	public static final String RESOURCE_PLANNING_FOLDER = "rp";

	public static final String MODALITIES_PROPERTIES = "modalities.properties";

	public static final String INCOME_PROPERTIES = "income.properties";

	HashMap<MonthKey, List<AccountingRow>> readAccountingData();
	
	Income readIncome();
	
	HashMap<String, PaymentModality> readPaymentModalitys();
	
	HashMap<MonthKey, BudgetPlanning> readBudgetPlannings();
}