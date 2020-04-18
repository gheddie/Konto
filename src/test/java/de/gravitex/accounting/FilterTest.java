package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.impl.EqualFilter;

public class FilterTest {

	private static final String ATTR_STRING = "string";
	
	private static final String ATTR_INTEGER = "integer";

	@Test
	public void testFilter() {
		
		EntityFilter<FilterTestItem> entityFilter = new EntityFilter<FilterTestItem>();
		
		entityFilter.registerFilter(new EqualFilter(ATTR_STRING));
		entityFilter.registerFilter(new EqualFilter(ATTR_INTEGER));
		
		List<FilterTestItem> testItems = getTestItems();
		
		entityFilter.setFilter(ATTR_STRING, "A");
		assertEquals(3, entityFilter.filterItems(testItems).size());
		
		entityFilter.setFilter(ATTR_INTEGER, 11);
		assertEquals(1, entityFilter.filterItems(testItems).size());
		
		entityFilter.setFilter(ATTR_STRING, EntityFilter.NO_FILTER);
		assertEquals(3, entityFilter.filterItems(testItems).size());
	}
	
	private List<FilterTestItem> getTestItems() {
		
		List<FilterTestItem> testItems = new ArrayList<FilterTestItem>();
		
		testItems.add(new FilterTestItem("A", 11));
		testItems.add(new FilterTestItem("A", 12));
		testItems.add(new FilterTestItem("A", 13));
		
		testItems.add(new FilterTestItem("B", 11));
		testItems.add(new FilterTestItem("B", 12));
		testItems.add(new FilterTestItem("B", 13));
		
		testItems.add(new FilterTestItem("C", 11));
		testItems.add(new FilterTestItem("C", 12));
		testItems.add(new FilterTestItem("C", 13));
		
		return testItems;
	}
}