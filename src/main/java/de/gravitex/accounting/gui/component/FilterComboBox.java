package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilterValueProvider;
import de.gravitex.accounting.filter.interfacing.FilteredValueReceiver;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;

public class FilterComboBox<T> extends JComboBox<T> implements FilterValueProvider {

	private static final long serialVersionUID = 9138045791347078807L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;

	private FilteredValuesHolder filteredValuesHolder;
	
	public FilterComboBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(attributeName, getSelectedFilterValue()));
				filteredValuesHolder.loadData();
			}
		});
	}
	
	@Override
	public void setModel(ComboBoxModel<T> aModel) {
		DefaultComboBoxModel<T> extendedModel = new DefaultComboBoxModel<T>();
		extendedModel.addElement((T) EntityFilter.NO_FILTER);
		for (int index=0;index<aModel.getSize();index++) {
			extendedModel.addElement(aModel.getElementAt(index));
		}
		super.setModel(extendedModel);
	}

	@Override
	public void setMvcData(FilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder,
			String attributeName) {
		this.filteredValueReceiver = filteredValueReceiver;
		this.filteredValuesHolder = filteredValuesHolder;
		this.attributeName = attributeName;
	}

	@Override
	public Object getSelectedFilterValue() {
		return getSelectedItem();
	}

	@Override
	public List<?> loadData() {
		return null;
	}
}