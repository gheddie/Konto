package de.gravitex.accounting;

import lombok.Data;

@Data
public class FilterTestItem {

	private String string;
	
	private Integer integer;

	public FilterTestItem(String string, Integer integer) {
		super();
		this.string = string;
		this.integer = integer;
	}
}