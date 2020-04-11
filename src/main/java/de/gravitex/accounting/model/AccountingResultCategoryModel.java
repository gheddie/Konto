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

	public String[] getHeaders() {
		return new String[] {"Datum", "Betrag", "Text"};
	}
}