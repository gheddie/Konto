package de.gravitex.accounting.filter.wrapper;

import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class FilterWrapper<T> {

	private HashMap<String, Class<? extends AbstractItemFilter<?>>> filterDefinitions = new HashMap<String, Class<? extends AbstractItemFilter<?>>>();
	
	private HashMap<String, AbstractItemFilter<T>> filters = new HashMap<String, AbstractItemFilter<T>>();
	
	private EntityFilter<T> entityFilter = new EntityFilter<T>();

	public FilterWrapper<T> withFilter(String attributeName, Class<? extends AbstractItemFilter<?>> filterClass) {
		filterDefinitions.put(attributeName, filterClass);
		return this;
	}

	public List<T> filterItems(List<T> items) {
		return items;
	}

	public void setFilter(String attributeName, Object filterValue) {
		assertFilterProduced(attributeName);
		// TODO Auto-generated method stub
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void assertFilterProduced(String attributeName) {
		if (filters.get(attributeName) != null) {
			return;
		}
		try {
			if (filterDefinitions.get(attributeName) == null) {
				throw new FilterWrapperException("no filter defined for attribute [" + attributeName + "]!!");	
			}
			filters.put(attributeName, (AbstractItemFilter<T>) filterDefinitions.get(attributeName).newInstance());			
		} catch (Exception e) {
			throw new FilterWrapperException("unable to construct filter for attribute [" + attributeName + "]!!");
		}
	}
}