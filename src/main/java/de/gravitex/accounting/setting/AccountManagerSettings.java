package de.gravitex.accounting.setting;

import lombok.Data;

@Data
public class AccountManagerSettings {
	
	// nur Einträge mit fester Kategorie
	private boolean fixedBudgetEntriesOnly;
	
	// Einträge ohne Budget anzeigen
	private boolean showUnbudgetedtEntries;

	// Realwerte in Budget anzeigen
	private boolean showRealBudgetEntries;
	
	// so weit in die Zukunft Budgets projizieren
	private int projectionDurationInMonths;

	private AccountManagerSettings() {
		// ...
	}

	public static AccountManagerSettings fromValues(boolean aFixedBudgetEntriesOnly, boolean aShowUnbudgetedtEntries,
			boolean aShowRealValuesInBidgetPlanning, int aProjectionDurationInMonths) {
		
		AccountManagerSettings accountManagerSettings = new AccountManagerSettings();
		accountManagerSettings.setFixedBudgetEntriesOnly(aFixedBudgetEntriesOnly);
		accountManagerSettings.setShowUnbudgetedtEntries(aShowUnbudgetedtEntries);
		accountManagerSettings.setShowRealBudgetEntries(aShowRealValuesInBidgetPlanning);
		accountManagerSettings.setProjectionDurationInMonths(aProjectionDurationInMonths);
		return accountManagerSettings;
	}
}