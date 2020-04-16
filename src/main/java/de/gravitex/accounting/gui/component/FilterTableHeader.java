package de.gravitex.accounting.gui.component;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class FilterTableHeader extends JTableHeader {
	
	private static final long serialVersionUID = -6282737902633919021L;

	public FilterTableHeader(TableColumnModel columnModel) {
		super(columnModel);
	    setReorderingAllowed(false);
	    // cellEditor = null;
	    recreateTableColumn(columnModel);
	}
	
	  protected void recreateTableColumn(TableColumnModel columnModel) {
		    int n = columnModel.getColumnCount();
		    EditableHeaderTableColumn[] newCols = new EditableHeaderTableColumn[n];
		    TableColumn[] oldCols = new TableColumn[n];
		    for (int i = 0; i < n; i++) {
		      oldCols[i] = columnModel.getColumn(i);
		      newCols[i] = new EditableHeaderTableColumn();
		      newCols[i].copyValues(oldCols[i]);
		    }
		    for (int i = 0; i < n; i++) {
		      columnModel.removeColumn(oldCols[i]);
		    }
		    for (int i = 0; i < n; i++) {
		      columnModel.addColumn(newCols[i]);
		    }
		  }
}