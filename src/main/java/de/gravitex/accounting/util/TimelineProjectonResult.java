package de.gravitex.accounting.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TimelineProjectonResult {
	
	private List<TimelineProjectorItem> timelineProjectorItems = new ArrayList<TimelineProjectorItem>();

	public boolean hasTimeStamps(String...timeStamps) {
		if (timelineProjectorItems.size() != timeStamps.length) {
			return false;
		}
		int index = 0;
		for (TimelineProjectorItem timelineProjectorItem : timelineProjectorItems) {
			if (!timelineProjectorItem.getMonthKey().equals(timeStamps[index])) {
				return false;
			}
			index++;
		}
		return true;
	}

	public boolean hasTimeStamp(String aTimeStamp) {
		for (TimelineProjectorItem timelineProjectorItem : timelineProjectorItems) {
			if (timelineProjectorItem.getMonthKey().equals(aTimeStamp)) {
				return true;		
			}
		}
		return false;
	}
}