package de.gravitex.accounting;

public class AccountingMain {

	public static void main(String[] args) {
		
		AccountingManager.getInstance().printMonth("4/2020", false);
		
		AccountingManager.getInstance().saldoCheck();
		
		// AccountingManager.instance().printAll(true);
		
		// AccountingManager.instance().printCategory("Nebenkosten");
		// AccountingManager.instance().printCategory("Paypal");
		// AccountingManager.getInstance().printCategory("Abo");
	}
}