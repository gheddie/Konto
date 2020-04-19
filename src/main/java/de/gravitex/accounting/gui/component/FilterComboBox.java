package de.gravitex.accounting.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.apache.log4j.Logger;

import de.gravitex.accounting.filter.EntityFilter;
import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilterValueProvider;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.filter.interfacing.IFilteredValueReceiver;

public class FilterComboBox<T> extends JComboBox<T> implements FilterValueProvider<Object> {

	private static final long serialVersionUID = 9138045791347078807L;
	
	private static final Logger logger = Logger.getLogger(FilterComboBox.class);
	
	private IFilteredValueReceiver filteredValueReceiver;
	
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
	public void setMvcData(IFilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder,
			String attributeName) {
		this.filteredValueReceiver = filteredValueReceiver;
		this.filteredValuesHolder = filteredValuesHolder;
		this.attributeName = attributeName;
	}

	@Override
	public Object getSelectedFilterValue() {
		return getSelectedItem();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData() {
		logger.debug("loading data...");
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
		for (Object o : filteredValueReceiver.loadDistinctItems(attributeName)) {
			model.addElement((T) o);
		}
		setModel(model);
	}
}