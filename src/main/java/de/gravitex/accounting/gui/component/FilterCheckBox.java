package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import de.gravitex.accounting.FilterValue;
import de.gravitex.accounting.FilterValueProvider;
import de.gravitex.accounting.FilteredValueReceiver;

public class FilterCheckBox extends JCheckBox implements FilterValueProvider {

	private static final long serialVersionUID = 3310334350858853081L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	public FilterCheckBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, isSelected()));
			}
		});
	}

	@Override
	public void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver) {
		this.filteredValueReceiver = filteredValueReceiver;
	}
}