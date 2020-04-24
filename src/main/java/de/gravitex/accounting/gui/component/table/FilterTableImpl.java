package de.gravitex.accounting.gui.component.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 2) {
	                int selectedRow = ((FilterTableImpl<T>) e.getSource()).getSelectedRow();
	                filterTableListener.rowDoubleClicked(selectedRow);
	            } else {
	            	// ...
	            }
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