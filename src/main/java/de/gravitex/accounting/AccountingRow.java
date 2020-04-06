package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingRow implements Comparable<AccountingRow> {

	private String month;
	private LocalDate date;
	private BigDecimal amount;
	private BigDecimal saldo;
	private String text;
	private String category;

	public boolean hasCategory(String aCategory) {
		return (category.equals(aCategory));
	}

	public int compareTo(AccountingRow accountingRow) {
		return date.compareTo(accountingRow.getDate());
	}

	public AccountingError getError() {
		// no date
		if (date == null) {
			return AccountingError.NO_DATE;
		}
		// undefined without without a text
		if (category.equals(AccountingCategory.Undefiniert.toString()) && (text == null || text.length() == 0)) {
			return AccountingError.UNDEF_NO_TEXT;
		}
		return null;
	}
}