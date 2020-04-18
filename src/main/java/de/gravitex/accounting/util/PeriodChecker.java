package de.gravitex.accounting.util;

import java.time.Period;
import java.util.List;

public class PeriodChecker {

	private PeriodChecker() {
		
	}

	public static PeriodChecker fromValues(List<Period> aPeriods) {
		return new PeriodChecker();
	}

	public void check() {
		throw new IllegalArgumentException("");
	}
}