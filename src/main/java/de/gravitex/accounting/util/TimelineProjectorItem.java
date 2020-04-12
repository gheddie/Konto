package de.gravitex.accounting.util;

import lombok.Data;

@Data
public class TimelineProjectorItem {
	
	private String monthKey;
	
	private TimelineProjectorItemState timelineProjectorItemState;

	private TimelineProjectorItem() {
		// ...
	}

	public static TimelineProjectorItem fromValues(String aMonthKey, TimelineProjectorItemState aTimelineProjectorItemState) {
		TimelineProjectorItem timelineProjectorItem = new TimelineProjectorItem();
		timelineProjectorItem.setMonthKey(aMonthKey);
		timelineProjectorItem.setTimelineProjectorItemState(aTimelineProjectorItemState);
		return timelineProjectorItem;
	}
}