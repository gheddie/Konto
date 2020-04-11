package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Vector;

import de.gravitex.accounting.enumeration.AccountingError;
import lombok.Data;

@Data
public class AccountingRow implements Comparable<AccountingRow> {

	private Integer runningIndex;
	private LocalDate date;
	private BigDecimal amount;
	private BigDecimal saldo;
	private String text;
	private String category;

	public boolean hasCategory(String aCategory) {
		return (category.equals(aCategory));
	}

	public int compareTo(AccountingRow accountingRow) {
		return runningIndex.compareTo(accountingRow.getRunningIndex());
	}

	public AccountingError getError() {
		// no running index
		if (runningIndex == null) {
			return AccountingError.NO_RUNNING_INDEX;
		}
		// no date
		if (date == null) {
			return AccountingError.NO_DATE;
		}
		if (category == null) {
			return AccountingError.NO_CATEGORY;
		}
		// undefined without without a text
		if (category.equals(AccountingManager.UNDEFINED_CATEGORY) && (text == null || text.length() == 0)) {
			return AccountingError.UNDEF_NO_TEXT;
		}
		return null;
	}

	public String[] asTableRow() {
		return new String[] {date.toString(), amount.toString(), text};
	}
}