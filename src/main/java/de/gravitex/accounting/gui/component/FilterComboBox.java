package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import de.gravitex.accounting.FilterValue;
import de.gravitex.accounting.FilterValueProvider;
import de.gravitex.accounting.FilteredValueReceiver;

public class FilterComboBox extends JComboBox<String> implements FilterValueProvider {

	private static final long serialVersionUID = 9138045791347078807L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	public FilterComboBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, getSelectedItem()));
			}
		});
	}

	@Override
	public void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver) {
		this.filteredValueReceiver = filteredValueReceiver;
	}
}