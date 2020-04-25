package de.gravitex.accounting.logic;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MonthlyBudgetCategoryResult {

	private String category;
	
	// the bugdeted amount for the month
	private BigDecimal targetAmount;

	// the actually spent amount for the month
	private BigDecimal actualAmount;
	
	private MonthlyBudgetCategoryResult() {
		super();
	}
	
	public static MonthlyBudgetCategoryResult fromValues(String aCategory, BigDecimal aTargetAmount,
			BigDecimal anActualAmount) {

		MonthlyBudgetCategoryResult monthlyBudgetCategoryResult = new MonthlyBudgetCategoryResult();
		monthlyBudgetCategoryResult.setCategory(aCategory);
		monthlyBudgetCategoryResult.setTargetAmount(aTargetAmount);
		monthlyBudgetCategoryResult.setActualAmount(anActualAmount);
		return monthlyBudgetCategoryResult;
	}

	public boolean exceeded() {
		if (targetAmount == null) {
			return false;
		}
		boolean result = actualAmount.abs().compareTo(targetAmount) > 0;
		return result;
	}

	public boolean budgeted() {
		return (targetAmount != null);
	}
}