package de.gravitex.accounting.util;

import lombok.Data;

@Data
public class TimelineProjectorItem {
	
	private MonthKey monthKey;
	
	private TimelineProjectorItemState timelineProjectorItemState;

	private TimelineProjectorItem() {
		// ...
	}

	public static TimelineProjectorItem fromValues(MonthKey aMonthKey, TimelineProjectorItemState aTimelineProjectorItemState) {
		TimelineProjectorItem timelineProjectorItem = new TimelineProjectorItem();
		timelineProjectorItem.setMonthKey(aMonthKey);
		timelineProjectorItem.setTimelineProjectorItemState(aTimelineProjectorItemState);
		return timelineProjectorItem;
	}
}