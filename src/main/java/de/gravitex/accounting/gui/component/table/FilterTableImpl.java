package de.gravitex.accounting.gui.component.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class FilterTableImpl extends JTable {

	private static final long serialVersionUID = 9146563169960538428L;
	
	public FilterTableImpl() {
		super();
		getTableHeader().setReorderingAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		/*
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		    {
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
		        return c;
		    }
		});
		*/
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}