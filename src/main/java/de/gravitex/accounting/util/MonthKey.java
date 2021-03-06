package de.gravitex.accounting.util;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MonthKey implements Comparable<MonthKey> {

	private int month;
	
	private int year;
	
	private MonthKey() {
		// ...
	}

	public static MonthKey fromValues(int aMonth, int aYear) {
		MonthKey monthKey = new MonthKey();
		monthKey.setMonth(aMonth);
		monthKey.setYear(aYear);
		return monthKey;
	}
	
	public static MonthKey fromString(String s) {
		String[] spl = s.split("/");
		return fromValues(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
	}
	
	public String toString() {
		return month + "/" + year;
	}

	public static MonthKey fromDate(LocalDate date) {
		return fromValues(date.getMonthValue(), date.getYear());
	}
	
	private LocalDate toDate() {
		return LocalDate.of(year, month, 1);
	}

	@Override
	public int compareTo(MonthKey monthKey) {
		return toDate().compareTo(monthKey.toDate());
	}

	public boolean isBefore(MonthKey monthKey) {
		return toDate().isBefore(monthKey.toDate());
	}
	
	public boolean isAfter(MonthKey monthKey) {
		return toDate().isAfter(monthKey.toDate());
	}
}