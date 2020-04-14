package de.gravitex.accounting;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.util.TimelineProjector;

public class TimelineProjectionTestCase {

	@Test
	public void testTimelineProjection() {

		assertTrue(TimelineProjector.fromValues(MonthKey.fromValues(2, 2008), PaymentPeriod.QUARTER, 15).getResult()
				.hasTimeStamps(MonthKey.fromValues(5, 2008), MonthKey.fromValues(8, 2008),
						MonthKey.fromValues(11, 2008), MonthKey.fromValues(2, 2009), MonthKey.fromValues(5, 2009)));

		assertTrue(TimelineProjector.fromValues(MonthKey.fromValues(11, 2019), PaymentPeriod.MONTH, 7).getResult()
				.hasTimeStamps(MonthKey.fromValues(12, 2019), MonthKey.fromValues(1, 2020),
						MonthKey.fromValues(2, 2020), MonthKey.fromValues(3, 2020), MonthKey.fromValues(4, 2020),
						MonthKey.fromValues(5, 2020), MonthKey.fromValues(6, 2020)));
	}
}