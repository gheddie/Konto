package de.gravitex.accounting.gui.component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class FilterTable extends JTable {

	private static final long serialVersionUID = -5818024539868172613L;
	
	List<TableCellEditor> editors = new ArrayList<TableCellEditor>();
	
	List<TableCellRenderer> renderers = new ArrayList<TableCellRenderer>();

	public FilterTable() {
		
		super();
		
		editors.add(new DefaultCellEditor(new JTextField()));
		renderers.add(new MooTableCellRenderer());
	}

	public FilterTable(DefaultTableModel model) {
		super(model);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (row == 0)
			return renderers.get(row);
		else
			return super.getCellRenderer(row, column);
	}

	public TableCellEditor getCellEditor(int row, int column) {
		if (row == 0)
			return editors.get(row);
		else
			return super.getCellEditor(row, column);
	}
}