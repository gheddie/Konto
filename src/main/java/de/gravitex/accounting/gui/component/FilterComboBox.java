package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilterValueProvider;
import de.gravitex.accounting.filter.FilteredValueReceiver;

public class FilterComboBox extends JComboBox<String> implements FilterValueProvider {

	private static final long serialVersionUID = 9138045791347078807L;
	
	private FilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;
	
	public FilterComboBox() {
		super();
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(attributeName, getSelectedFilterValue()));
			}
		});
	}
	
	@Override
	public void setModel(ComboBoxModel<String> aModel) {
		DefaultComboBoxModel<String> extendedModel = new DefaultComboBoxModel<String>();
		extendedModel.addElement(EntityFilter.NO_FILTER);
		for (int index=0;index<aModel.getSize();index++) {
			extendedModel.addElement(aModel.getElementAt(index));
		}
		super.setModel(extendedModel);
	}

	@Override
	public void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver) {
		this.filteredValueReceiver = filteredValueReceiver;
	}

	@Override
	public Object getSelectedFilterValue() {
		return getSelectedItem();
	}
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}