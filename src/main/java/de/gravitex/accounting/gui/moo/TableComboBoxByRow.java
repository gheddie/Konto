package de.gravitex.accounting.gui.moo;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.gui.component.FilterTable;

public class TableComboBoxByRow extends JFrame {

	private static final long serialVersionUID = 8452398310733919942L;
	
	public TableComboBoxByRow() {

		Object[][] data = { { "Color", "Red" }, { "Shape", "Square" }, { "Fruit", "Banana" }, { "Plain", "Text" } };
		String[] columnNames = { "Type", "Value" };
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		
		FilterTable table = new FilterTable();
		table.setModel(model);
		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane);
	}
	
	public static void main(String[] args) {
		TableComboBoxByRow frame = new TableComboBoxByRow();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}