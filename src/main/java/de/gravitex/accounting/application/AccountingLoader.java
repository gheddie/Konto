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
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.provider.AccoutingDataProvider;
import de.gravitex.accounting.provider.IAccoutingDataProvider;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;

public class AccountingLoader {
	
	private IAccoutingDataProvider accoutingDataProvider = new AccoutingDataProvider();
	
	public AccountingManager startUp(String mainAccountingKey) {
		
		Income income = readIncome();
		AccountingManager accountingManager = new AccountingManager();
		accountingManager.setMainAccount(loadAccountingData(mainAccountingKey, AccountingType.MAIN_ACCOUNT));
		accountingManager.withSettings(AccountManagerSettings.fromValues(true, true, true, 12)).withIncome(income);
		return accountingManager;
	}

	public AccountingData loadAccountingData(String accountingKey, AccountingType accountingType) {
		
		AccountingData accountingData = new AccountingData();
		
		HashMap<MonthKey, List<AccountingRow>> fileData = readAccountingData(accountingKey);
		for (MonthKey key : fileData.keySet()) {
			accountingData.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
		
		accountingData.setBudgetPlannings(readBudgetPlannings(accountingKey));
		accountingData.setPaymentModalitys(readPaymentModalitys(accountingKey));
		accountingData.setSubAccountReferences(readSubAccountReferences(accountingKey));
		
		accountingData.setAccountKey(accountingKey);
		accountingData.setAccountingType(accountingType);
		
		return accountingData;
	}

	private Income readIncome() {
		try {
			Income income = accoutingDataProvider.readIncome();
			return income;	
		} catch (Exception e) {
			throw new GenericAccountingException("Einkommen konnten nicht gelesen werden!!", null, AccountingError.NO_DATA_READ);
		}
	}
	
	private HashMap<String, String> readSubAccountReferences(String accountingKey) {
		try {
			HashMap<String, String> subAccountReferences = accoutingDataProvider.readSubAccountReferences(accountingKey);
			return subAccountReferences;	
		} catch (Exception e) {
			throw new GenericAccountingException("Unterkonto-Referenzen konnten nicht gelesen werden!!", null, AccountingError.NO_DATA_READ);
		}
	}

	private HashMap<String, PaymentModality> readPaymentModalitys(String accountingKey) {
		try {
			HashMap<String, PaymentModality> paymentModalitys = accoutingDataProvider.readPaymentModalitys(accountingKey);
			return paymentModalitys;	
		} catch (Exception e) {
			throw new GenericAccountingException("Zahlungstypen konnten nicht gelesen werden!!", null, AccountingError.NO_DATA_READ);
		}
	}

	private HashMap<MonthKey, BudgetPlanning> readBudgetPlannings(String accountingKey) {
		try {
			HashMap<MonthKey, BudgetPlanning> budgetPlannings = accoutingDataProvider.readBudgetPlannings(accountingKey);
			return budgetPlannings;	
		} catch (Exception e) {
			throw new GenericAccountingException("Budgets konnten nicht gelesen werden!!", null, AccountingError.NO_DATA_READ);
		}
	}

	private HashMap<MonthKey, List<AccountingRow>> readAccountingData(String accountingKey) {
		try {
			HashMap<MonthKey, List<AccountingRow>> fileData = accoutingDataProvider.readAccountingData(accountingKey);
			return fileData;	
		} catch (Exception e) {
			throw new GenericAccountingException("Buchungen konnten nicht gelesen werden!!", null, AccountingError.NO_DATA_READ);
		}
	}

	/*
	public AccountingLoader withMainAccount(AccountDefinition accountDefinition) {
		accountDefinitions.put(accountDefinition.getAccountKey(), accountDefinition);
		return this;
	}
	
	public AccountingLoader withSubAccount(AccountDefinition accountDefinition) {
		accountDefinitions.put(accountDefinition.getAccountKey(), accountDefinition);
		return this;
	}
	*/
}