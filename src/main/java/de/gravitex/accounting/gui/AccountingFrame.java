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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.wrapper.CategoryWrapper;

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
					pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.OK, "Saldo OK!!").getAlertMessages());
				} catch (AccountingException accountingException) {
					pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, "Saldo error: " + accountingException.getMessage()).getAlertMessages());
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
				fillCategoriesForMonth(monthModel);
				BigDecimal overallSum = monthModel.calculateOverallSum();
				tfMonthOverall.setText(overallSum.toString());
				if (overallSum.intValue() > 0) {
					tfMonthOverall.setBackground(Color.WHITE);
				} else {
					tfMonthOverall.setBackground(Color.RED);
				}
			}

			private void fillCategoriesForMonth(AccountingResultMonthModel accountingResultMonthModel) {
				final DefaultListModel<CategoryWrapper> categoriesByMonthModel = new DefaultListModel<CategoryWrapper>();
				for (String category : accountingResultMonthModel.getDistinctCategories()) {
					categoriesByMonthModel.addElement(CategoryWrapper.fromValues(category, manager.initPaymentModality(accountingResultMonthModel.getMonthKey(), category)));	
				}
				categoriesByMonthList.setModel(categoriesByMonthModel);
				categoriesByMonthList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						CategoryWrapper categoryWrapper = (CategoryWrapper) categoriesByMonthList.getSelectedValue();
						AccountingResultCategoryModel categoryModel = monthModel.getCategoryModel(
								categoryWrapper.getCategory());
						fillCategoryEntries(categoryModel);
						updatePaymentModality(categoryWrapper.getPaymentModality());
						if (manager.getAccountManagerSettings().isBudgetProjectionsEnabled()) {
							List<String> evaluationResult = manager.evaluateBudgetProjection(categoryWrapper);
							if (evaluationResult.size() > 0) {
								AlertMessagesBuilder builder = new AlertMessagesBuilder();
								for (String message : evaluationResult) {
									builder.withMessage(AlertMessageType.WARNING, message);
								}
								pushMessages(builder.getAlertMessages());
							} else {
								clearMessages();
							}
						}
					}

					private void updatePaymentModality(PaymentModality paymentModality) {
						switch(paymentModality.getPaymentType()) {
						case INCOMING:
							rbIncoming.setSelected(true);
							rbOutgoing.setSelected(false);
							break;
						case OUTGOING:
							rbIncoming.setSelected(false);
							rbOutgoing.setSelected(true);
							break;
						}
						tfPaymentPeriod.setText(paymentModality.getPaymentPeriod().getTranslation());
					}

					@SuppressWarnings("static-access")
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
	
	private void clearMessages() {
		pushMessages(new AlertMessagesBuilder().getAlertMessages());
	}
	
	private void pushMessages(List<AlertMessage> messages) {
	    DefaultTableModel tablemodel = new DefaultTableModel();
	    tablemodel.addColumn("Typ");
	    tablemodel.addColumn("Text");
	    for (AlertMessage message : messages) {
	        tablemodel.addRow(new String[] {message.getAlertMessageType().toString(), message.getText()});
	    }
	    messagesTable.setModel(tablemodel);
	    messagesTable.getColumnModel().getColumn(0).setPreferredWidth(25);
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
		rbIncoming = new JRadioButton();
		rbOutgoing = new JRadioButton();
		label1 = new JLabel();
		cbAllCategories = new JComboBox();
		tfPaymentPeriod = new JTextField();
		scrollPane4 = new JScrollPane();
		categoryEntriesTable = new JTable();
		lblSum = new JLabel();
		tfSum = new JTextField();
		lblBudget = new JLabel();
		tfBudget = new JTextField();
		scrollPane2 = new JScrollPane();
		messagesTable = new JTable();
		label2 = new JLabel();
		tfMonthOverall = new JTextField();

		//======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {77, 236, 43, 0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 92, 0, 0, 0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4};

		//======== tbMain ========
		{
			tbMain.setEnabled(false);

			//---- btnCheckSaldo ----
			btnCheckSaldo.setText("Check Saldo");
			tbMain.add(btnCheckSaldo);
		}
		contentPane.add(tbMain, new GridBagConstraints(0, 0, 6, 1, 0.0, 0.0,
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
			new Insets(0, 0, 5, 5), 0, 0));

		//---- rbIncoming ----
		rbIncoming.setText("eingehend");
		rbIncoming.setEnabled(false);
		contentPane.add(rbIncoming, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- rbOutgoing ----
		rbOutgoing.setText("ausgehend");
		rbOutgoing.setEnabled(false);
		contentPane.add(rbOutgoing, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label1 ----
		label1.setText("Per Kategorie:");
		contentPane.add(label1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		contentPane.add(cbAllCategories, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- tfPaymentPeriod ----
		tfPaymentPeriod.setEditable(false);
		contentPane.add(tfPaymentPeriod, new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane4 ========
		{
			scrollPane4.setViewportView(categoryEntriesTable);
		}
		contentPane.add(scrollPane4, new GridBagConstraints(0, 3, 6, 1, 0.0, 0.0,
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
		contentPane.add(tfBudget, new GridBagConstraints(3, 4, 3, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(messagesTable);
		}
		contentPane.add(scrollPane2, new GridBagConstraints(0, 5, 6, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label2 ----
		label2.setText("Monatsabschluss:");
		contentPane.add(label2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

		//---- tfMonthOverall ----
		tfMonthOverall.setEditable(false);
		contentPane.add(tfMonthOverall, new GridBagConstraints(1, 6, 5, 1, 0.0, 0.0,
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
	private JRadioButton rbIncoming;
	private JRadioButton rbOutgoing;
	private JLabel label1;
	private JComboBox cbAllCategories;
	private JTextField tfPaymentPeriod;
	private JScrollPane scrollPane4;
	private JTable categoryEntriesTable;
	private JLabel lblSum;
	private JTextField tfSum;
	private JLabel lblBudget;
	private JTextField tfBudget;
	private JScrollPane scrollPane2;
	private JTable messagesTable;
	private JLabel label2;
	private JTextField tfMonthOverall;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}