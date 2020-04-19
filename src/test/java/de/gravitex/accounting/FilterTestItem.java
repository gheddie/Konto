package de.gravitex.accounting;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FilterTestItem {

	private String string;
	
	private Integer integer;
	
	private LocalDate localDate;

	public FilterTestItem(String string, Integer integer, LocalDate localDate) {
		super();
		this.string = string;
		this.integer = integer;
		this.localDate = localDate;
	}
}