package de.gravitex.accounting.budget;

import java.util.HashMap;

import de.gravitex.accounting.AccountingCategory;
import de.gravitex.accounting.AccountingError;
import de.gravitex.accounting.exception.AccountingException;

public class BudgetPlanningFactory {

	private static final HashMap<AccountingCategory, BudgetPlanning> budgetlimits = new HashMap<AccountingCategory, BudgetPlanning>();
	static {
		budgetlimits.put(AccountingCategory.Auto, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Undefiniert, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Kreditkarte, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Paypal, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Nebenkosten, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Nahverkehr, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Telekommunikation, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Essen, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Sonstiges, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Abo, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Lebensversicherung, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Miete, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Unterhalt, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Benzin, BudgetPlanning.fromLimit(150));
		budgetlimits.put(AccountingCategory.Fahrrad, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Charity, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Musik, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Einrichtung, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.AbhebungEC, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Kippen, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Fitnessstudio, BudgetPlanning.fromLimit(100));
		budgetlimits.put(AccountingCategory.Rundfunk, BudgetPlanning.fromLimit(100));
		
		// --- TODO positives?!?
		
		budgetlimits.put(AccountingCategory.Krankengeld, BudgetPlanning.fromLimit(100));
	}

	public static BudgetPlanning getBudgetPlanning(String categoryKey) {
		BudgetPlanning budgetPlanning = budgetlimits.get(AccountingCategory.valueOf(categoryKey));
		if (budgetPlanning == null) {
			throw new AccountingException("no bugdet planning found for category '" + categoryKey + "'!!",
					AccountingError.NO_BUDGET_FOR_CATEGORY, null);
		}
		return budgetPlanning;
	}
}