package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class Income {

	private Properties properties;
	
	private Income() {
		// ...
	}
	
	public static Income fromValues(Properties aProperties) {
		Income budgetPlanning = new Income();
		budgetPlanning.setProperties(aProperties);
		return budgetPlanning;
	}

	public BigDecimal getIncomeForMonth(MonthKey monthKey) {
		List<MonthlyIncome> list = asList();
		if (list == null || list.size() == 0) {
			return null;	
		}
		if (monthKey.isBefore(list.get(0).getMonthKey())) {
			// before first entry
			return null;
		}
		if (monthKey.equals(list.get(0).getMonthKey())) {
			// equal to first entry
			return list.get(0).getAmount();
		}
		// loop
		for (int index = list.size() - 1; index >= 0; index--) {
			if (monthKey.isAfter(list.get(index).getMonthKey()) || monthKey.equals(list.get(index).getMonthKey())) {
				return list.get(index).getAmount();
			}
		}	
		return null;
	}

	private List<MonthlyIncome> asList() {
		List<MonthlyIncome> result = new ArrayList<Income.MonthlyIncome>();
		for (Object key : properties.keySet()) {
			result.add(new MonthlyIncome(MonthKey.fromString((String) key), new BigDecimal(String.valueOf(properties.get(key)))));
		}
		Collections.sort(result);
		return result;
	}
	
	// ---
	
	@Data
	private class MonthlyIncome implements Comparable<MonthlyIncome> {
		
		private MonthKey monthKey;
		
		private BigDecimal amount;

		public MonthlyIncome(MonthKey monthKey, BigDecimal amount) {
			super();
			this.monthKey = monthKey;
			this.amount = amount;
		}

		@Override
		public int compareTo(MonthlyIncome o) {
			return monthKey.compareTo(o.getMonthKey());
		}
	}
}