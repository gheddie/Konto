package de.gravitex.accounting.gui.component;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.AccountingSingleton;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.model.AccountingResultCategoryModel;

public class FilterTable extends JTable implements FilteredValuesHolder {

	private static final long serialVersionUID = -3840627411445980560L;

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
}