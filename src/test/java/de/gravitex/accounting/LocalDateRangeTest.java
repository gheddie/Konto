package de.gravitex.accounting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import de.gravitex.accounting.filter.util.LocalDateRange;

public class LocalDateRangeTest {

	@Test
	public void testLocalDateRange() {

		assertFalse(LocalDateRange.fromValues(null, null).contains(null));
		assertTrue(LocalDateRange.fromValues(null, null).contains(LocalDate.now()));
	}
}