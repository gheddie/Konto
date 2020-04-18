package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilterValueProvider;
import de.gravitex.accounting.filter.FilteredValueReceiver;
import lombok.Data;

@Data
public class FilterCheckBox extends JCheckBox implements FilterValueProvider {

	private static final long serialVersionUID = 3310334350858853081L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;
	
	public FilterCheckBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, getSelectedFilterValue()));
			}
		});
	}

	@Override
	public void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver) {
		this.filteredValueReceiver = filteredValueReceiver;
	}

	@Override
	public Object getSelectedFilterValue() {
		return isSelected();
	}
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}