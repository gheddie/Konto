package de.gravitex.accounting.dao;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.util.MonthKey;

public class AccoutingDataProvider implements IAccoutingDataProvider {
	
	private static final String FILE = "C:\\work\\eclipseWorkspaces\\2019\\konto2\\accounting-excel\\src\\main\\resources\\Konto.xlsx";
	
	private static final int COL_RUNNING_INDEX = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_PARTNER = 4;
	private static final int COL_TEXT = 5;
	private static final int COL_ALARM = 6;

	private static List<String> header;

	@Override
	public HashMap<MonthKey, List<AccountingRow>> readAccountingData() {
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
						fileRows.put(MonthKey.fromDate(accountingRow.getDate()),
								new ArrayList<AccountingRow>());
					}
					fileRows.get(MonthKey.fromDate(accountingRow.getDate())).add(accountingRow);
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
	
	private static void readHeaderRow(Row row) {
		
		List<String> result = new ArrayList<String>();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			result.add(cell.getStringCellValue());
		}
		header = result;
	}
}