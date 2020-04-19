package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.impl.EqualFilter;
import de.gravitex.accounting.gui.component.FilterComboBox;
import de.gravitex.accounting.util.FakedValueHolder;
import de.gravitex.accounting.util.FilterTestData;

public class FilterTest {
	
	private static final FilterTestData filterTestData = new FilterTestData();
	static {
		
		List<FilterTestItem> testItems = new ArrayList<FilterTestItem>();
		
		testItems.add(new FilterTestItem("A", 11, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("A", 12, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("A", 13, LocalDate.of(2020, 2, 20)));
		
		testItems.add(new FilterTestItem("B", 11, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("B", 12, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("B", 13, LocalDate.of(2020, 2, 20)));
		
		testItems.add(new FilterTestItem("C", 11, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("C", 12, LocalDate.of(2020, 2, 20)));
		testItems.add(new FilterTestItem("C", 13, LocalDate.of(2020, 2, 20)));
		
		filterTestData.setFilterTestItems(testItems);
	}

	@Test
	public void testSimpleFilters() {
		
		EntityFilter<FilterTestItem> entityFilter = new EntityFilter<FilterTestItem>();
		
		// ---
		
		FakedValueHolder fakedValueHolder = new FakedValueHolder();
		
		// set up components
		FilterComboBox<String> filterString = new FilterComboBox<String>();
		filterString.setMvcData(filterTestData, fakedValueHolder, FilterTestData.ATTR_STRING);
		
		FilterComboBox<Integer> filterInteger = new FilterComboBox<Integer>();
		filterInteger.setMvcData(filterTestData, fakedValueHolder, FilterTestData.ATTR_INTEGER);
		
		// ---
		
		// register filters
		entityFilter.registerFilter(new EqualFilter(FilterTestData.ATTR_STRING));
		entityFilter.registerFilter(new EqualFilter(FilterTestData.ATTR_INTEGER));
		
		List<FilterTestItem> testData = filterTestData.getFilterTestItems();
		
		entityFilter.setFilter(FilterTestData.ATTR_STRING, "A");
		assertEquals(3, entityFilter.filterItems(testData).size());
		
		entityFilter.setFilter(FilterTestData.ATTR_INTEGER, 11);
		assertEquals(1, entityFilter.filterItems(testData).size());
		
		entityFilter.setFilter(FilterTestData.ATTR_STRING, EntityFilter.NO_FILTER);
		assertEquals(3, entityFilter.filterItems(testData).size());
	}
}