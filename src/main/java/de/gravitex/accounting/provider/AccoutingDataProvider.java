package de.gravitex.accounting.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.BudgetPlanning;
import de.gravitex.accounting.Income;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.io.ResourceFileReader;
import de.gravitex.accounting.modality.FixedPeriodPaymentModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.modality.UndefinedPeriodPaymentModality;
import de.gravitex.accounting.util.MonthKey;

public class AccoutingDataProvider implements IAccoutingDataProvider {
	
	private static final int COL_RUNNING_INDEX = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_PARTNER = 4;
	private static final int COL_TEXT = 5;
	private static final int COL_MAIN_ACCOUT = 6;
	private static final int COL_MAIN_ACCOUT_REF = 7;
	private static final int COL_VALID_FROM = 8;
	private static final int COL_VALID_UNTIL = 9;
	private static final int COL_ALARM = 10;

	private static List<String> header;

	private static final Logger logger = Logger.getLogger(AccountingData.class);
	
	private static final String BOOKING_FILE_NAME = "Buchungen.xlsx";

	@Override
	public HashMap<MonthKey, List<AccountingRow>> readAccountingData(String accountingKey) {
		try {
			File file = ResourceFileReader.getResourceFile(accountingKey, BOOKING_FILE_NAME);
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			BigDecimal completeAmount = new BigDecimal(0);
			HashMap<MonthKey, List<AccountingRow>> fileRows = new HashMap<MonthKey, List<AccountingRow>>();
			while (itr.hasNext()) {
				Row row = itr.next();
				if (row.getRowNum() > 0) {
					AccountingRow accountingRow = readRow(row);
					if (fileRows.get(MonthKey.fromDate(accountingRow.getDate())) == null) {
						fileRows.put(MonthKey.fromDate(accountingRow.getDate()),
								new ArrayList<AccountingRow>());
					}
					fileRows.get(MonthKey.fromDate(accountingRow.getDate())).add(accountingRow);
					completeAmount = completeAmount.add(accountingRow.getAmount());
				} else {
					readHeaderRow(row);
				}
			}
			logger.info("completeAmount: " + completeAmount);
			wb.close();
			return fileRows;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static AccountingRow readRow(Row row) {

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
			case COL_MAIN_ACCOUT:
				// 6
				rowObject.setMainAccount(AccountingUtil.getCellValue(String.class, cell));
				break;
			case COL_MAIN_ACCOUT_REF:
				// 7
				rowObject.setMainAccountReference(AccountingUtil.getCellValue(String.class, cell));
				break;
			case COL_VALID_FROM:
				// 8
				rowObject.setValidFrom(AccountingUtil.getCellValue(LocalDate.class, cell));
				break;
			case COL_VALID_UNTIL:
				// 9
				rowObject.setValidUntil(AccountingUtil.getCellValue(LocalDate.class, cell));
				break;
			case COL_ALARM:
				// 10
				rowObject.setAlarm(AccountingUtil.getCellValue(Boolean.class, cell));
				break;
			}
		}
		/*
		AccountingError error = rowObject.getError();
		if (error != null) {
			throw new AccountingException("row [" + rowObject + "] is not valid!!", rowObject, error);
		}
		*/
		return rowObject;
	}
	
	private static String resolveRowCategory(Row accountingRow) {
		
		List<String> categorys = new ArrayList<String>();
		Iterator<Cell> cellIterator = accountingRow.cellIterator();
		Cell cell = null;
		while (cellIterator.hasNext()) {
			cell = cellIterator.next();
			if (cell.getColumnIndex() > COL_ALARM) {
				if (AccountingUtil.getCellValue(Boolean.class, cell)) {
					categorys.add(header.get(cell.getColumnIndex()));
				}
			}
		}
		if (categorys.size() == 0) {
			throw new GenericAccountingException("no category found for row!!", null, AccountingError.NO_CATEGORY);
		}
		if (categorys.size() > 1) {
			throw new GenericAccountingException("more than one category found for row!!", null,
					AccountingError.MULTIPLE_CATEGORIES);
		}
		return categorys.get(0);
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

	@Override
	public Income readIncome() throws IOException {
		
		Properties prop = ResourceFileReader.getProperties(null, IAccoutingDataProvider.INCOME_PROPERTIES);
		return Income.fromValues(prop);
	}

	@Override
	public HashMap<String, PaymentModality> readPaymentModalitys(String accountingKey) throws IOException {
		
		HashMap<String, PaymentModality> result = new HashMap<String, PaymentModality>();
		Properties prop = ResourceFileReader.getProperties(accountingKey, IAccoutingDataProvider.MODALITIES_PROPERTIES);
		String key = null;
		for (Object keyValue : prop.keySet()) {
			key = String.valueOf(keyValue);
			logger.info(keyValue + " ---> " + prop.getProperty(key));
			createCategory(key, prop.getProperty(key), result);
		}
		return result;
	}

	@Override
	public HashMap<MonthKey, BudgetPlanning> readBudgetPlannings(String accountingKey) throws FileNotFoundException, IOException {
		
		HashMap<MonthKey, BudgetPlanning> result = new HashMap<MonthKey, BudgetPlanning>();
		for (File resourcePlanningFile : ResourceFileReader.getResourceFiles(accountingKey, IAccoutingDataProvider.RESOURCE_PLANNING_FOLDER)) {
			logger.info("reading resource planning: " + resourcePlanningFile.getName());
			Properties budgetPlanningForMonth = new Properties();
			budgetPlanningForMonth.load(new FileInputStream(resourcePlanningFile.getAbsolutePath()));
			String[] spl = FilenameUtils.removeExtension(resourcePlanningFile.getName()).split("_");
			MonthKey monthKey = MonthKey.fromValues(Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
			result.put(monthKey, BudgetPlanning.fromValues(budgetPlanningForMonth));
		}
		return result;
	}
	
	private void createCategory(String categoryKey, String paymentInfo,
			HashMap<String, PaymentModality> aPaymentModalitys) {
		String[] spl = paymentInfo.split("#");
		PaymentType paymentType = PaymentType.valueOf(spl[0]);
		PaymentPeriod paymentPeriod = PaymentPeriod.valueOf(spl[1]);
		if (paymentPeriod.equals(PaymentPeriod.UNDEFINED)) {
			aPaymentModalitys.put(categoryKey, new UndefinedPeriodPaymentModality(paymentType));
		} else {
			aPaymentModalitys.put(categoryKey, new FixedPeriodPaymentModality(paymentPeriod, paymentType));
		}
	}
}