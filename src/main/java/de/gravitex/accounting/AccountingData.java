package de.gravitex.accounting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import lombok.Data;

@Data
public class AccountingData {

	private HashMap<String, AccountingMonth> data = new HashMap<String, AccountingMonth>();

	public Set<String> keySet() {
		return data.keySet();
	}

	public AccountingMonth get(String key) {
		return data.get(key);
	}

	public void put(String key, AccountingMonth accountingMonth) {
		data.put(key, accountingMonth);
	}

	public Collection<AccountingMonth> getAccountingMonths() {
		return data.values();
	}
}