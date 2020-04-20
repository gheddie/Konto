package de.gravitex.accounting.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import lombok.Data;

public class OverlapChecker {
	
	private static final Logger logger = Logger.getLogger(OverlapChecker.class);
	
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
		for (LocalDate day : new MyPeriod(earliest, latest).getDays()) {
			logger.info(day);
			markers.add(new LocalDateMarker(day));
		}
		for (MyPeriod period : periods) {
			for (LocalDate day : period.getDays()) {
				logger.info("checking: " + day);
				try {
					LocalDateMarker foundMarker = findMarker(day);
					foundMarker.check();
				} catch (Exception e) {
					logger.info("day '" + day + "' already checked --> overlap!!");
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
		periods.add(new MyPeriod(aStart, aEnd));
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
	
	@Data
	private class LocalDateMarker {
		
		private LocalDate mark;
		
		private Boolean marked;
		
		private LocalDateMarker(LocalDate aMark) {
			super();
			this.mark = aMark;
			this.marked = false;
		}

		public void check() throws Exception {
			if (marked) {
				throw new Exception();
			}
			marked = true;
		}
	}
	
	@Data
	private class MyPeriod {
		
		private LocalDate start;
		
		private LocalDate end;

		private MyPeriod(LocalDate aStart, LocalDate aEnd) {
			super();
			this.start = aStart;
			this.end = aEnd;
		}

		public List<LocalDate> getDays() {
			
			List<LocalDate> result = new ArrayList<LocalDate>();
			LocalDate actual = start;
			while (!actual.isEqual(end)) {
				result.add(actual);
				actual = actual.plusDays(1);
			}
			result.add(actual);
			return result;
		}

		public boolean valid() {
			if (start == null || end == null) {
				return false;	
			}
			if (end.isBefore(start)) {
				return false;	
			}
			return true;
		}
	}
}