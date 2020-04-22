package de.gravitex.accounting.gui.component.table;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.filter.interfacing.FilteredComponentListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.model.AccountingResultCategoryModel;

public class FilterTable<T> extends JPanel implements FilteredValuesHolder {

	private static final long serialVersionUID = -3840627411445980560L;
	
	private FilterTableImpl table;

	private JLabel entrySumLabel;

	private FilteredComponentListener changeListener;

	private TableModelGenerator<T> tableModelGenerator;

	private JScrollPane tableScroller;

	private List<?> data;

	public FilterTable() {
		
		super();
		setLayout(new BorderLayout());
		table = new FilterTableImpl();
		tableScroller = new JScrollPane();
		// tableScroller.setLayout(new ScrollPaneLayout());
		add(tableScroller, BorderLayout.NORTH);
		tableScroller.setViewportView(table);
		entrySumLabel = new JLabel();
		add(entrySumLabel, BorderLayout.SOUTH);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
	            if (e.getValueIsAdjusting()) {
	            	return;	            	
	            }
	            final DefaultListSelectionModel target = (DefaultListSelectionModel)e.getSource();
	            int anchorSelectionIndex = target.getAnchorSelectionIndex();
	            if (anchorSelectionIndex < 0) {
	            	return;	            	
	            }
				changeListener.itemSelected(data.get(anchorSelectionIndex));
			}
		});
	}

	@Override
	public void loadData() {
		setData(AccountingSingleton.getInstance().getFilteredEntries());
	}
	
	@SuppressWarnings("unchecked")
	public void setData(List<?> aData) {
	    	    
		this.data = aData;
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
	public void acceptDataChagedListener(FilteredComponentListener changeListener) {
		this.changeListener = changeListener;
	}
}