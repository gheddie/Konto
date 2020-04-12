package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.enumeration.PaymentPeriod;
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

	public static String getMonthKey(LocalDate localDate) {
		return getMonthKey(localDate.getMonth().getValue(), localDate.getYear());
	}

	public static String getMonthKey(int month, int year) {
		return month + "/" + year;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getCellValue(Class<T> clazz, Cell cell) {
		return (T) cellValueResolvers.get(clazz).resolveCellValue(cell);
	}

	public static String nextMonthlyTimeStamp(String actualAppereance, PaymentPeriod paymentPeriod) {
		String[] spl = actualAppereance.split("/");
		LocalDate localDate = LocalDate.of(Integer.parseInt(spl[1]), Integer.parseInt(spl[0]), 1);
		localDate = localDate.plusMonths(paymentPeriod.getDurationInMonths());
		return getMonthKey(localDate.getMonth().getValue(), localDate.getYear());
	}
}