package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.resolver.BooleanCellValueResolver;
import de.gravitex.accounting.resolver.LocalDateCellValueResolver;

public class AccountingUtil {
	
	private static final HashMap<Class, CellValueResolver> cellValueResolvers = new HashMap<Class, CellValueResolver>();
	static {
		cellValueResolvers.put(Boolean.class, new BooleanCellValueResolver());
		cellValueResolvers.put(LocalDate.class, new LocalDateCellValueResolver());
		cellValueResolvers.put(BigDecimal.class, new BigDecimalCellValueResolver());
	}

	public static String getMonthKey(LocalDate datum) {
		return datum.getMonth().getValue() + "/" + datum.getYear();
	}
	
	public static boolean getBoolean(Cell cell) {
		return (Boolean) cellValueResolvers.get(Boolean.class).resolveCellValue(cell);
	}

	public static BigDecimal getBigDecimal(Cell cell) {
		return (BigDecimal) cellValueResolvers.get(BigDecimal.class).resolveCellValue(cell);
	}

	public static LocalDate getLocalDate(Cell cell) {
		return (LocalDate) cellValueResolvers.get(LocalDate.class).resolveCellValue(cell);
	}
}