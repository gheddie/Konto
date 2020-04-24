package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilterValueProvider;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.filter.interfacing.IFilteredValueReceiver;

public class FilterCheckBox extends JCheckBox implements FilterValueProvider<Boolean> {

	private static final long serialVersionUID = 3310334350858853081L;
	
	private IFilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;

	private FilteredValuesHolder filteredValuesHolder;
	
	public FilterCheckBox() {
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
	public void setMvcData(IFilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder,
			String attributeName) {
		this.filteredValueReceiver = filteredValueReceiver;
		this.filteredValuesHolder = filteredValuesHolder;
		this.attributeName = attributeName;
	}

	@Override
	public Boolean getSelectedFilterValue() {
		return isSelected();
	}

	@Override
	public void initData() {
		filteredValueReceiver.loadDistinctItems(attributeName);
	}
}