package de.gravitex.accounting.gui.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.gravitex.accounting.AccountingManager;

/**
 * JTable mit ComboBoxes im Header welche als Filter für die Tabelle dienen<br>
 * <br>
 * Erstellt: 2011-09-02
 * 
 * @author MaRk
 */
public class FilterTable2 extends JTable {

	private static final long serialVersionUID = -8474574634738370571L;
	
	private JTableHeader header;

	public FilterTable2() {
		super();
		// init();
	}

	@Override
	public void setModel(TableModel dataModel) {
		// TODO Auto-generated method stub
		super.setModel(dataModel);
		init();
	}

	private void init() {

		TableRowSorter<TableModel> tableRowSorter = new TableRowSorter<TableModel>(getModel());
		setRowSorter(tableRowSorter);
		setTableHeader(new FilterTableHeader(getColumnModel()));
		header = getTableHeader();
		tableHeader.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				System.out.println("click...");
				if (event.getClickCount() == 2) {
					editColumnAt(event.getPoint());
				}
			}
		});
		tableHeader.setLayout(new FilterLayoutManager());
		TableCellRenderer headerRendererTop = new FilterTableCellRenderer();
		// Für jede Spalte wird eine ComboBox hinzugefügt
		for (int columnIndex = 0; columnIndex < getModel().getColumnCount(); columnIndex++) {
			tableHeader.add(createComboBox(columnIndex), columnIndex);
			TableColumn column = getColumnModel().getColumn(columnIndex);
			column.setHeaderRenderer(headerRendererTop);
		}
		tableHeader.setEnabled(true);
	}
	
	@Override
	public Component getComponentAt(int x, int y) {
		// TODO Auto-generated method stub
		return new JComboBox<String>();
	}

	private void editColumnAt(Point p) {
		int columnIndex = header.columnAtPoint(p);
		if (columnIndex != -1) {
			TableColumn column = header.getColumnModel().getColumn(columnIndex);
			Rectangle columnRectangle = header.getHeaderRect(columnIndex);
		}
	}

	private Component createComboBox(int index) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		Set<String> allCategories = AccountingManager.getInstance().getAllPartners();
		for (String partner : allCategories) {
			model.addElement(partner);
		}
		JComboBox<String> combo = new JComboBox<String>();
		combo.setModel(model);
		combo.setEnabled(true);
		return combo;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
}