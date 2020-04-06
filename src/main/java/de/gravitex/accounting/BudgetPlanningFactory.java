package de.gravitex.accounting;

import java.util.HashMap;

public class BudgetPlanningFactory {

	private static final HashMap<AccountingCategory, BudgetPlanning> budgetlimits = new HashMap<AccountingCategory, BudgetPlanning>();
	static {
		budgetlimits.put(AccountingCategory.Auto, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Undefiniert, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Kreditkarte, BudgetPlanning.fromLimit(100));
	}

	public static BudgetPlanning getBudgetPlanning(String categoryKey) {
		try {
			return budgetlimits.get(AccountingCategory.valueOf(categoryKey));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}