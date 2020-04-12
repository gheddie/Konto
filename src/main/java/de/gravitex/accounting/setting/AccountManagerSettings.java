package de.gravitex.accounting.setting;

import lombok.Data;

@Data
public class AccountManagerSettings {
	
	private boolean budgetProjectionsEnabled;
	
	private int projectionDurationInMonths;

	private AccountManagerSettings() {
		// ...
	}

	public static AccountManagerSettings fromValues(boolean aBudgetProjectionsEnabled, int aProjectionDurationInMonths) {
		AccountManagerSettings accountManagerSettings = new AccountManagerSettings();
		accountManagerSettings.setBudgetProjectionsEnabled(aBudgetProjectionsEnabled);
		accountManagerSettings.setProjectionDurationInMonths(aProjectionDurationInMonths);
		return accountManagerSettings;
	}
}