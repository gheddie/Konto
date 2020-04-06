package de.gravitex.accounting;

public class AccountingMain {

	public static void main(String[] args) {
		
		AccountingManager manager = AccountingManager.instance();
		
		// manager.printMonth("4/2020", true);
		
		manager.printAll(true);
		
		// manager.printCategory("Nebenkosten");
		// manager.printCategory("Paypal");
	}
}