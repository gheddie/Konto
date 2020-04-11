package de.gravitex.accounting.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class AccountingResultCategoryModel {

	private String monthKey;
	
	private String category;
	
	private List<AccountingResultModelRow> accountingResultModelRows;
	
	private BigDecimal sum;
	
	private BigDecimal budget;

	public String[] getHeaders() {
		return new String[] {"Datum", "Betrag", "Text"};
	}

	public boolean inBudget() {
		if (budget == null) {
			return true;
		}
		return !(Math.abs(sum.intValue()) > budget.intValue());
	}
}