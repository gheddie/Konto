package de.gravitex.accounting;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LocalDateMarker {
	
	private LocalDate mark;
	
	private Boolean marked;
	
	private LocalDateMarker() {
		// ...
	}

	public static LocalDateMarker fromValues(LocalDate aMark) {
		LocalDateMarker localDateMarker = new LocalDateMarker();
		localDateMarker.setMark(aMark);
		localDateMarker.setMarked(false);
		return localDateMarker;
	}

	public void check() throws Exception {
		if (marked) {
			throw new Exception();
		}
		marked = true;
	}
}