package de.gravitex.accounting.gui.component.table;

import javax.swing.JTable;

public class FilterTableImpl extends JTable {

	private static final long serialVersionUID = 9146563169960538428L;
	
	public FilterTableImpl() {
		super();
		getTableHeader().setReorderingAllowed(false);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}