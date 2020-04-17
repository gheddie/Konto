package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Properties;

import org.junit.Test;

import de.gravitex.accounting.util.MonthKey;

public class IncomeTest {
	
	private static final BigDecimal V_2400 = new BigDecimal(2400);
	private static final BigDecimal V_3000 = new BigDecimal(3000);

	@Test
	public void testIncome() {
		
		Properties aProperties = new Properties();
		
		aProperties.put(MonthKey.fromValues(1, 2020).toString(), V_2400.toString());
		aProperties.put(MonthKey.fromValues(7, 2020).toString(), V_3000.toString());
		
		Income income = Income.fromValues(aProperties);
		
		assertNull(income.getIncomeForMonth(MonthKey.fromValues(12, 2019)));
		assertEquals(V_2400, income.getIncomeForMonth(MonthKey.fromValues(1, 2020)));
		assertEquals(V_2400, income.getIncomeForMonth(MonthKey.fromValues(6, 2020)));
		assertEquals(V_3000, income.getIncomeForMonth(MonthKey.fromValues(7, 2020)));
		assertEquals(V_3000, income.getIncomeForMonth(MonthKey.fromValues(12, 2020)));
		assertEquals(V_3000, income.getIncomeForMonth(MonthKey.fromValues(1, 2021)));
	}
}