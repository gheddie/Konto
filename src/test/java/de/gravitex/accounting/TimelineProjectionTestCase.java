package de.gravitex.accounting;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.util.TimelineProjector;

public class TimelineProjectionTestCase {

	@Test
	public void testTimelineProjection() {
		
		assertTrue(TimelineProjector.fromValues("2/2008", PaymentPeriod.QUARTER, 15).getResult().hasTimeStamps("2/2008",
				"5/2008", "8/2008", "11/2008", "2/2009", "5/2009"));
		
		assertTrue(TimelineProjector.fromValues("11/2019", PaymentPeriod.MONTH, 7).getResult().hasTimeStamps("11/2019",
				"12/2019", "1/2020", "2/2020", "3/2020", "4/2020", "5/2020", "6/2020"));
	}
}