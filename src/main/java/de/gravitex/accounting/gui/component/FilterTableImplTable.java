package de.gravitex.accounting.gui.component;

import javax.swing.JTable;

public class FilterTableImplTable extends JTable {

	private static final long serialVersionUID = 5373511751240526654L;

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}