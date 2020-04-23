package de.gravitex.accounting.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.filter.exception.FilterException;
import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EntityFilter<T> {
	
	private HashMap<String, Class<? extends AbstractItemFilter<?>>> filterDefinitions = new HashMap<String, Class<? extends AbstractItemFilter<?>>>();
	
	private HashMap<String, AbstractItemFilter<T>> filters = new HashMap<String, AbstractItemFilter<T>>();
	
	public static final String NO_FILTER = "[kein Eintrag]";
	
	public List<T> filterItems(List<T> itemsToFilter) {
		List<T> result = new ArrayList<T>();
		for (T item : itemsToFilter) {
			if (matchesFilters(item)) {
				result.add(item);
			}	
		}
		return result;
	}
	
	public EntityFilter<T> setFilter(String attributeName, Object value) {
		AbstractItemFilter filter = assertFilterProduced(attributeName);
		if (filter.isNullValue(value)) {
			resetFilter(attributeName);
		} else {
			filter.setActive(true);
			filter.setFilterValue(value);	
		}
		return this;
	}

	private boolean matchesFilters(T item) {
		for (AbstractItemFilter filter : filters.values()) {
			if (filter.isActive() && !filter.accept(item)) {
				return false;
			}
		}
		return true;
	}
	
	public EntityFilter<T> registerFilter(String attributeName, Class<? extends AbstractItemFilter<?>> filterClass) {
		filterDefinitions.put(attributeName, filterClass);
		return this;
	}
	
	public EntityFilter<T> registerFilter(FilterDefinition filterDefinition) {
		return registerFilter(filterDefinition.getAttributeName(), filterDefinition.getFilterClass());
	}

	private void resetFilter(String attributeName) {
		assertFilterProduced(attributeName).setActive(false);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	private AbstractItemFilter<T> assertFilterProduced(String attributeName) {
		if (filters.get(attributeName) != null) {
			return filters.get(attributeName);
		}
		try {
			if (filterDefinitions.get(attributeName) == null) {
				throw new FilterException("no filter defined for attribute [" + attributeName + "]!!", null);	
			}
			AbstractItemFilter<T> constructedFilter = (AbstractItemFilter<T>) filterDefinitions.get(attributeName).newInstance();
			constructedFilter.setAttributeName(attributeName);
			filters.put(attributeName, constructedFilter);
			return constructedFilter;
		} catch (Exception e) {
			throw new FilterException("unable to construct filter defined for attribute [" + attributeName + "]!!", e);
		}
	}
}