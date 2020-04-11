/*
 * Created by JFormDesigner on Fri Apr 10 19:24:19 CEST 2020
 */

package de.gravitex.accounting.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingResultMonthModel;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;

/**
 * @author Stefan Schulz
 */
public class AccountingFrame extends JFrame {
	
	private static final long serialVersionUID = -8241085588080811229L;
	
	private AccountingManager manager;

	protected AccountingResultMonthModel monthModel;

	public AccountingFrame() {
		initComponents();
		manager = AccountingManager.getInstance();
		accountingMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoriesByMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fillAccountingMonths();
	}

	@SuppressWarnings("unchecked")
	private void fillAccountingMonths() {
		final DefaultListModel<String> monthKeyModel = new DefaultListModel<String>();
		for (String monthKey : manager.getResult().keySet()) {
			monthKeyModel.addElement(monthKey);	
		}
		accountingMonthList.setModel(monthKeyModel);
		accountingMonthList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				System.out.println(accountingMonthList.getSelectedValue());
				String monthKey = String.valueOf(accountingMonthList.getSelectedValue());
				monthModel = manager.getAccountingResultMonthModel(monthKey);
				fillCategoriesForMoth(monthModel.getDistinctCategories());
			}

			private void fillCategoriesForMoth(Set<String> distinctCategories) {
				final DefaultListModel<String> categoriesByMonthModel = new DefaultListModel<String>();
				for (String category : distinctCategories) {
					categoriesByMonthModel.addElement(category);	
				}
				categoriesByMonthList.setModel(categoriesByMonthModel);
				categoriesByMonthList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						AccountingResultCategoryModel categoryModel = monthModel.getCategoryModel((String) categoriesByMonthList.getSelectedValue());
						System.out.println("entries [" + categoryModel.getAccountingResultModelRows().size()
								+ "] for month " + accountingMonthList.getSelectedValue() + " and category "
								+ categoriesByMonthList.getSelectedValue() + "...");
						fillCategoryEntries(categoryModel);
					}

					private void fillCategoryEntries(AccountingResultCategoryModel categoryModel) {
						
					    DefaultTableModel tablemodel = new DefaultTableModel();
					    // tablemodel.setRowCount(0);
					    for (String col : categoryModel.getHeaders()) {
					        tablemodel.addColumn(col);
					    }
					    for (AccountingResultModelRow row : categoryModel.getAccountingResultModelRows()) {
					        tablemodel.addRow(row.asTableRow());
					    }
					    categoryEntriesTable.setModel(tablemodel);
					}
				});
			}
		});
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Stefan Schulz
		scrollPane1 = new JScrollPane();
		accountingMonthList = new JList();
		scrollPane3 = new JScrollPane();
		categoriesByMonthList = new JList();
		button1 = new JButton();
		scrollPane4 = new JScrollPane();
		categoryEntriesTable = new JTable();
		scrollPane2 = new JScrollPane();
		taOutput = new JTextArea();

		//======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {211, 0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {92, 0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0, 1.0E-4};

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(accountingMonthList);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== scrollPane3 ========
		{
			scrollPane3.setViewportView(categoriesByMonthList);
		}
		contentPane.add(scrollPane3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- button1 ----
		button1.setText("Check Saldo");
		contentPane.add(button1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane4 ========
		{
			scrollPane4.setViewportView(categoryEntriesTable);
		}
		contentPane.add(scrollPane4, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(taOutput);
		}
		contentPane.add(scrollPane2, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	public static void main(String[] args) {
		AccountingFrame accountingFrame = new AccountingFrame();
		accountingFrame.setSize(new Dimension(800, 600));
		accountingFrame.setVisible(true);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Stefan Schulz
	private JScrollPane scrollPane1;
	private JList accountingMonthList;
	private JScrollPane scrollPane3;
	private JList categoriesByMonthList;
	private JButton button1;
	private JScrollPane scrollPane4;
	private JTable categoryEntriesTable;
	private JScrollPane scrollPane2;
	private JTextArea taOutput;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}