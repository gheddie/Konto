package de.gravitex.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.gravitex.accounting.dao.AccountingDao;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.FixedPeriodIncomingPaymentModality;
import de.gravitex.accounting.modality.FixedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.modality.UndefinedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingSingleton {

	private static final String FILE = "C:\\work\\eclipseWorkspaces\\2019\\konto2\\accounting-excel\\src\\main\\resources\\Konto.xlsx";

	private static final String RESOURCE_PLANNING_FOLDER = "rp";

	private static final String MODALITIES_PROPERTIES = "modalities.properties";

	public static final String UNDEFINED_CATEGORY = "Undefiniert";

	private static final int COL_RUNNING_INDEX = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_PARTNER = 4;
	private static final int COL_TEXT = 5;
	private static final int COL_ALARM = 6;

	private static List<String> header;

	private static AccountingSingleton instance;

	private AccountManagerSettings accountManagerSettings = AccountManagerSettings.fromValues(true, 24, true);

	private AccountingManager accountingManager;

	private AccountingSingleton() {
		initialize();
	}

	public void initialize() {
		AccountingData data = new AccountingData();
		HashMap<MonthKey, List<AccountingRow>> fileData = readFileData();
		for (MonthKey key : fileData.keySet()) {
			data.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
		accountingManager = new AccountingManager().withAccountingData(data).withBudgetPlannings(readBudgetPlannings())
				.withPaymentModalitys(readPaymentModalitys()).withSettings(AccountManagerSettings.fromValues(true, 24, true));
	}

	public static AccountingSingleton getInstance() {
		if (instance == null) {
			instance = new AccountingSingleton();
		}
		return instance;
	}

	private HashMap<String, PaymentModality> readPaymentModalitys() {
		Properties prop = new Properties();
		HashMap<String, PaymentModality> result = new HashMap<String, PaymentModality>();
		try {
			prop.load(AccountingSingleton.class.getClassLoader().getResourceAsStream(MODALITIES_PROPERTIES));
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
		for (File resourcePlanningFile : getResourceFolderFiles(RESOURCE_PLANNING_FOLDER)) {
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

	private static HashMap<MonthKey, List<AccountingRow>> readFileData() {
		try {
			File file = new File(FILE);
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			BigDecimal completeAmount = new BigDecimal(0);
			HashMap<MonthKey, List<AccountingRow>> fileRows = new HashMap<MonthKey, List<AccountingRow>>();
			while (itr.hasNext()) {
				Row row = itr.next();
				if (row.getRowNum() > 0) {
					AccountingRow accountingRow = readLine(row);
					if (fileRows.get(MonthKey.fromDate(accountingRow.getDate())) == null) {
						fileRows.put(MonthKey.fromDate(accountingRow.getDate()), new ArrayList<AccountingRow>());
					}
					fileRows.get(MonthKey.fromDate(accountingRow.getDate())).add(accountingRow);
					completeAmount = completeAmount.add(accountingRow.getAmount());
				} else {
					header = readHeaderRow(row);
				}
			}
			System.out.println("completeAmount: " + completeAmount);
			wb.close();
			return fileRows;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<String> readHeaderRow(Row row) {
		List<String> result = new ArrayList<String>();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			result.add(cell.getStringCellValue());
		}
		return result;
	}

	private static AccountingRow readLine(Row row) {

		AccountingRow rowObject = new AccountingRow();
		Iterator<Cell> cellIterator = row.cellIterator();
		rowObject.setCategory(resolveRowCategory(row));
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			int columnIndex = cell.getColumnIndex();
			switch (columnIndex) {
			case COL_RUNNING_INDEX:
				// 0
				rowObject.setRunningIndex(AccountingUtil.getCellValue(Integer.class, cell));
				break;
			case COL_DATUM:
				// 1
				rowObject.setDate(AccountingUtil.getCellValue(LocalDate.class, cell));
				break;
			case COL_BETRAG:
				// 2
				rowObject.setAmount(AccountingUtil.getCellValue(BigDecimal.class, cell));
				break;
			case COL_SALDO:
				// 3
				rowObject.setSaldo(AccountingUtil.getCellValue(BigDecimal.class, cell));
				break;
			case COL_PARTNER:
				// 4
				rowObject.setPartner(AccountingUtil.getCellValue(String.class, cell));
				break;
			case COL_TEXT:
				// 5
				rowObject.setText(AccountingUtil.getCellValue(String.class, cell));
				break;
			case COL_ALARM:
				// 6
				rowObject.setAlarm(AccountingUtil.getCellValue(String.class, cell));
				break;
			}
		}
		AccountingError error = rowObject.getError();
		if (error != null) {
			throw new AccountingException("row [" + rowObject + "] is not valid!!", error, rowObject);
		}
		return rowObject;
	}

	private static String resolveRowCategory(Row accountingRow) {
		List<String> categorys = new ArrayList<String>();
		Iterator<Cell> cellIterator = accountingRow.cellIterator();
		Cell cell = null;
		while (cellIterator.hasNext()) {
			cell = cellIterator.next();
			if (cell.getColumnIndex() > COL_TEXT) {
				if (AccountingUtil.getCellValue(Boolean.class, cell)) {
					categorys.add(header.get(cell.getColumnIndex()));
				}
			}
		}
		if (categorys.size() == 0) {
			throw new AccountingException("no category found for row!!", AccountingError.NO_CATEGORY, null);
		}
		if (categorys.size() > 1) {
			throw new AccountingException("more than one category found for row!!", AccountingError.MULTIPLE_CATEGORIES,
					null);
		}
		return categorys.get(0);
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

	public Integer requestLimit(MonthKey monthKey, String category) {
		if (monthKey == null || category == null) {
			throw new AccountingException("request limit --> both month key and category must be set!!", null, null);
		}
		Properties properties = accountingManager.getBudgetPlannings().get(monthKey).getProperties();
		if (properties == null) {
			throw new AccountingException(
					"request limit --> no budget planning available for month key [" + monthKey + "]!!", null, null);
		}
		Object entry = properties.get(category);
		if (entry == null) {
			return null;
			/*
			 * throw new
			 * AccountingException("request limit --> no budget planning available for category ["
			 * + category + "] in month key [" + monthKey + "]!!", null, null);
			 */
		}
		String value = String.valueOf(properties.get(category));
		if (value == null || value.length() == 0) {
			throw new AccountingException("request limit --> no value set for budget planning for month [" + monthKey
					+ "] available and category [" + category + "]!!", null, null);
		}
		int limit = 0;
		try {
			limit = Integer.parseInt(String.valueOf(value));
		} catch (Exception e) {
			throw new AccountingException(
					"request limit --> unparsable numeric value [" + value + "] set for budget planning for month ["
							+ monthKey + "] available and category [" + category + "]!!",
					null, null);
		}
		return limit;
	}

	public AccountingResultMonthModel getAccountingResultMonthModel(MonthKey monthKey) {
		AccountingMonth accountingMonth = accountingManager.getAccountingData().get(monthKey);
		AccountingResultMonthModel result = new AccountingResultMonthModel();
		result.setMonthKey(monthKey);
		for (String category : accountingMonth.getDistinctCategories()) {
			result.addCategoryModel(getAccountingResultCategoryModel(monthKey, category));
		}
		return result;
	}

	public AccountingResultCategoryModel getAccountingResultCategoryModel(MonthKey monthKey, String category) {
		AccountingMonth accountingMonth = accountingManager.getAccountingData().get(monthKey);
		List<AccountingRow> rowsByCategory = accountingMonth.getRowObjectsByCategory(category);
		AccountingResultCategoryModel categoryModel = new AccountingResultCategoryModel();
		categoryModel.setMonthKey(monthKey);
		categoryModel.setCategory(category);
		List<AccountingResultModelRow> accountingResultModelRows = new ArrayList<AccountingResultModelRow>();
		BigDecimal sum = new BigDecimal(0);
		for (AccountingRow accountingRow : rowsByCategory) {
			accountingResultModelRows.add(AccountingResultModelRow.fromValues(accountingRow.getRunningIndex(),
					accountingRow.getAmount(), accountingRow.getDate(), accountingRow.getText()));
			sum = sum.add(accountingRow.getAmount());
		}
		categoryModel.setAccountingResultModelRows(accountingResultModelRows);
		categoryModel.setSum(sum);
		// initPaymentModality(monthKey, category);
		Integer limit = requestLimit(monthKey, category);
		categoryModel.setBudget(limit != null ? new BigDecimal(limit) : null);
		return categoryModel;
	}

	public AccountManagerSettings getAccountManagerSettings() {
		return accountManagerSettings;
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