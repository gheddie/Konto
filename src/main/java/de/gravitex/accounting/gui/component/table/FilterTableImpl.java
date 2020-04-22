package de.gravitex.accounting.gui.component.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class FilterTableImpl extends JTable {

	private static final long serialVersionUID = 9146563169960538428L;
	
	public FilterTableImpl() {
		super();
		getTableHeader().setReorderingAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}