package de.gravitex.accounting.model;

import java.math.BigDecimal;
import java.util.List;

import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public class AccountingResultCategoryModel {

	private MonthKey monthKey;
	
	private String category;
	
	private List<AccountingResultModelRow> accountingResultModelRows;
	
	private BigDecimal sum;
	
	private BigDecimal budget;

	public static String[] getHeaders() {
		return new String[] {"Lfd. Nr.", "Datum", "Betrag", "Text"};
	}
	
	public static String[] getHeadersFromUntil() {
		return new String[] {"Lfd. Nr.", "Datum", "Betrag", "von", "bis", "Text"};
	}

	public boolean inBudget() {
		if (budget == null) {
			return true;
		}
		return !(Math.abs(sum.intValue()) > budget.intValue());
	}
}