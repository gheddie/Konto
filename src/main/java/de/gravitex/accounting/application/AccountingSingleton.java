package de.gravitex.accounting.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingMonth;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class AccountingSingleton {
	
	private static final Logger logger = Logger.getLogger(AccountingSingleton.class);

	// productive
	public static final String ACCOUNTING_KEY_VB = "VB";
	
	// test
	public static final String ACCOUNTING_KEY_VISA = "VISA";

	private static AccountingSingleton instance;

	private AccountingManager accountingManager;
	
	private AccountingSingleton() {
		accountingManager = new AccountingLoader().startUp(ACCOUNTING_KEY_VB);
	}

	public static AccountingSingleton getInstance() {
		if (instance == null) {
			instance = new AccountingSingleton();
		}
		return instance;
	}
	
	public void printCategory(String category) {
		List<AccountingRow> resultsByCategory = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getMainAccount().getAccountingMonths()) {
			resultsByCategory.addAll(month.getRowObjectsByCategory(category));
		}
		for (AccountingRow accountingRow : resultsByCategory) {
			logger.info(accountingRow);
		}
	}

	public void saldoCheck() {

		logger.info(" --------------------- SALDO CHECK --------------------- ");

		List<AccountingRow> results = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getMainAccount().getAccountingMonths()) {
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
							throw new GenericAccountingException(
									"invalid reference saldo [reference value: " + referenceSaldo + " <-> row value: "
											+ accountingRow.getSaldo() + "]!!",
									accountingRow, AccountingError.INVALID_SALDO_REF);
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
		AccountingMonth monthData = accountingManager.getMainAccount().get(monthKey);
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

	public List<AccountingRow> getFilteredEntries() {
		return accountingManager.getFilteredEntries();
	}
}