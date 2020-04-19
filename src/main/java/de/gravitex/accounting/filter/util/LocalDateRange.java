package de.gravitex.accounting.filter.util;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LocalDateRange {
	
	private LocalDate from;
	
	private LocalDate to;

	private LocalDateRange() {
		// ...
	}

	public static LocalDateRange fromValues(LocalDate aFrom, LocalDate aTo) {
		LocalDateRange localDateRange = new LocalDateRange();
		localDateRange.setFrom(aFrom);
		localDateRange.setTo(aTo);
		return localDateRange;
	}

	public boolean contains(LocalDate localDate) {
		if (localDate == null) {
			return false;
		}
		// infinity
		if (from == null && to == null) {
			return true;
		}
		return (localDate.equals(from) || localDate.isAfter(from)) && (localDate.equals(to) || localDate.isBefore(to));
	}
}