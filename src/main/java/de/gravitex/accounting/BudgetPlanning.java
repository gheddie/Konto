package de.gravitex.accounting;

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
		boolean compared = limit.compareTo(totalAmount.abs()) < 0;
		return compared;
	}
}