package de.gravitex.accounting;

public class AccountingMain {

	public static void main(String[] args) {
		
		AccountingManager.instance().printMonth("4/2020", false);
		
		// AccountingManager.instance().saldoCheck();
		
		// AccountingManager.instance().printAll(true);
		
		// AccountingManager.instance().printCategory("Nebenkosten");
		// AccountingManager.instance().printCategory("Paypal");
	}
}