package de.gravitex.accounting.budget;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BudgetPlanning {
	
	private BigDecimal limit;
	
	private BudgetPlanning() {
		// ...
	}

	public static BudgetPlanning fromLimit(int aLimit) {
		BudgetPlanning budgetPlanning = new BudgetPlanning();
		budgetPlanning.setLimit(new BigDecimal(aLimit));
		return budgetPlanning;
	}

	public boolean amountExceeded(BigDecimal totalAmount) {
		return limit.compareTo(totalAmount.abs()) < 0;
	}
}