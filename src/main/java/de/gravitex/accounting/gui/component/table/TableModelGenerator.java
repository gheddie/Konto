package de.gravitex.accounting.gui.component.table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.DisplayValue;
import de.gravitex.accounting.filter.FilterUtil;

public class TableModelGenerator<T> {

	private List<T> data;
	
	private Class<T> entityClass;
	
	private List<String> headers = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public TableModelGenerator(List<T> aData, String[] aHeaders) {
		super();
		this.data = aData;
		this.entityClass = (data != null && data.size() > 0) ? (Class<T>) data.get(0).getClass() : null;
	}

	public DefaultTableModel generate() {
		
		if (entityClass == null) {
			return new DefaultTableModel();
		}
		
		DefaultTableModel model = new DefaultTableModel();
		// headers
		for (Field f : entityClass.getDeclaredFields()) {
			if (f.isAnnotationPresent(DisplayValue.class)) {
				headers.add(f.getAnnotation(DisplayValue.class).header());
				model.addColumn(f.getAnnotation(DisplayValue.class).header());
			}
		}
		// data
		for (Integer rowIndex = 0; rowIndex < data.size(); rowIndex++) {
			List<Object> generatedRow = generateRow(data.get(rowIndex));
			model.addRow(generatedRow.toArray(new Object[generatedRow.size()]));
		}
		return model;
	}

	private List<Object> generateRow(T t) {

		List<Object> rowValues = new ArrayList<Object>();
		for (Field field : entityClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(DisplayValue.class)) {
				rowValues.add(FilterUtil.getAttributeValue(field.getName(), t));	
			}
		}
		return rowValues;
	}
}