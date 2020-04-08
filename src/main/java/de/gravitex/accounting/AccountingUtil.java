package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.resolver.BigDecimalCellValueResolver;
import de.gravitex.accounting.resolver.BooleanCellValueResolver;
import de.gravitex.accounting.resolver.CellValueResolver;
import de.gravitex.accounting.resolver.LocalDateCellValueResolver;
import de.gravitex.accounting.resolver.StringCellValueResolver;

public class AccountingUtil {
	
	@SuppressWarnings("rawtypes")
	private static final HashMap<Class, CellValueResolver> cellValueResolvers = new HashMap<Class, CellValueResolver>();
	static {
		cellValueResolvers.put(Integer.class, new IntegerCellValueResolver());
		cellValueResolvers.put(Boolean.class, new BooleanCellValueResolver());
		cellValueResolvers.put(LocalDate.class, new LocalDateCellValueResolver());
		cellValueResolvers.put(BigDecimal.class, new BigDecimalCellValueResolver());
		cellValueResolvers.put(String.class, new StringCellValueResolver());
	}

	public static String getMonthKey(LocalDate datum) {
		return datum.getMonth().getValue() + "/" + datum.getYear();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getCellValue(Class<T> clazz, Cell cell) {
		return (T) cellValueResolvers.get(clazz).resolveCellValue(cell);
	}
}