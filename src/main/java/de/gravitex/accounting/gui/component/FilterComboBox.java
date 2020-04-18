package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilterValueProvider;
import de.gravitex.accounting.filter.FilteredValueReceiver;

public class FilterComboBox extends JComboBox<String> implements FilterValueProvider {

	private static final long serialVersionUID = 9138045791347078807L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	public FilterComboBox() {
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
		return getSelectedItem();
	}
}