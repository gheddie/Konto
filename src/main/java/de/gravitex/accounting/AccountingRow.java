package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingRow {

	private String month;
	private LocalDate date;
	private BigDecimal amount;
	private BigDecimal saldo;
	private String text;
	private String category;

	public boolean hasCategory(String aCategory) {
		return (category.equals(aCategory));
	}
}