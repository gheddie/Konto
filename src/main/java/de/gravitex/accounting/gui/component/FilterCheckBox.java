package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilterValueProvider;
import de.gravitex.accounting.filter.interfacing.FilteredValueReceiver;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;

public class FilterCheckBox extends JCheckBox implements FilterValueProvider {

	private static final long serialVersionUID = 3310334350858853081L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;

	private FilteredValuesHolder filteredValuesHolder;
	
	public FilterCheckBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, getSelectedFilterValue()));
				filteredValuesHolder.loadData();
			}
		});
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
		return isSelected();
	}

	@Override
	public List<?> loadData() {
		return filteredValueReceiver.loadDistinctItems(attributeName);
	}
}