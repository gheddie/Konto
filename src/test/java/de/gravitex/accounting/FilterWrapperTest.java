package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.gravitex.accounting.filter.impl.EqualFilter;
import de.gravitex.accounting.filter.wrapper.FilterWrapper;

public class FilterWrapperTest {

	private static final String ATTR_STRING = "string";

	@Test
	public void testFilterWrapper() {

		List<FilterTestItem> testItems = new ArrayList<FilterTestItem>();
		testItems.add(new FilterTestItem("A", 11, LocalDate.of(2020, 2, 10)));
		testItems.add(new FilterTestItem("B", 11, LocalDate.of(2020, 2, 10)));
		testItems.add(new FilterTestItem("C", 11, LocalDate.of(2020, 2, 10)));
		
		FilterWrapper<FilterTestItem> complex = new FilterWrapper<FilterTestItem>().withFilter(ATTR_STRING, EqualFilter.class);
		
		assertEquals(3, complex.filterItems(testItems).size());
		
		complex.setFilter("string", "A");
		
		assertEquals(3, complex.filterItems(testItems).size());
	}
}