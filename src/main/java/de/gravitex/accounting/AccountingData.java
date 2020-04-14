package de.gravitex.accounting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class AccountingData {

	private HashMap<MonthKey, AccountingMonth> data = new HashMap<MonthKey, AccountingMonth>();

	public Set<MonthKey> keySet() {
		return data.keySet();
	}

	public AccountingMonth get(MonthKey key) {
		return data.get(key);
	}

	public void put(MonthKey key, AccountingMonth accountingMonth) {
		data.put(key, accountingMonth);
	}

	public Collection<AccountingMonth> getAccountingMonths() {
		return data.values();
	}
}