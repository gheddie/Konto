package de.gravitex.accounting.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class BudgetModel {

	private MonthKey monthKey;

	// key = category
	private HashMap<String, MonthlyBudgetCategoryResult> budgetValuesForMonth;

	private int budgetPercentage;

	private BigDecimal availableAmount;

	private BigDecimal spentAmount;

	private BudgetModel() {
		super();
	}

	public static BudgetModel fromValues(MonthKey aMonthKey,
			HashMap<String, MonthlyBudgetCategoryResult> aBudgetValuesForMonth, BigDecimal anAvailableAmount,
			BigDecimal aSpentAmount) {

		BudgetModel budgetModel = new BudgetModel();
		budgetModel.setMonthKey(aMonthKey);
		budgetModel.setBudgetValuesForMonth(aBudgetValuesForMonth);

		budgetModel.setAvailableAmount(anAvailableAmount);
		budgetModel.setSpentAmount(aSpentAmount);

		return budgetModel;
	}

	public int getBudgetPercentage() {
		return (int) AccountingUtil.getPercentage(spentAmount.doubleValue(), availableAmount.doubleValue());
	}

	public BigDecimal getRemainingAmount() {
		if (availableAmount.compareTo(spentAmount) < 0) {
			return null;
		}
		return availableAmount.subtract(spentAmount);
	}
	
	private List<MonthlyBudgetCategoryResult> getBudgetedResults() {
		List<MonthlyBudgetCategoryResult> result = new ArrayList<MonthlyBudgetCategoryResult>();
		for (MonthlyBudgetCategoryResult categoryResult : budgetValuesForMonth.values()) {
			if (categoryResult.budgeted()) {
				result.add(categoryResult);
			}
		}
		return result;
	}

	private List<MonthlyBudgetCategoryResult> getUnbudgetedResults() {
		List<MonthlyBudgetCategoryResult> result = new ArrayList<MonthlyBudgetCategoryResult>();
		for (MonthlyBudgetCategoryResult categoryResult : budgetValuesForMonth.values()) {
			if (!categoryResult.budgeted()) {
				result.add(categoryResult);
			}
		}
		return result;
	}

	public String toString() {

		StringBuffer buffer = new StringBuffer();
		MonthlyBudgetCategoryResult monthlyBudgetCategoryResult = null;
		buffer.append(" --------- BUDGET ["+monthKey.toString()+"] --------- \n");
		buffer.append(" ---> MIT BUDGET\n");
		for (MonthlyBudgetCategoryResult budgeted : getBudgetedResults()) {
			appendResult(buffer, budgeted);
			buffer.append("\n");
		}
		buffer.append(" ---> OHNE BUDGET\n");
		for (MonthlyBudgetCategoryResult unbudgeted : getUnbudgetedResults()) {
			appendResult(buffer, unbudgeted);
			buffer.append("\n");
		}
		buffer.append(" ---> available: " + availableAmount + "\n");
		buffer.append(" ---> spent: " + spentAmount + "\n");
		buffer.append(" ---> remaining: " + getRemainingAmount() + "\n");
		buffer.append(" ---> percentage: " + getBudgetPercentage() + "\n");
		return buffer.toString();
	}

	private void appendResult(StringBuffer buffer, MonthlyBudgetCategoryResult monthlyBudgetCategoryResult) {
		
		buffer.append(
				"[" + monthlyBudgetCategoryResult.getCategory() + "]" + (monthlyBudgetCategoryResult.exceeded() ? "Überschritten" : "In Limit")
						+ "[Grenze=" + monthlyBudgetCategoryResult.getTargetAmount() + ", Aktuell:"
						+ monthlyBudgetCategoryResult.getActualAmount().abs() + "]");
		if (!monthlyBudgetCategoryResult.budgeted()) {
			buffer.append(" ---> [nicht budgetiert!!]");
		}
	}
}