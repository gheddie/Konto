package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

import de.gravitex.accounting.enumeration.AccountingError;
import lombok.Data;

@Data
public class AccountingRow implements Comparable<AccountingRow> {

	private Integer runningIndex;
	private LocalDate date;
	private BigDecimal amount;
	private BigDecimal saldo;
	private String partner;
	private String text;
	private String category;
	private String alarm;

	public boolean hasCategory(String aCategory) {
		return (category.equals(aCategory));
	}
	
	public boolean hasPartner(String aPartner) {
		return (partner != null && partner.equals(aPartner));
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
		if (category.equals(AccountingSingleton.UNDEFINED_CATEGORY) && (text == null || text.length() == 0)) {
			return AccountingError.UNDEF_NO_TEXT;
		}
		return null;
	}

	public String[] asTableRow() {
		return new String[] {String.valueOf(runningIndex), date.toString(), amount.toString(), text};
	}
}