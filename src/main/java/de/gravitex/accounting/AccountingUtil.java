package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public class AccountingUtil {

	private static final String CHECKED_CATEGORY = "X";

	public static String getMonthKey(LocalDate datum) {
		return datum.getMonth().getValue() + "/" + datum.getYear();
	}
	
	public static boolean getBoolean(Cell cell) {
		String value = cell.getStringCellValue();
		if (value != null && value.equals(CHECKED_CATEGORY)) {
			return true;
		}
		return false;
	}

	public static BigDecimal getBigDecimal(Cell cell) {
		// TODO works, but a better way?!?
		String numericCellValue = String.valueOf(cell.getNumericCellValue());
		BigDecimal bigDecimal = new BigDecimal(numericCellValue);
		return bigDecimal;
	}

	public static LocalDate toLocalDate(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}