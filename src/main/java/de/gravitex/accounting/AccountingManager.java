package de.gravitex.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Data;

@Data
public class AccountingManager {

	private static final String FILE = "C:\\work\\eclipseWorkspaces\\2019\\konto2\\accounting-excel\\src\\main\\resources\\Konto.xlsx";

	private static final int COL_MONAT = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_TEXT = 4;

	private HashMap<String, AccountingMonth> result;

	private static AccountingManager fromData(HashMap<String, AccountingMonth> data) {
		AccountingManager accountingManager = new AccountingManager();
		accountingManager.setResult(data);
		return accountingManager;
	}
	
	public void printAll(boolean showObjects) {
		for (String monthKey : result.keySet()) {
			result.get(monthKey).print(showObjects);	
		}
	}

	public void printMonth(String monthKey, boolean showObjects) {
		result.get(monthKey).print(showObjects);
	}

	public static AccountingManager instance() {

		HashMap<String, AccountingMonth> result = new HashMap<String, AccountingMonth>();
		HashMap<String, List<AccountingRow>> fileData = readFileData();
		if (fileData == null) {
			return null;
		}
		for (String key : fileData.keySet()) {
			result.put(key, AccountingMonth.fromValues(key, fileData.get(key)));
		}
		return AccountingManager.fromData(result);
	}

	private static HashMap<String, List<AccountingRow>> readFileData() {
		try {
			File file = new File(FILE);
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			BigDecimal completeAmount = new BigDecimal(0);
			List<String> header = null;
			HashMap<String, List<AccountingRow>> puh = new HashMap<String, List<AccountingRow>>();
			while (itr.hasNext()) {
				Row row = itr.next();
				if (row.getRowNum() > 0) {
					AccountingRow obj = readLine(row, sheet, header);
					if (puh.get(AccountingUtil.getMonthKey(obj.getDate())) == null) {
						puh.put(AccountingUtil.getMonthKey(obj.getDate()), new ArrayList<AccountingRow>());
					}
					puh.get(AccountingUtil.getMonthKey(obj.getDate())).add(obj);
					completeAmount = completeAmount.add(obj.getAmount());
				} else {
					header = getHeaderRow(row);
				}
			}
			System.out.println("completeAmount: " + completeAmount);
			wb.close();
			return puh;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<String> getHeaderRow(Row row) {
		List<String> result = new ArrayList<String>();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			result.add(cell.getStringCellValue());
		}
		return result;
	}

	private static AccountingRow readLine(Row row, XSSFSheet sheet, List<String> header) {

		AccountingRow rowObject = new AccountingRow();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			int columnIndex = cell.getColumnIndex();
			switch (columnIndex) {
			case COL_MONAT:
				rowObject.setMonth(cell.getStringCellValue());
				break;
			case COL_DATUM:
				rowObject.setDate(AccountingUtil.toLocalDate(cell.getDateCellValue()));
				break;
			case COL_BETRAG:
				rowObject.setAmount(AccountingUtil.getBigDecimal(cell));
				break;
			case COL_SALDO:
				rowObject.setSaldo(AccountingUtil.getBigDecimal(cell));
				break;
			case COL_TEXT:
				rowObject.setText(cell.getStringCellValue());
				break;
			default:
				// all yes/nos
				if (AccountingUtil.getBoolean(cell)) {
					rowObject.setCategory(resolveCategory(header, cell));
				}
				break;
			}
		}
		AccountingError error = rowObject.getError();
		if (error != null) {
			throw new AccountingException("row [" + rowObject + "] is not valid!!", error, rowObject);
		}
		return rowObject;
	}

	private static String resolveCategory(List<String> header, Cell cell) {
		return header.get(cell.getColumnIndex());
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
}