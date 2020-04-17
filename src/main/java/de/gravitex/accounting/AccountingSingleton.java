package de.gravitex.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import de.gravitex.accounting.dao.AccountingDao;
import de.gravitex.accounting.dao.AccoutingDataProvider;
import de.gravitex.accounting.dao.IAccoutingDataProvider;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.FixedPeriodIncomingPaymentModality;
import de.gravitex.accounting.modality.FixedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.modality.UndefinedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingSingleton {

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
		accountingManager = new AccountingManager().withAccountingData(data).withBudgetPlannings(readBudgetPlannings())
				.withPaymentModalitys(readPaymentModalitys()).withSettings(AccountManagerSettings.fromValues(true, 24, true, true)).withIncome(readIncome());
	}

	public static AccountingSingleton getInstance() {
		if (instance == null) {
			instance = new AccountingSingleton();
		}
		return instance;
	}
	
	private Income readIncome() {
		Properties prop = new Properties();
		try {
			prop.load(AccountingSingleton.class.getClassLoader().getResourceAsStream(IAccoutingDataProvider.INCOME_PROPERTIES));
			return Income.fromValues(prop);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private HashMap<String, PaymentModality> readPaymentModalitys() {
		Properties prop = new Properties();
		HashMap<String, PaymentModality> result = new HashMap<String, PaymentModality>();
		try {
			prop.load(AccountingSingleton.class.getClassLoader().getResourceAsStream(IAccoutingDataProvider.MODALITIES_PROPERTIES));
			String key = null;
			for (Object keyValue : prop.keySet()) {
				key = String.valueOf(keyValue);
				System.out.println(keyValue + " ---> " + prop.getProperty(key));
				createCategory(key, prop.getProperty(key), result);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private void createCategory(String categoryKey, String paymentInfo,
			HashMap<String, PaymentModality> aPaymentModalitys) {
		String[] spl = paymentInfo.split("#");
		PaymentPeriod paymentPeriod = PaymentPeriod.valueOf(spl[1]);
		switch (PaymentType.valueOf(spl[0])) {
		case INCOMING:
			aPaymentModalitys.put(categoryKey, new FixedPeriodIncomingPaymentModality(paymentPeriod));
			break;
		case OUTGOING:
			if (paymentPeriod.equals(paymentPeriod.UNDEFINED)) {
				aPaymentModalitys.put(categoryKey, new UndefinedPeriodOutgoingPaymentModality());
			} else {
				aPaymentModalitys.put(categoryKey, new FixedPeriodOutgoingPaymentModality(paymentPeriod));
			}
			break;
		}
	}

	private HashMap<MonthKey, BudgetPlanning> readBudgetPlannings() {
		HashMap<MonthKey, BudgetPlanning> result = new HashMap<MonthKey, BudgetPlanning>();
		for (File resourcePlanningFile : getResourceFolderFiles(IAccoutingDataProvider.RESOURCE_PLANNING_FOLDER)) {
			System.out.println("reading resource planning: " + resourcePlanningFile.getName());
			Properties budgetPlanningForMonth = new Properties();
			try {
				budgetPlanningForMonth.load(new FileInputStream(resourcePlanningFile.getAbsolutePath()));
				String[] spl = FilenameUtils.removeExtension(resourcePlanningFile.getName()).split("_");
				MonthKey monthKey = MonthKey.fromValues(Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
				result.put(monthKey, BudgetPlanning.fromValues(budgetPlanningForMonth));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	private static File[] getResourceFolderFiles(String folder) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(folder);
		String path = url.getPath();
		File[] result = new File(path).listFiles();
		return result;
	}

	public void printCategory(String category) {
		List<AccountingRow> resultsByCategory = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getAccountingData().getAccountingMonths()) {
			resultsByCategory.addAll(month.getRowObjectsByCategory(category));
		}
		for (AccountingRow accountingRow : resultsByCategory) {
			System.out.println(accountingRow);
		}
	}

	public void saldoCheck() {

		System.out.println(" --------------------- SALDO CHECK --------------------- ");

		List<AccountingRow> results = new ArrayList<AccountingRow>();
		for (AccountingMonth month : accountingManager.getAccountingData().getAccountingMonths()) {
			results.addAll(month.getRowObjects());
		}
		Collections.sort(results);
		BigDecimal referenceSaldo = null;
		for (AccountingRow accountingRow : results) {
			if (referenceSaldo == null && accountingRow.getSaldo() != null) {
				referenceSaldo = accountingRow.getSaldo();
				System.out.println("setting reference saldo to: " + referenceSaldo);
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
						System.out.println(
								" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: " + referenceSaldo
										+ " [CHECK against row saldo '" + accountingRow.getSaldo() + "'] --> OK!!");
					} else {
						System.out.println(" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: "
								+ referenceSaldo);
					}
				}
			}
		}
		System.out.println("saldo check ok...");
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

	public Set<String> getAllPartners(AccountingData accountingData) {
		return AccountingDao.getAllPartners(accountingData);
	}

	public Set<Category> getAllCategories(AccountingData accountingData) {
		return AccountingDao.getAllCategories(accountingData, accountingManager);
	}

	public List<AccountingRow> getAllEntriesForCategory(String category, AccountingData accountingData) {
		return AccountingDao.getAllEntriesForCategory(accountingData, category);
	}

	public List<AccountingRow> getAllEntriesForPartner(String partner, AccountingData accountingData) {
		return AccountingDao.getAllEntriesForPartner(accountingData, partner);
	}

	public List<AccountingRow> getAllEntries() {
		return AccountingDao.getAllEntries(accountingManager.getAccountingData());
	}
}