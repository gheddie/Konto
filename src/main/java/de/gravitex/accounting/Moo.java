package de.gravitex.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Moo {

	private static final String FILE = "C:\\work\\eclipseWorkspaces\\2019\\konto2\\accounting-excel\\src\\main\\resources\\Konto.xlsx";

	private static final int COL_MONAT = 0;
	private static final int COL_DATUM = 1;
	private static final int COL_BETRAG = 2;
	private static final int COL_SALDO = 3;
	private static final int COL_TEXT = 4;

	public static void main(String[] args) {
		
		HashMap<String, AMonth> result = kruhpuhluh();
		
		/*
		for (AMonth aMonth : k.values()) {
			aMonth.printSorted();
		}
		*/
		
		result.get("1/2020").printSorted(true);
	}

	private static HashMap<String, AMonth> kruhpuhluh() {
		
		HashMap<String, AMonth> result = new HashMap<String, AMonth>();
		HashMap<String, List<RowObject>> zoo = zoo();
		for (String key : zoo.keySet()) {
			result.put(key, AMonth.fromValues(key, zoo.get(key)));
			/*
			System.out.println(" ------------------ " + key + " ------------------ ");
			for (RowObject obj : zoo.get(key)) {
				System.out.println(obj.getBETRAG() + " [" + obj.getWHAT() + "] (" + obj.getDATUM() + ") ["
						+ kuh(obj.getDATUM()) + "]");	
			}
			*/
		}
		return result;
	}

	private static HashMap<String, List<RowObject>> zoo() {
		try {
			File file = new File(FILE);
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> itr = sheet.iterator();
			BigDecimal completeAmount = new BigDecimal(0);
			List<String> header = null;
			HashMap<String, List<RowObject>> puh = new HashMap<String, List<RowObject>>();
			while (itr.hasNext()) {
				Row row = itr.next();
				if (row.getRowNum() > 0) {
					RowObject obj = readLine(row, sheet, header);
					/*
					System.out.println(obj.getBETRAG() + " [" + obj.getWHAT() + "] (" + obj.getDATUM() + ") ["
							+ kuh(obj.getDATUM()) + "]");
							*/
					if (puh.get(kuh(obj.getDATUM())) == null) {
						puh.put(kuh(obj.getDATUM()), new ArrayList<RowObject>());
					}
					puh.get(kuh(obj.getDATUM())).add(obj);
					completeAmount = completeAmount.add(obj.getBETRAG());
				} else {
					header = getHeaderRow(row);
				}
			}
			System.out.println("completeAmount: " + completeAmount);
			return puh;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String kuh(LocalDate datum) {
		// TODO Auto-generated method stub
		return datum.getMonth().getValue() + "/" + datum.getYear();
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

	private static RowObject readLine(Row row, XSSFSheet sheet, List<String> header) {

		RowObject rowObject = new RowObject();
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			int columnIndex = cell.getColumnIndex();
			switch (columnIndex) {
			case COL_MONAT:
				rowObject.setMONAT(cell.getStringCellValue());
				break;
			case COL_DATUM:
				rowObject.setDATUM(toLocalDate(cell.getDateCellValue()));
				break;
			case COL_BETRAG:
				rowObject.setBETRAG(moo(cell));
				break;
			case COL_SALDO:
				rowObject.setSALDO(moo(cell));
				break;
			case COL_TEXT:
				rowObject.setTEXT(cell.getStringCellValue());
				break;
			default:
				// all yes/nos
				if (extracted(cell)) {
					rowObject.setWHAT(header.get(cell.getColumnIndex()));
				}
				break;
			}
		}
		return rowObject;
	}

	private static boolean extracted(Cell cell) {
		String stringCellValue = cell.getStringCellValue();
		if (stringCellValue != null && stringCellValue.equals("JA")) {
			return true;
		}
		return false;
	}

	private static BigDecimal moo(Cell cell) {
		String numericCellValue = String.valueOf(cell.getNumericCellValue());
		BigDecimal bigDecimal = new BigDecimal(numericCellValue);
		return bigDecimal;
	}

	public static LocalDate toLocalDate(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}