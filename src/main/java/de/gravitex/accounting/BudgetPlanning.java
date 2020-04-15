package de.gravitex.accounting;

import java.util.Properties;

import lombok.Data;

@Data
public class BudgetPlanning {

	private Properties properties;
	
	private BudgetPlanning() {
		
	}
	
	public static BudgetPlanning fromValues(Properties aProperties) {
		BudgetPlanning budgetPlanning = new BudgetPlanning();
		budgetPlanning.setProperties(aProperties);
		return budgetPlanning;
	}
}