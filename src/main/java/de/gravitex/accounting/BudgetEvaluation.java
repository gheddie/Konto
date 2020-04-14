package de.gravitex.accounting;

import de.gravitex.accounting.enumeration.BudgetEvaluationResult;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class BudgetEvaluation {

	private String category;

	private MonthKey monthKey;

	private BudgetEvaluationResult budgetEvaluationResult;

	private BudgetEvaluation() {
		// ...
	}

	public static BudgetEvaluation fromValues(String aCategory, MonthKey aMonthKey,
			BudgetEvaluationResult aBudgetEvaluationResult) {
		BudgetEvaluation budgetEvaluation = new BudgetEvaluation();
		budgetEvaluation.setCategory(aCategory);
		budgetEvaluation.setMonthKey(aMonthKey);
		budgetEvaluation.setBudgetEvaluationResult(aBudgetEvaluationResult);
		return budgetEvaluation;
	}

	public String generateMessage() {
		String message = null;
		switch (budgetEvaluationResult) {
		case MISSING_BUDGET:
			message = "Fehlendes Budget für Kategorie '" + category + "' in Monat '" + monthKey + "'!!";
			break;
		case MISPLACED_BUDGET:
			message = "Falsch plaziertes Budget für Kategorie '" + category + "' in Monat '" + monthKey + "'!!";
			break;
		}
		return message;
	}
}