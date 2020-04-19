package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.provider.AccoutingDataProvider;
import de.gravitex.accounting.provider.IAccoutingDataProvider;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class AccountingSingleton {
	
	private static final Logger logger = Logger.getLogger(AccountingSingleton.class);

	private static AccountingSingleton instance;

	private AccountingManager accountingManager;
	
	private IAccoutingDataProvider accoutingDataProvider = new AccoutingDataProvider();

	private AccountingSingleton() {
		initialize();
	}

	public void initialize() {
		AccountingData data = new AccountingData();
		HashMap<MonthKey, List<AccountingRow>> fileData = accoutingDataProvider.readAccountingData();
		for (MonthKey key : fileData.keySet()) {
			data.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
		accountingManager = new AccountingManager().withAccountingData(data)
				.withBudgetPlannings(accoutingDataProvider.readBudgetPlannings())
				.withPaymentModalitys(accoutingDataProvider.readPaymentModalitys())
				.withSettings(AccountManagerSettings.fromValues(true, 24, true, true))
				.withIncome(accoutingDataProvider.readIncome());
	}

	public static AccountingSingleton getInstance() {
		if (instance == null) {
			instance = new AccountingSingleton();
		}
		return instance;
	}
	
	public void printCategory(String category) {
		List<AccountingRow> resultsByCategory = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getAccountingData().getAccountingMonths()) {
			resultsByCategory.addAll(month.getRowObjectsByCategory(category));
		}
		for (AccountingRow accountingRow : resultsByCategory) {
			logger.info(accountingRow);
		}
	}

	public void saldoCheck() {

		logger.info(" --------------------- SALDO CHECK --------------------- ");

		List<AccountingRow> results = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getAccountingData().getAccountingMonths()) {
			results.addAll(month.getRowObjects());
		}
		Collections.sort(results);
		BigDecimal referenceSaldo = null;
		for (AccountingRow accountingRow : results) {
			if (referenceSaldo == null && accountingRow.getSaldo() != null) {
				referenceSaldo = accountingRow.getSaldo();
				logger.info("setting reference saldo to: " + referenceSaldo);
			} else {
				if (referenceSaldo != null) {
					referenceSaldo = referenceSaldo.add(accountingRow.getAmount());
					if (accountingRow.getSaldo() != null) {
						if (!(accountingRow.getSaldo().equals(referenceSaldo))) {
							throw new AccountingException(
									"invalid reference saldo [reference value: " + referenceSaldo + " <-> row value: "
											+ accountingRow.getSaldo() + "]!!",
									AccountingError.INVALID_SALDO_REF, accountingRow);
						}
						logger.info(
								" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: " + referenceSaldo
										+ " [CHECK against row saldo '" + accountingRow.getSaldo() + "'] --> OK!!");
					} else {
						logger.info(" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: "
								+ referenceSaldo);
					}
				}
			}
		}
		logger.info("saldo check ok...");
	}

	public HashMap<String, BigDecimal> getCategorySums(MonthKey monthKey) {
		AccountingMonth monthData = accountingManager.getAccountingData().get(monthKey);
		if (monthData == null) {
			return null;
		}
		HashMap<String, BigDecimal> categorySums = new HashMap<String, BigDecimal>();
		for (String category : monthData.getDistinctCategories()) {
			BigDecimal categorySum = new BigDecimal(0);
			for (AccountingRow accountingRow : monthData.getRowObjectsByCategory(category)) {
				categorySum = categorySum.add(accountingRow.getAmount());
			}
			categorySums.put(category, categorySum);
		}
		return categorySums;
	}

	public List<AccountingRow> getAllEntriesForCategory(String category, AccountingData accountingData) {
		return accountingManager.getAllEntriesForCategory(category);
	}

	public List<AccountingRow> getAllEntriesForPartner(String partner, AccountingData accountingData) {
		return accountingManager.getAllEntriesForPartner(partner);
	}

	public List<AccountingRow> getAllEntries() {
		return accountingManager.getAllEntries();
	}

	public List<AccountingRow> getFilteredEntries() {
		return accountingManager.getFilteredEntries();
	}
}