package de.gravitex.accounting.application.definition;

import de.gravitex.accounting.enumeration.AccountingType;
import lombok.Data;

@Data
public class AccountDefinition {
	
	private String accountKey;
	
	private AccountingType accountingType;

	private AccountDefinition() {
		super();
	}
	
	public static AccountDefinition fromValues(String anAccountKey, AccountingType anAccountingType) {
		AccountDefinition accountDefinition = new AccountDefinition();
		accountDefinition.setAccountKey(anAccountKey);
		accountDefinition.setAccountingType(anAccountingType);
		return accountDefinition;
	}
}