package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.impl.DateRangeFilter;
import de.gravitex.accounting.filter.impl.EqualFilter;
import de.gravitex.accounting.filter.util.LocalDateRange;
import de.gravitex.accounting.gui.component.FilterComboBox;
import de.gravitex.accounting.gui.component.FromToDateFilter;
import de.gravitex.accounting.util.FakedValueHolder;
import de.gravitex.accounting.util.FilterTestDataProvider;

public class FilterTest {
	
	private static final Logger logger = Logger.getLogger(FilterTest.class);
	
	private static final FilterTestDataProvider filterTestData = new FilterTestDataProvider();
	static {
		
		List<FilterTestItem> testItems = new ArrayList<FilterTestItem>();
		
		testItems.add(new FilterTestItem("A", 11, LocalDate.of(2021, 2, 20)));
		testItems.add(new FilterTestItem("A", 12, LocalDate.of(2022, 2, 20)));
		testItems.add(new FilterTestItem("A", 13, LocalDate.of(2023, 2, 20)));
		
		testItems.add(new FilterTestItem("B", 11, LocalDate.of(2024, 2, 20)));
		testItems.add(new FilterTestItem("B", 12, LocalDate.of(2025, 2, 20)));
		testItems.add(new FilterTestItem("B", 13, LocalDate.of(2026, 2, 20)));
		
		testItems.add(new FilterTestItem("C", 11, LocalDate.of(2027, 2, 20)));
		testItems.add(new FilterTestItem("C", 12, LocalDate.of(2028, 2, 20)));
		testItems.add(new FilterTestItem("C", 13, LocalDate.of(2029, 2, 20)));
		
		filterTestData.setFilterTestItems(testItems);
	}
	
	@Test
	public void testDateFilter() {
		
		EntityFilter<FilterTestItem> entityFilter = new EntityFilter<FilterTestItem>();
		
		// register filters
		entityFilter.registerFilter(new DateRangeFilter(FilterTestDataProvider.ATTR_LOCAL_DATE));
		
		// set up components
		FromToDateFilter filterLocalDate = new FromToDateFilter();
		FakedValueHolder fakedValueHolder = new FakedValueHolder();
		filterLocalDate.setMvcData(filterTestData, fakedValueHolder, FilterTestDataProvider.ATTR_LOCAL_DATE);
		filterLocalDate.initData();
		
		entityFilter.setFilter(FilterTestDataProvider.ATTR_LOCAL_DATE, LocalDateRange.fromValues(LocalDate.of(2024, 2, 20), LocalDate.of(2026, 2, 20)));
		
		assertEquals(3, entityFilter.filterItems(filterTestData.getFilterTestItems()).size());
	}

	@Test
	public void testSimpleFilters() {
		
		EntityFilter<FilterTestItem> entityFilter = new EntityFilter<FilterTestItem>();
		
		FakedValueHolder fakedValueHolder = new FakedValueHolder();
		
		// set up components
		FilterComboBox<String> filterString = new FilterComboBox<String>();
		filterString.setMvcData(filterTestData, fakedValueHolder, FilterTestDataProvider.ATTR_STRING);
		
		FilterComboBox<Integer> filterInteger = new FilterComboBox<Integer>();
		filterInteger.setMvcData(filterTestData, fakedValueHolder, FilterTestDataProvider.ATTR_INTEGER);
		
		// load data for components
		filterString.initData();
		// including 'no entry' string...
		assertEquals(4, filterString.getModel().getSize());
		
		// load data for components
		filterInteger.initData();
		// including 'no entry' string...
		assertEquals(4, filterInteger.getModel().getSize());
		
		// ---
		
		// register filters
		entityFilter.registerFilter(new EqualFilter(FilterTestDataProvider.ATTR_STRING));
		entityFilter.registerFilter(new EqualFilter(FilterTestDataProvider.ATTR_INTEGER));
		
		List<FilterTestItem> testData = filterTestData.getFilterTestItems();
		
		entityFilter.setFilter(FilterTestDataProvider.ATTR_STRING, "A");
		assertEquals(3, entityFilter.filterItems(testData).size());
		
		entityFilter.setFilter(FilterTestDataProvider.ATTR_INTEGER, 11);
		assertEquals(1, entityFilter.filterItems(testData).size());
		
		entityFilter.setFilter(FilterTestDataProvider.ATTR_STRING, EntityFilter.NO_FILTER);
		assertEquals(3, entityFilter.filterItems(testData).size());
	}
}