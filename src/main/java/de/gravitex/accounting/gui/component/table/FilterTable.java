package de.gravitex.accounting.gui.component.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.filter.interfacing.FilteredComponentListener;
import de.gravitex.accounting.filter.interfacing.FilteredValuesHolder;
import de.gravitex.accounting.gui.component.table.listener.FilterTableListener;
import de.gravitex.accounting.model.AccountingResultCategoryModel;

public class FilterTable<T> extends JPanel implements FilteredValuesHolder, FilterTableListener {

	private static final long serialVersionUID = -3840627411445980560L;
	
	private FilterTableImpl<T> table;

	private JLabel entrySumLabel;

	private FilteredComponentListener filteredComponentListener;

	private TableModelGenerator<T> tableModelGenerator;

	private JScrollPane tableScroller;

	private List<?> data;

	public FilterTable() {
		
		super();
		setLayout(new GridBagLayout());
		table = new FilterTableImpl();
		entrySumLabel = new JLabel();
		tableScroller = new JScrollPane();
		setLayout(new GridBagLayout());
		((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0 };
		((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0, 0 };
		((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0, 1.0E-4 };
		((GridBagLayout) getLayout()).rowWeights = new double[] { 1.0, 0.0, 1.0E-4 };
		add(tableScroller, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
		add(entrySumLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		tableScroller.setViewportView(table);
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
	            if (filteredComponentListener != null) {
	            	filteredComponentListener.itemSelected(data.get(anchorSelectionIndex));	            	
	            }
			}
		});
		table.acceptFilterTableListener(this);
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
			entrySumLabel.setText(rowCount + " Eintr�ge geladen");			
		}
		if (filteredComponentListener != null) {
			filteredComponentListener.filterDataChanged();	
		}
	}

	public TableColumnModel getColumnModel() {
		return table.getColumnModel();
	}

	@Override
	public void acceptFilteredComponentListener(FilteredComponentListener aFilteredComponentListener) {
		this.filteredComponentListener = aFilteredComponentListener;
	}

	@Override
	public Color getRowColor(int row) {
		if (filteredComponentListener == null) {
			return null;
		}
		return filteredComponentListener.getRowColor(data.get(row));
	}

	@Override
	public void rowDoubleClicked(int row) {
		if (filteredComponentListener == null) {
			return;
		}
		filteredComponentListener.itemDoubleClicked(data.get(row));
	}
}