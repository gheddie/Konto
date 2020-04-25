package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import lombok.Data;

@Data
public class BudgetPlanning {

	private Properties properties;
	
	private BudgetPlanning() {
		// ...
	}
	
	public static BudgetPlanning fromValues(Properties aProperties) {
		BudgetPlanning budgetPlanning = new BudgetPlanning();
		budgetPlanning.setProperties(aProperties);
		return budgetPlanning;
	}

	public Set<String> getCategoryKeys() {
		Set<String> result = new HashSet<String>();
		for (Object key : properties.keySet()) {
			result.add((String) key);
		}
		return result;
	}

	public BigDecimal getAmountForCategory(String categoryBudget) {
		Object value = properties.get(categoryBudget);
		if (value == null) {
			return null;
		}
		return new BigDecimal(String.valueOf(value));
	}
}