package de.gravitex.accounting.gui.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Properties;

import javax.swing.JPanel;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import de.gravitex.accounting.filter.FilterValue;
import de.gravitex.accounting.filter.interfacing.FilterValueProvider;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.filter.interfacing.IFilteredValueReceiver;
import de.gravitex.accounting.filter.util.LocalDateRange;

public class FromToDateFilter extends JPanel implements FilterValueProvider<LocalDateRange> {

	private static final long serialVersionUID = -9222184285488288619L;

	private JDatePickerImpl datePickerFrom;

	private JDatePickerImpl datePickerTo;

	private IFilteredValueReceiver filteredValueReceiver;
	
	private String attributeName;

	private FilteredValuesHolder filteredValuesHolder;

	public FromToDateFilter() {
		setLayout(new BorderLayout());
		datePickerFrom = getDatePicker();
		add(datePickerFrom, BorderLayout.NORTH);
		datePickerTo = getDatePicker();
		add(datePickerTo, BorderLayout.SOUTH);
		datePickerFrom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(attributeName, getSelectedFilterValue()));
				filteredValuesHolder.loadData();
			}
		});
		datePickerTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredValueReceiver.receiveFilterValue(FilterValue.fromValues(attributeName, getSelectedFilterValue()));
				filteredValuesHolder.loadData();
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
	public void setMvcData(IFilteredValueReceiver filteredValueReceiver, FilteredValuesHolder filteredValuesHolder,
			String attributeName) {
		this.filteredValueReceiver = filteredValueReceiver;
		this.filteredValuesHolder = filteredValuesHolder;
		this.attributeName = attributeName;
	}

	@Override
	public LocalDateRange getSelectedFilterValue() {
		return LocalDateRange.fromValues(parseDate(datePickerFrom), parseDate(datePickerTo));		
	}

	private LocalDate parseDate(JDatePickerImpl datePicker) {
		// TODO
		String text = datePicker.getJFormattedTextField().getText();
		if (text == null || text.length() == 0) {
			return null;
		}
		String[] textSplitted = text.split("\\.");
		LocalDate result = LocalDate.of(Integer.parseInt(textSplitted[2]), Integer.parseInt(textSplitted[1]), Integer.parseInt(textSplitted[0]));
		return result;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
	}
}