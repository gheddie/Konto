package de.gravitex.accounting;

public class AccountingMain {

	public static void main(String[] args) {
		
		AccountingManager manager = AccountingManager.instance();
		
		// manager.printMonth("4/2020", true);
		
		/*
		for (AMonth aMonth : k.values()) {
			aMonth.printSorted();
		}
		*/
		
		manager.printCategory("Nebenkosten");
	}
}