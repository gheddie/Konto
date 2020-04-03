package de.gravitex.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class RowObject {

	private String MONAT;
	private LocalDate DATUM;
	private BigDecimal BETRAG;
	private BigDecimal SALDO;
	private String TEXT;
	
	private String WHAT;
}