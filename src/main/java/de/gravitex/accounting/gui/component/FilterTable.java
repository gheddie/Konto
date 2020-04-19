package de.gravitex.accounting.gui.component;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.AccountingSingleton;
import de.gravitex.accounting.filter.interfacing.FilterDataChangedListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.model.AccountingResultCategoryModel;

public class FilterTable<T> extends JPanel implements FilteredValuesHolder {

	private static final long serialVersionUID = -3840627411445980560L;
	
	private FilterTableImplTable table;

	private JLabel entrySumLabel;

	private FilterDataChangedListener changeListener;
	
	public FilterTable() {
		super();
		setLayout(new BorderLayout());
		table = new FilterTableImplTable();
		add(table, BorderLayout.NORTH);
		entrySumLabel = new JLabel();
		add(entrySumLabel, BorderLayout.SOUTH);
	}

	@Override
	public void loadData() {
		
		List<AccountingRow> allEntries = AccountingSingleton.getInstance().getFilteredEntries();
		DefaultTableModel tablemodel = new DefaultTableModel();
		for (String col : AccountingResultCategoryModel.getHeadersFromUntil()) {
			tablemodel.addColumn(col);
		}
		BigDecimal sum = new BigDecimal(0);
		for (AccountingRow row : allEntries) {
			sum = sum.add(row.getAmount());
			tablemodel.addRow(row.asTableRow(true));
		}
		setModel(tablemodel);
	}

	public void setModel(DefaultTableModel tablemodel) {
		table.setModel(tablemodel);
		int rowCount = tablemodel.getRowCount();
		if (rowCount == 1) {
			entrySumLabel.setText(rowCount + " Eintrag geladen");
		} else {
			entrySumLabel.setText(rowCount + " Einträge geladen");			
		}
		if (changeListener != null) {
			changeListener.filterDataChanged();	
		}
	}

	public TableColumnModel getColumnModel() {
		return table.getColumnModel();
	}

	@Override
	public void acceptDataChagedListener(FilterDataChangedListener changeListener) {
		this.changeListener = changeListener;
	}
}