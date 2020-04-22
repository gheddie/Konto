package de.gravitex.accounting.gui.component.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import de.gravitex.accounting.gui.component.table.listener.FilterTableListener;

public class FilterTableImpl<T> extends JTable {

	private static final long serialVersionUID = 9146563169960538428L;
	
	private FilterTableListener filterTableListener;
	
	public FilterTableImpl() {
		super();
		getTableHeader().setReorderingAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		    {
		        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		        c.setBackground(filterTableListener.getRowColor(row));
		        return c;
		    }
		});
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void acceptFilterTableListener(FilterTableListener aFilterTableListener) {
		this.filterTableListener = aFilterTableListener;
	}
}