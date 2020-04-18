package de.gravitex.accounting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverlapChecker {
	
	private List<MyPeriod> periods = new ArrayList<MyPeriod>();
	
	private List<LocalDateMarker> markers = new ArrayList<LocalDateMarker>();
	
	private List<LocalDateMarker> remainingMarkers = new ArrayList<LocalDateMarker>();

	private boolean overlapFlag = false;
	
	private boolean invalidPeriodFlag = false;

	public boolean check() {
		if (periods == null || periods.size() == 0) {
			return true;
		}
		LocalDate earliest = LocalDate.MAX;
		LocalDate latest = LocalDate.MIN;
		for (MyPeriod myPeriod : periods) {
			if (!myPeriod.valid()) {
				invalidPeriodFlag = true;
				return false;
			}
			if (myPeriod.getStart().isBefore(earliest)) {
				earliest = myPeriod.getStart();
			}
			if (myPeriod.getEnd().isAfter(latest)) {
				latest = myPeriod.getEnd();
			}
		}
		for (LocalDate day : MyPeriod.fromValues(earliest, latest).getDays()) {
			System.out.println(day);
			markers.add(LocalDateMarker.fromValues(day));
		}
		for (MyPeriod period : periods) {
			for (LocalDate day : period.getDays()) {
				System.out.println("checking: " + day);
				try {
					LocalDateMarker foundMarker = findMarker(day);
					foundMarker.check();
				} catch (Exception e) {
					System.out.println("day '" + day + "' already checked --> overlap!!");
					overlapFlag = true;
					return false;
				}
			}
		}
		remainingMarkers = getRemaining();
		if (remainingMarkers.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private List<LocalDateMarker> getRemaining() {
		List<LocalDateMarker> result = new ArrayList<LocalDateMarker>();
		for (LocalDateMarker localDateMarker : markers) {
			if (!localDateMarker.getMarked()) {
				result.add(localDateMarker);
			}
		}
		return result;
	}

	private LocalDateMarker findMarker(LocalDate day) {
		for (LocalDateMarker localDateMarker : markers) {
			if (day.equals(localDateMarker.getMark())) {
				return localDateMarker;
			}
		}
		return null;
		
	}

	public OverlapChecker withPeriod(LocalDate aStart, LocalDate aEnd) {
		periods.add(MyPeriod.fromValues(aStart, aEnd));
		return this;
	}

	public boolean hasGap(LocalDate aDate) {
		for (LocalDateMarker localDateMarker : getRemaining()) {
			if (localDateMarker.getMark().equals(aDate)) {
				return true;
			}
		}
		return false;
	}

	public List<LocalDate> getRemainingDays() {
		List<LocalDate> result = new ArrayList<LocalDate>();
		for (LocalDateMarker localDateMarker : remainingMarkers) {
			result.add(localDateMarker.getMark());
		}
		return result;
	}

	public boolean isOverlapFlag() {
		return overlapFlag;
	}

	public boolean isInvalidPeriodFlag() {
		return invalidPeriodFlag;
	}
}