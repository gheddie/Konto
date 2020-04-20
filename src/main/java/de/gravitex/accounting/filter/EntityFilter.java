package de.gravitex.accounting.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.accounting.filter.impl.base.AbstractItemFilter;

public class EntityFilter<T> {
	
	private HashMap<String, AbstractItemFilter> registeredFilters = new HashMap<String, AbstractItemFilter>();
	
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
	
	public void setFilter(String attributeName, Object value) {
		AbstractItemFilter filter = registeredFilters.get(attributeName);
		if (filter.isNullValue(value)) {
			resetFilter(attributeName);
		} else {
			filter.setActive(true);
			filter.setFilterValue(value);	
		}
	}

	private boolean matchesFilters(T item) {
		for (AbstractItemFilter filter : registeredFilters.values()) {
			if (filter.isActive() && !filter.accept(item)) {
				return false;
			}
		}
		return true;
	}
	
	public void registerFilter(AbstractItemFilter filter) {
		registeredFilters.put(filter.getAttributeName(), filter);
	}

	private void resetFilter(String attributeName) {
		registeredFilters.get(attributeName).setActive(false);
	}
}