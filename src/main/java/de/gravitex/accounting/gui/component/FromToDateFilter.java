package de.gravitex.accounting.gui.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JPanel;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.FilterValueProvider;
import de.gravitex.accounting.filter.FilteredValueReceiver;

public class FromToDateFilter extends JPanel implements FilterValueProvider {

	private static final long serialVersionUID = -9222184285488288619L;

	private JDatePickerImpl datePickerFrom;

	private JDatePickerImpl datePickerTo;

	private FilteredValueReceiver filteredValueReceiver;

	public FromToDateFilter() {
		setLayout(new BorderLayout());
		datePickerFrom = getDatePicker();
		add(datePickerFrom, BorderLayout.NORTH);
		datePickerTo = getDatePicker();
		add(datePickerTo, BorderLayout.SOUTH);
		datePickerFrom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, getSelectedFilterValue()));
			}
		});
		datePickerTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(null, getSelectedFilterValue()));
			}
		});
	}

	private JDatePickerImpl getDatePicker() {

		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model, new Properties());
		JDatePickerImpl datePickerFrom = new JDatePickerImpl(datePanel, new DateComponentFormatter());
		return datePickerFrom;
	}

	@Override
	public void acceptFilterReceiver(FilteredValueReceiver filteredValueReceiver) {
		this.filteredValueReceiver = filteredValueReceiver;
	}

	@Override
	public Object getSelectedFilterValue() {
		return "KUH";
	}
}