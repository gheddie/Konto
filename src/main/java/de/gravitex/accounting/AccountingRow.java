package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountingRow implements Comparable<AccountingRow> {

	@DisplayValue(header = "Lfd. Nr.")
	private Integer runningIndex;
	
	@DisplayValue(header = "Datum")
	private LocalDate date;
	
	@DisplayValue(header = "Betrag")
	private BigDecimal amount;
	
	private BigDecimal saldo;
	
	@DisplayValue(header = "Partner")
	private String partner;
	
	@DisplayValue(header = "Text")
	private String text;
	
	private String mainAccount;
	
	private String mainAccountReference;
	
	@DisplayValue(header = "Kategorie")
	private String category;
	
	@DisplayValue(header = "gültig von")
	private LocalDate validFrom;
	
	@DisplayValue(header = "gültig bis")
	private LocalDate validUntil;
	
	// @DisplayValue(header = "Alarm")
	private Boolean alarm;

	public boolean hasCategory(String aCategory) {
		return (category.equals(aCategory));
	}
	
	public boolean hasPartner(String aPartner) {
		return (partner != null && partner.equals(aPartner));
	}

	public int compareTo(AccountingRow accountingRow) {
		return runningIndex.compareTo(accountingRow.getRunningIndex());
	}

	public String[] asTableRow(boolean withValidity) {
		if (withValidity) {
			return new String[] {String.valueOf(runningIndex), date.toString(), amount.toString(), validFrom != null ? validFrom.toString() : "", validUntil != null ? validUntil.toString() : "", text};	
		} else {
			return new String[] {String.valueOf(runningIndex), date.toString(), amount.toString(), text};
		}
	}

	public boolean checkPeriod() {
		if (validFrom == null || validUntil == null) {
			return false;
		}
		if (validUntil.isBefore(validFrom)) {
			return false;
		}
		return true;
	}
}