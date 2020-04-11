package de.gravitex.accounting.gui;

import java.util.Set;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.model.AccountingResultMonthModel;

public class AccountingMain {

	public static void main(String[] args) {
		
		System.out.println(AccountingManager.getInstance().printMonth("4/2020", false).toString());
		
		// ---
		
		AccountingResultMonthModel model = AccountingManager.getInstance().getAccountingResultMonthModel("4/2020");
		Set<String> distinctCategories = model.getDistinctCategories();
		
		// AccountingResultCategoryModel model = AccountingManager.getInstance().getAccountingResultCategoryModel("4/2020", "Essen");
		
		int werner = 5;
		
		// ---
		
		// AccountingManager.getInstance().printAll(true);
		
		// AccountingManager.getInstance().saldoCheck();
		
		// AccountingManager.instance().printCategory("Nebenkosten");
		// AccountingManager.instance().printCategory("Paypal");
		// AccountingManager.getInstance().printCategory("Abo");
	}
}