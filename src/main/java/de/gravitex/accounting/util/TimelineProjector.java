package de.gravitex.accounting.util;

import java.util.List;

import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import lombok.Data;

@Data
public class TimelineProjector {
	
	private String initialAppereance;
	
	private PaymentPeriod paymentPeriod;
	
	private int projectionDuration;
	
	private TimelineProjector() {
		// ...
	}
	
	public static TimelineProjector fromValues(String anInitialAppereance, PaymentPeriod aPaymentPeriod, int aProjectionDuration) {
		TimelineProjector timelineProjector = new TimelineProjector();
		timelineProjector.setInitialAppereance(anInitialAppereance);
		timelineProjector.setPaymentPeriod(aPaymentPeriod);
		timelineProjector.setProjectionDuration(aProjectionDuration);
		return timelineProjector;
	}

	public TimelineProjectonResult getResult() {
		TimelineProjectonResult result = new TimelineProjectonResult();
		String actualAppereance = initialAppereance;
		// result.getTimelineProjectorItems().add(TimelineProjectorItem.fromValues(actualAppereance , null));
		int months = 0;
		while (months < projectionDuration) {
			actualAppereance = AccountingUtil.nextMonthlyTimeStamp(actualAppereance, paymentPeriod);
			result.getTimelineProjectorItems().add(TimelineProjectorItem.fromValues(actualAppereance , null));
			months += paymentPeriod.getDurationInMonths();
		}
		return result;
	}
}