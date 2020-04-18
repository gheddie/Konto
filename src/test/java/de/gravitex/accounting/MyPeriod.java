package de.gravitex.accounting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MyPeriod {
	
	private LocalDate start;
	
	private LocalDate end;

	private MyPeriod() {
		// ...
	}

	public static MyPeriod fromValues(LocalDate aStart, LocalDate aEnd) {
		MyPeriod myPeriod = new MyPeriod();
		myPeriod.setStart(aStart);
		myPeriod.setEnd(aEnd);
		return myPeriod;
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