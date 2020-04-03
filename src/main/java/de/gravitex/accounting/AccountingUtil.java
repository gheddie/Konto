package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public class AccountingUtil {

	public static String getMonthKey(LocalDate datum) {
		return datum.getMonth().getValue() + "/" + datum.getYear();
	}
	
	public static boolean getBoolean(Cell cell) {
		String stringCellValue = cell.getStringCellValue();
		if (stringCellValue != null && stringCellValue.equals("JA")) {
			return true;
		}
		return false;
	}

	public static BigDecimal getBigDecimal(Cell cell) {
		String numericCellValue = String.valueOf(cell.getNumericCellValue());
		BigDecimal bigDecimal = new BigDecimal(numericCellValue);
		return bigDecimal;
	}

	public static LocalDate toLocalDate(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}