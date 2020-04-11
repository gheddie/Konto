/*
 * Created by JFormDesigner on Fri Apr 10 19:24:19 CEST 2020
 */

package de.gravitex.accounting.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;

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
		btnCheckSaldo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					manager.saldoCheck();	
					JOptionPane.showMessageDialog(AccountingFrame.this, "Saldo OK!!");
				} catch (AccountingException accountingException) {
					JOptionPane.showMessageDialog(AccountingFrame.this, "Saldo error: " + accountingException.getMessage());
				}
			}
		});
		fillAccountingMonths();
		fillAllCategories();
		cbAllCategories.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fillAllCategoryEntries(cbAllCategories.getSelectedItem().toString());
			}

			private void fillAllCategoryEntries(String category) {
				categoryEntriesTable.setBackground(Color.WHITE);
				System.out.println("fillAllCategoryEntries : " + cbAllCategories.getSelectedItem());
				List<AccountingRow> allEntriesForCategory = AccountingManager.getInstance()
						.getAllEntriesForCategory(category);
			    DefaultTableModel tablemodel = new DefaultTableModel();
			    for (String col : AccountingResultCategoryModel.getHeaders()) {
			        tablemodel.addColumn(col);
			    }
			    BigDecimal sum = new BigDecimal(0);
			    for (AccountingRow row : allEntriesForCategory) {
			        tablemodel.addRow(row.asTableRow());
			        sum = sum.add(row.getAmount());
			    }
			    categoryEntriesTable.setModel(tablemodel);
				tfSum.setText(sum.toString());
			    tfBudget.setText("---");
			}
		});
	}

	private void fillAllCategories() {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		Set<String> allCategories = AccountingManager.getInstance().getAllCategories();
		for (String category : allCategories) {
			model.addElement(category);
		}
		cbAllCategories.setModel(model);
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
				fillCategoriesForMonth(monthModel.getDistinctCategories());
				BigDecimal overallSum = monthModel.calculateOverallSum();
				tfMonthOverall.setText(overallSum.toString());
				if (overallSum.intValue() > 0) {
					tfMonthOverall.setBackground(Color.WHITE);
				} else {
					tfMonthOverall.setBackground(Color.RED);
				}
			}

			private void fillCategoriesForMonth(Set<String> distinctCategories) {
				final DefaultListModel<String> categoriesByMonthModel = new DefaultListModel<String>();
				for (String category : distinctCategories) {
					categoriesByMonthModel.addElement(category);	
				}
				categoriesByMonthList.setModel(categoriesByMonthModel);
				categoriesByMonthList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						AccountingResultCategoryModel categoryModel = monthModel.getCategoryModel((String) categoriesByMonthList.getSelectedValue());
						/*
						System.out.println("entries [" + categoryModel.getAccountingResultModelRows().size()
								+ "] for month " + accountingMonthList.getSelectedValue() + " and category "
								+ categoriesByMonthList.getSelectedValue() + "...");
								*/
						fillCategoryEntries(categoryModel);
					}

					private void fillCategoryEntries(AccountingResultCategoryModel categoryModel) {
						
						if (categoryModel == null) {
							return;
						}
						
					    DefaultTableModel tablemodel = new DefaultTableModel();
					    for (String col : categoryModel.getHeaders()) {
					        tablemodel.addColumn(col);
					    }
					    for (AccountingResultModelRow row : categoryModel.getAccountingResultModelRows()) {
					        tablemodel.addRow(row.asTableRow());
					    }
					    categoryEntriesTable.setModel(tablemodel);
					    tfSum.setText(categoryModel.getSum().toString());
					    tfBudget.setText(categoryModel.getBudget() != null ? categoryModel.getBudget().toString() : "---");
					    
					    updateBudgetAlert(categoryModel.inBudget());
					}

					private void updateBudgetAlert(boolean inBudget) {
						if (!inBudget) {
							categoryEntriesTable.setBackground(Color.RED);
						} else {
							categoryEntriesTable.setBackground(Color.WHITE);
						}
					}
				});
			}
		});
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Stefan Schulz
		tbMain = new JToolBar();
		btnCheckSaldo = new JButton();
		scrollPane1 = new JScrollPane();
		accountingMonthList = new JList();
		scrollPane3 = new JScrollPane();
		categoriesByMonthList = new JList();
		label1 = new JLabel();
		cbAllCategories = new JComboBox();
		scrollPane4 = new JScrollPane();
		categoryEntriesTable = new JTable();
		lblSum = new JLabel();
		tfSum = new JTextField();
		lblBudget = new JLabel();
		tfBudget = new JTextField();
		scrollPane2 = new JScrollPane();
		taOutput = new JTextArea();
		label2 = new JLabel();
		tfMonthOverall = new JTextField();

		//======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {77, 236, 43, 0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 92, 0, 0, 0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4};

		//======== tbMain ========
		{
			tbMain.setEnabled(false);

			//---- btnCheckSaldo ----
			btnCheckSaldo.setText("Check Saldo");
			tbMain.add(btnCheckSaldo);
		}
		contentPane.add(tbMain, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(accountingMonthList);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== scrollPane3 ========
		{
			scrollPane3.setViewportView(categoriesByMonthList);
		}
		contentPane.add(scrollPane3, new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label1 ----
		label1.setText("Per Kategorie:");
		contentPane.add(label1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		contentPane.add(cbAllCategories, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane4 ========
		{
			scrollPane4.setViewportView(categoryEntriesTable);
		}
		contentPane.add(scrollPane4, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- lblSum ----
		lblSum.setText("Summe:");
		contentPane.add(lblSum, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- tfSum ----
		tfSum.setEditable(false);
		contentPane.add(tfSum, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- lblBudget ----
		lblBudget.setText("Budget:");
		contentPane.add(lblBudget, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- tfBudget ----
		tfBudget.setEditable(false);
		contentPane.add(tfBudget, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(taOutput);
		}
		contentPane.add(scrollPane2, new GridBagConstraints(0, 5, 4, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label2 ----
		label2.setText("Monatsabschluss:");
		contentPane.add(label2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

		//---- tfMonthOverall ----
		tfMonthOverall.setEditable(false);
		contentPane.add(tfMonthOverall, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0,
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
	private JToolBar tbMain;
	private JButton btnCheckSaldo;
	private JScrollPane scrollPane1;
	private JList accountingMonthList;
	private JScrollPane scrollPane3;
	private JList categoriesByMonthList;
	private JLabel label1;
	private JComboBox cbAllCategories;
	private JScrollPane scrollPane4;
	private JTable categoryEntriesTable;
	private JLabel lblSum;
	private JTextField tfSum;
	private JLabel lblBudget;
	private JTextField tfBudget;
	private JScrollPane scrollPane2;
	private JTextArea taOutput;
	private JLabel label2;
	private JTextField tfMonthOverall;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}