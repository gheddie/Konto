package de.gravitex.accounting.gui;

import de.gravitex.accounting.AccountingManager;

public class AccountingMain {

	public static void main(String[] args) {
		
		System.out.println(AccountingManager.getInstance().printMonth("4/2020", false).toString());
		
		// AccountingManager.getInstance().printAll(true);
		
		// AccountingManager.getInstance().saldoCheck();
		
		// AccountingManager.instance().printCategory("Nebenkosten");
		// AccountingManager.instance().printCategory("Paypal");
		// AccountingManager.getInstance().printCategory("Abo");
	}
}