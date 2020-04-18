package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import de.gravitex.accounting.util.OverlapChecker;

public class PeriodCheckerTest {
	
	@Test
	public void testPeriodCheckerWithPeriodicalGaps() {
		
		OverlapChecker checker = new OverlapChecker()
				.withPeriod(LocalDate.of(2017, 2, 10), LocalDate.of(2017, 2, 12))
				.withPeriod(LocalDate.of(2017, 2, 15), LocalDate.of(2017, 2, 18))
				.withPeriod(LocalDate.of(2017, 2, 23), LocalDate.of(2017, 2, 26));
		
		/**
		 * gaps:
		 * 
		 * 13-14
		 * 19-22
		 */
		
		assertFalse(checker.check());
		assertFalse(checker.isOverlapFlag());
		assertFalse(checker.isInvalidPeriodFlag());
		assertEquals(6, checker.getRemainingDays().size());
		
		
	}
	
	@Test
	public void testPeriodCheckerWithDayGap() {
		
		OverlapChecker checker = new OverlapChecker().withPeriod(LocalDate.of(2017, 2, 20), LocalDate.of(2017, 2, 24))
				.withPeriod(LocalDate.of(2017, 2, 26), LocalDate.of(2017, 2, 28));
		assertFalse(checker.check());
		assertFalse(checker.isOverlapFlag());
		assertFalse(checker.isInvalidPeriodFlag());
		// 25.02. is a gap...
		assertEquals(1, checker.getRemainingDays().size());
		assertTrue(checker.hasGap(LocalDate.of(2017, 2, 25)));
	}
	
	@Test
	public void testPeriodCheckerWithOverlap() {
		
		OverlapChecker checker = new OverlapChecker().withPeriod(LocalDate.of(2017, 2, 20), LocalDate.of(2017, 2, 24))
				.withPeriod(LocalDate.of(2017, 2, 22), LocalDate.of(2017, 2, 26));
		assertFalse(checker.check());
		assertTrue(checker.isOverlapFlag());
		assertFalse(checker.isInvalidPeriodFlag());
		assertEquals(0, checker.getRemainingDays().size());
	}
	
	@Test
	public void testPeriodCheckerInvalidPeriod() {
		
		OverlapChecker checker = new OverlapChecker().withPeriod(LocalDate.of(2017, 2, 26), LocalDate.of(2017, 2, 20));
		assertFalse(checker.check());
		assertTrue(checker.isInvalidPeriodFlag());
		assertEquals(0, checker.getRemainingDays().size());
	}
	
	@Test
	public void testPeriodCheckerBlank() {
		
		OverlapChecker checker = new OverlapChecker();
		assertTrue(checker.check());
		assertEquals(0, checker.getRemainingDays().size());
	}

	@Test
	public void testPeriodCheckerSimple() {
		
		OverlapChecker checker = new OverlapChecker().withPeriod(LocalDate.of(2017, 2, 20), LocalDate.of(2017, 2, 26));
		assertTrue(checker.check());
		assertEquals(0, checker.getRemainingDays().size());
	}
}