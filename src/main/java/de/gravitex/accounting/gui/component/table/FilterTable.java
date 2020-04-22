package de.gravitex.accounting.gui.component.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.filter.interfacing.FilterDataChangedListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.model.AccountingResultCategoryModel;

public class FilterTable<T> extends JPanel implements FilteredValuesHolder {

	private static final long serialVersionUID = -3840627411445980560L;
	
	private FilterTableImpl table;

	private JLabel entrySumLabel;

	private FilterDataChangedListener changeListener;

	private TableModelGenerator<T> tableModelGenerator;

	// private JScrollPane tableScroller;

	public FilterTable() {
		super();
		setLayout(new BorderLayout());
		table = new FilterTableImpl();
		
		/*
		tableScroller = new JScrollPane();
		tableScroller.setLayout(new ScrollPaneLayout());
		add(tableScroller, BorderLayout.NORTH);
		tableScroller.add(table);
		*/
		
		add(table, BorderLayout.NORTH);
		
		entrySumLabel = new JLabel();
		add(entrySumLabel, BorderLayout.SOUTH);
		
		/*
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setPreferredSize(new Dimension(0, 10));
		table.getTableHeader().setDefaultRenderer(renderer);
		*/
	}

	@Override
	public void loadData() {
		setData(AccountingSingleton.getInstance().getFilteredEntries());
	}
	
	@SuppressWarnings("unchecked")
	public void setData(List<?> aData) {
		
		/*
	    String[][] rowData = {
	    	    { "Japan", "245" }, { "USA", "240" }, { "Italien", "220" },
	    	    { "Spanien", "217" }, {"Türkei", "215"} ,{ "England", "214" },
	    	    { "Frankreich", "190" }, {"Griechenland", "185" },
	    	    { "Deutschland", "180" }, {"Portugal", "170" }
	    	    };

	    	    String[] columnNames =  {
	    	      "Land", "Durchschnittliche Fernsehdauer pro Tag in Minuten"
	    	    };
		
	    	    setModel(new DefaultTableModel(rowData, columnNames));
	    	    */
	    	    
		tableModelGenerator = new TableModelGenerator<T>((List<T>) aData,
				AccountingResultCategoryModel.getHeadersFromUntil());
		setModel(tableModelGenerator.generate());
	}

	private void setModel(DefaultTableModel tablemodel) {
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