package de.gravitex.accounting.setting;

import lombok.Data;

@Data
public class AccountManagerSettings {
	
	private boolean budgetProjectionsEnabled;
	
	private boolean showActualValuesInBidgetPlanning;
	
	private int projectionDurationInMonths;

	private AccountManagerSettings() {
		// ...
	}

	public static AccountManagerSettings fromValues(boolean aBudgetProjectionsEnabled, int aProjectionDurationInMonths, boolean aShowActualValuesInBidgetPlanning) {
		AccountManagerSettings accountManagerSettings = new AccountManagerSettings();
		accountManagerSettings.setBudgetProjectionsEnabled(aBudgetProjectionsEnabled);
		accountManagerSettings.setShowActualValuesInBidgetPlanning(aShowActualValuesInBidgetPlanning);
		accountManagerSettings.setProjectionDurationInMonths(aProjectionDurationInMonths);
		return accountManagerSettings;
	}
}