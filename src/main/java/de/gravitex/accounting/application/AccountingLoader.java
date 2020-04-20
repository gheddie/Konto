package de.gravitex.accounting.application;

import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.BudgetPlanning;
import de.gravitex.accounting.Income;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.provider.AccoutingDataProvider;
import de.gravitex.accounting.provider.IAccoutingDataProvider;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;

public class AccountingLoader {
	
	private IAccoutingDataProvider accoutingDataProvider = new AccoutingDataProvider();

	public AccountingManager startUp(String accountingKey) {
		
		AccountingData data = new AccountingData();
		
		HashMap<MonthKey, List<AccountingRow>> fileData = readAccountingData(accountingKey);

		for (MonthKey key : fileData.keySet()) {
			data.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
		
		HashMap<MonthKey, BudgetPlanning> budgetPlannings = readBudgetPlannings(accountingKey);
		HashMap<String, PaymentModality> paymentModalitys = readPaymentModalitys(accountingKey);
		Income income = readIncome(accountingKey);
		
		AccountingManager accountingManager = new AccountingManager().withAccountingData(accountingKey, data)
				.withBudgetPlannings(budgetPlannings)
				.withPaymentModalitys(paymentModalitys)
				.withSettings(AccountManagerSettings.fromValues(true, 24, true, true))
				.withIncome(income);
		
		return accountingManager;
	}

	private Income readIncome(String accountingKey) {
		try {
			Income income = accoutingDataProvider.readIncome(accountingKey);
			return income;	
		} catch (Exception e) {
			throw new AccountingException("Einkommen konnten nicht gelesen werden!!", AccountingError.NO_DATA_READ, null);
		}
	}

	private HashMap<String, PaymentModality> readPaymentModalitys(String accountingKey) {
		try {
			HashMap<String, PaymentModality> paymentModalitys = accoutingDataProvider.readPaymentModalitys(accountingKey);
			return paymentModalitys;	
		} catch (Exception e) {
			throw new AccountingException("Zahlungstypen konnten nicht gelesen werden!!", AccountingError.NO_DATA_READ, null);
		}
	}

	private HashMap<MonthKey, BudgetPlanning> readBudgetPlannings(String accountingKey) {
		try {
			HashMap<MonthKey, BudgetPlanning> budgetPlannings = accoutingDataProvider.readBudgetPlannings(accountingKey);
			return budgetPlannings;	
		} catch (Exception e) {
			throw new AccountingException("Budgets konnten nicht gelesen werden!!", AccountingError.NO_DATA_READ, null);
		}
	}

	private HashMap<MonthKey, List<AccountingRow>> readAccountingData(String accountingKey) {
		try {
			HashMap<MonthKey, List<AccountingRow>> fileData = accoutingDataProvider.readAccountingData(accountingKey);
			return fileData;	
		} catch (Exception e) {
			throw new AccountingException("Buchungen konnten nicht gelesen werden!!", AccountingError.NO_DATA_READ, null);
		}
	}
}