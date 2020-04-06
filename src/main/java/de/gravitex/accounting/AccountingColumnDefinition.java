package de.gravitex.accounting;

import lombok.Data;

@Data
public class AccountingColumnDefinition {

	private String columnName;
	
	private int columnIndex;
	
	private AccountingColumnDefinition() {
		// ...
	}

	public static AccountingColumnDefinition fromValues(int aColumnIndex, String aColumnName) {
		AccountingColumnDefinition accountingColumnDefinition = new AccountingColumnDefinition();
		accountingColumnDefinition.setColumnIndex(aColumnIndex);
		accountingColumnDefinition.setColumnName(aColumnName);
		return accountingColumnDefinition;
	}
}