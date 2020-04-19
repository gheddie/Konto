package de.gravitex.accounting.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.gravitex.accounting.filter.interfacing.IFilteredValueReceiver;

public abstract class FilteredValueReceiver<T> implements IFilteredValueReceiver {
	
	public abstract List<T> loadFilteredItems();

	@Override
	public Set<Object> loadDistinctItems(String attributeName) {
		Set<Object> result = new HashSet<Object>();
		Object attributeValue = null;
		for (T item : loadAllItems()) {
			attributeValue = FilterUitl.getAttributeValue(attributeName, item);
			if (attributeValue != null && !String.valueOf(attributeValue).isEmpty()) {
				result.add(attributeValue);				
			}
		}
		return result;
	}

	protected abstract List<T> loadAllItems();
}