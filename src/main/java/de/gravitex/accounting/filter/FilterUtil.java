package de.gravitex.accounting.filter;

import java.lang.reflect.InvocationTargetException;

public class FilterUtil {

	public static Object getAttributeValue(String attributeName, Object item) {
		try {
			String getterName = constructGetterName(attributeName);
			Object attributeValue = item.getClass().getMethod(getterName, null).invoke(item, null);
			return attributeValue;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	protected static String constructGetterName(String attributeName) {
		return "get" + firstToUpper(attributeName);
	}
	
	private static String firstToUpper(String attributeName) {
		return attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
	}
	
	public static boolean doValuesEqual(Object value1, Object value2) {
		
		if (value1 == null && value2 == null) {
			return true;
		}
		if (value1 != null && value2 == null) {
			return false;
		}
		if (value1 == null && value2 != null) {
			return false;
		}
		return value1.equals(value2);
	}
}