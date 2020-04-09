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

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.FixedPeriodIncomingPaymentModality;
import de.gravitex.accounting.modality.FixedPeriodPaymentOutgoingModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.modality.PaymentPeriod;
import de.gravitex.accounting.modality.UndefinedPeriodOutgoingPaymentModality;
import lombok.Data;

@Data
public class AccountingManager {

	private static final String FILE = "C:\\work\\eclipseWorkspaces\\2019\\konto2\\accounting-excel\\src\\main\\resources\\Konto.xlsx";
	
	private static final String RESOURCE_PLANNING_FOLDER = "rp";

	private static final int COL_RUNNING_INDEX = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_TEXT = 4;
	
	private static final HashMap<AccountingCategory, PaymentModality> paymentModalityDefinitions = new HashMap<AccountingCategory, PaymentModality>();
	static {
		paymentModalityDefinitions.put(AccountingCategory.Auto, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Undefiniert, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Kreditkarte, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Paypal, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Nebenkosten, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Nahverkehr, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Telekommunikation, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Essen, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Sonstiges, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Abo, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Lebensversicherung, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.HALF_YEAR));
		paymentModalityDefinitions.put(AccountingCategory.Miete, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Unterhalt, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Benzin, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Fahrrad, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Charity, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.QUARTER));
		paymentModalityDefinitions.put(AccountingCategory.Musik, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Einrichtung, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.AbhebungEC, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Kippen, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Fitnessstudio, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Rundfunk, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.YEAR));
		paymentModalityDefinitions.put(AccountingCategory.Krankengeld, new FixedPeriodIncomingPaymentModality(PaymentPeriod.MONTH));
	}

	private static List<String> header;

	private static HashMap<String, Properties> budgetPlannings = new HashMap<String, Properties>();

	private static AccountingManager instance;

	// hashmap month to rows...
	private HashMap<String, AccountingMonth> result;
	
	private AccountingManager() {
		result = new HashMap<String, AccountingMonth>();
		HashMap<String, List<AccountingRow>> fileData = readFileData();
		readBudgetPlannings();
		for (String key : fileData.keySet()) {
			result.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
	}

	public void printAll(boolean showObjects) {
		for (String monthKey : result.keySet()) {
			result.get(monthKey).print(showObjects);
		}
	}

	public void printMonth(String monthKey, boolean showObjects) {
		result.get(monthKey).print(showObjects);
	}

	public static AccountingManager getInstance() {
		if (instance == null) {
			instance = new AccountingManager();
		}
		return instance;
		
		// ---
	}

	private static void readBudgetPlannings() {
		for (File resourcePlanningFile : getResourceFolderFiles(RESOURCE_PLANNING_FOLDER)) {
			System.out.println("reading resource planning: " + resourcePlanningFile.getName());
			Properties budgetPlanningForMonth = new Properties();
			try {
				budgetPlanningForMonth.load(new FileInputStream(resourcePlanningFile.getAbsolutePath()));
				applyBudgetPlanningForMonth(budgetPlanningForMonth, resourcePlanningFile.getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void applyBudgetPlanningForMonth(Properties budgetPlanningForMonth, String fileName) {
		String[] spl = FilenameUtils.removeExtension(fileName).split("_");
		String monthKey = AccountingUtil.getMonthKey(Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
		budgetPlannings.put(monthKey, budgetPlanningForMonth);
	}

	private static File[] getResourceFolderFiles(String folder) {
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    URL url = loader.getResource(folder);
	    String path = url.getPath();
	    File[] result = new File(path).listFiles();
		return result;
	}

	private static HashMap<String, List<AccountingRow>> readFileData() {
		try {
			File file = new File(FILE);
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			BigDecimal completeAmount = new BigDecimal(0);
			HashMap<String, List<AccountingRow>> fileRows = new HashMap<String, List<AccountingRow>>();
			while (itr.hasNext()) {
				Row row = itr.next();
				if (row.getRowNum() > 0) {
					AccountingRow accountingRow = readLine(row);
					if (fileRows.get(AccountingUtil.getMonthKey(accountingRow.getDate())) == null) {
						fileRows.put(AccountingUtil.getMonthKey(accountingRow.getDate()), new ArrayList<AccountingRow>());
					}
					fileRows.get(AccountingUtil.getMonthKey(accountingRow.getDate())).add(accountingRow);
					completeAmount = completeAmount.add(accountingRow.getAmount());
				} else {
					readHeaderRow(row);
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

	private static void readHeaderRow(Row row) {
		List<String> result = new ArrayList<String>();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			result.add(cell.getStringCellValue());
		}
		header = result;
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
				rowObject.setRunningIndex(AccountingUtil.getCellValue(Integer.class, cell));
				break;
			case COL_DATUM:
				rowObject.setDate(AccountingUtil.getCellValue(LocalDate.class, cell));
				break;
			case COL_BETRAG:
				rowObject.setAmount(AccountingUtil.getCellValue(BigDecimal.class, cell));
				break;
			case COL_SALDO:
				rowObject.setSaldo(AccountingUtil.getCellValue(BigDecimal.class, cell));
				break;
			case COL_TEXT:
				rowObject.setText(AccountingUtil.getCellValue(String.class, cell));
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
		for (AccountingMonth month : result.values()) {
			resultsByCategory.addAll(month.getRowObjectsByCategory(category));
		}
		for (AccountingRow accountingRow : resultsByCategory) {
			System.out.println(accountingRow);
		}
	}

	public void saldoCheck() {
		
		System.out.println(" --------------------- SALDO CHECK --------------------- ");
		
		List<AccountingRow> results = new ArrayList<AccountingRow>();
		for (AccountingMonth month : result.values()) {
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
						System.out.println(" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: "
								+ referenceSaldo + " [CHECK against row saldo '" + accountingRow.getSaldo() + "'] --> OK!!");
					} else {
						System.out.println(
								" ---> altered ref [diff:" + accountingRow.getAmount() + "] saldo to: " + referenceSaldo);
					}
				}
			}
		}
		System.out.println("saldo check ok...");
	}

	public int requestLimit(String monthKey, String category) {
		if (monthKey == null || category == null) {
			throw new AccountingException("request limit --> both month key and category must be set!!", null, null);
		}
		Properties properties = budgetPlannings.get(monthKey);
		if (properties == null) {
			throw new AccountingException("request limit --> no budget planning available for month key ["+monthKey+"]!!", null, null);
		}
		Object entry = properties.get(category);
		if (entry == null) {
			throw new AccountingException("request limit --> no budget planning available for category ["+category+"] in month key ["+monthKey+"]!!", null, null);
		}
		String value = String.valueOf(properties.get(category));
		if (value == null || value.length() == 0) {
			throw new AccountingException("request limit --> no value set for budget planning for month ["+monthKey+"] available and category ["+category+"]!!", null, null);
		}
		int limit = 0;
		try {
			limit = Integer.parseInt(String.valueOf(value));			
		} catch (Exception e) {
			throw new AccountingException("request limit --> unparsable numeric value ["+value+"] set for budget planning for month ["+monthKey+"] available and category ["+category+"]!!", null, null);
		}
		return limit;
	}

	public PaymentModality getPaymentModality(String categoryKey) {
		PaymentModality paymentModality = paymentModalityDefinitions.get(AccountingCategory.valueOf(categoryKey));
		if (paymentModality == null) {
			throw new AccountingException("no payment modality found for category '" + categoryKey + "'!!",
					AccountingError.NO_PM_FOR_CATEGORY, null);
		}
		return paymentModality;
	}
}