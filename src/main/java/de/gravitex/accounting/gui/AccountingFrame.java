package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.AccountingUtil;
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
		budgetPlanningList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		btnCheckSaldo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					manager.saldoCheck();
					pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.OK, "Saldo OK!!")
							.getAlertMessages());
				} catch (AccountingException accountingException) {
					pushMessages(new AlertMessagesBuilder()
							.withMessage(AlertMessageType.ERROR, "Saldo error: " + accountingException.getMessage())
							.getAlertMessages());
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
		fillBudgetPlannings();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void fillBudgetPlannings() {
		final DefaultListModel<String> budgetPlanningModel = new DefaultListModel<String>();
		Set<String> keySet = manager.getBudgetPlannings().keySet();
		ArrayList<String> keyList = new ArrayList<String>(keySet);
		Collections.sort(keyList);
		for (String budgetKey : keyList) {
			budgetPlanningModel.addElement(budgetKey);
		}
		budgetPlanningList.setModel(budgetPlanningModel);
		budgetPlanningList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				displayBudgetChart(String.valueOf(budgetPlanningList.getSelectedValue()));
			}

			private void displayBudgetChart(String monthKey) {
				pnlChart.removeAll();
				PieDataset dataset = createDataset(monthKey);
				if (dataset == null) {
					pnlChart.add(new JLabel("Budget überschritten!!"), BorderLayout.CENTER);
					pnlChart.validate();
					return;
				}
				JFreeChart chart = ChartFactory.createPieChart(  
						"Budgetplanung für " + monthKey + " verfügbar: " + manager.getAvailableIncome(monthKey) + " Euro",
						dataset,  
				        false,   
				        true,  
				        false);  
				pnlChart.add(new ChartPanel(chart), BorderLayout.CENTER);
				pnlChart.validate();
			}

			private PieDataset createDataset(String monthKey) {
				BigDecimal availableIncome = manager.getAvailableIncome(monthKey);
				Properties budgetPlanningForMonth = manager.getBudgetPlannings().get(monthKey);
				int totalyPlanned = 0;
				for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
					totalyPlanned += Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget)));
				}
				if (totalyPlanned > availableIncome.intValue()) {
					return null;
				}
			    DefaultPieDataset dataset=new DefaultPieDataset();
			    // Rest
			    int freeToUse = availableIncome.intValue() - totalyPlanned;
				dataset.setValue("Frei ("+freeToUse+")", AccountingUtil.getPercentage(freeToUse, availableIncome.intValue()));
			    // Kategorien
				int budgetForCategory = 0;
			    for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
					budgetForCategory = Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget)));
					dataset.setValue(String.valueOf(categoryBudget) + " (" + budgetForCategory + ")",
							AccountingUtil.getPercentage(budgetForCategory, availableIncome.intValue()));
				}
			    return dataset;  
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
					categoriesByMonthModel.addElement(CategoryWrapper.fromValues(category,
							manager.initPaymentModality(accountingResultMonthModel.getMonthKey(), category)));
				}
				categoriesByMonthList.setModel(categoriesByMonthModel);
				categoriesByMonthList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						CategoryWrapper categoryWrapper = (CategoryWrapper) categoriesByMonthList.getSelectedValue();
						if (categoryWrapper == null) {
							return;
						}
						AccountingResultCategoryModel categoryModel = monthModel
								.getCategoryModel(categoryWrapper.getCategory());
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
						switch (paymentModality.getPaymentType()) {
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
						tfBudget.setText(
								categoryModel.getBudget() != null ? categoryModel.getBudget().toString() : "---");

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
			tablemodel.addRow(new String[] { message.getAlertMessageType().toString(), message.getText() });
		}
		messagesTable.setModel(tablemodel);
		messagesTable.getColumnModel().getColumn(0).setPreferredWidth(25);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Stefan Schulz
		tbMain = new JToolBar();
		btnCheckSaldo = new JButton();
		scrollPane1 = new JScrollPane();
		accountingMonthList = new JList();
		scrollPane3 = new JScrollPane();
		categoriesByMonthList = new JList();
		rbIncoming = new JRadioButton();
		rbOutgoing = new JRadioButton();
		tfPaymentPeriod = new JTextField();
		tabbedPane1 = new JTabbedPane();
		pnlData = new JPanel();
		label1 = new JLabel();
		cbAllCategories = new JComboBox();
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
		pnlChartHolder = new JPanel();
		scrollPane5 = new JScrollPane();
		budgetPlanningList = new JList();
		pnlChart = new JPanel();

		// ======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] { 173, 43, 444, 102, 0 };
		((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 92, 0, 169, 0, 183, 0, 0 };
		((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0E-4 };
		((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				1.0, 0.0, 1.0E-4 };

		// ======== tbMain ========
		{
			tbMain.setEnabled(false);

			// ---- btnCheckSaldo ----
			btnCheckSaldo.setText("Check Saldo");
			tbMain.add(btnCheckSaldo);
		}
		contentPane.add(tbMain, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

		// ======== scrollPane1 ========
		{
			scrollPane1.setViewportView(accountingMonthList);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

		// ======== scrollPane3 ========
		{
			scrollPane3.setViewportView(categoriesByMonthList);
		}
		contentPane.add(scrollPane3, new GridBagConstraints(1, 1, 2, 4, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

		// ---- rbIncoming ----
		rbIncoming.setText("eingehend");
		rbIncoming.setEnabled(false);
		contentPane.add(rbIncoming, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

		// ---- rbOutgoing ----
		rbOutgoing.setText("ausgehend");
		rbOutgoing.setEnabled(false);
		contentPane.add(rbOutgoing, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

		// ---- tfPaymentPeriod ----
		tfPaymentPeriod.setEditable(false);
		contentPane.add(tfPaymentPeriod, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

		// ======== tabbedPane1 ========
		{

			// ======== pnlData ========
			{
				pnlData.setBorder(new javax.swing.border.CompoundBorder(
						new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
								"JF\u006frmDes\u0069gner \u0045valua\u0074ion", javax.swing.border.TitledBorder.CENTER,
								javax.swing.border.TitledBorder.BOTTOM,
								new java.awt.Font("D\u0069alog", java.awt.Font.BOLD, 12), java.awt.Color.red),
						pnlData.getBorder()));
				pnlData.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
					@Override
					public void propertyChange(java.beans.PropertyChangeEvent e) {
						if ("\u0062order".equals(e.getPropertyName()))
							throw new RuntimeException();
					}
				});
				pnlData.setLayout(new GridBagLayout());
				((GridBagLayout) pnlData.getLayout()).columnWidths = new int[] { 0, 254, 0, 312, 0 };
				((GridBagLayout) pnlData.getLayout()).rowHeights = new int[] { 0, 107, 0, 131, 0, 0 };
				((GridBagLayout) pnlData.getLayout()).columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 1.0E-4 };
				((GridBagLayout) pnlData.getLayout()).rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4 };

				// ---- label1 ----
				label1.setText("Per Kategorie:");
				pnlData.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
				pnlData.add(cbAllCategories, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== scrollPane4 ========
				{
					scrollPane4.setViewportView(categoryEntriesTable);
				}
				pnlData.add(scrollPane4, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ---- lblSum ----
				lblSum.setText("Summe:");
				pnlData.add(lblSum, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

				// ---- tfSum ----
				tfSum.setEditable(false);
				pnlData.add(tfSum, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

				// ---- lblBudget ----
				lblBudget.setText("Budget:");
				pnlData.add(lblBudget, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

				// ---- tfBudget ----
				tfBudget.setEditable(false);
				pnlData.add(tfBudget, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== scrollPane2 ========
				{
					scrollPane2.setViewportView(messagesTable);
				}
				pnlData.add(scrollPane2, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ---- label2 ----
				label2.setText("Monatsabschluss:");
				pnlData.add(label2, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

				// ---- tfMonthOverall ----
				tfMonthOverall.setEditable(false);
				pnlData.add(tfMonthOverall, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			tabbedPane1.addTab("Daten", pnlData);

			// ======== pnlChartHolder ========
			{
				pnlChartHolder.setLayout(new GridBagLayout());
				((GridBagLayout) pnlChartHolder.getLayout()).columnWidths = new int[] { 216, 0, 0 };
				((GridBagLayout) pnlChartHolder.getLayout()).rowHeights = new int[] { 0, 0 };
				((GridBagLayout) pnlChartHolder.getLayout()).columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
				((GridBagLayout) pnlChartHolder.getLayout()).rowWeights = new double[] { 1.0, 1.0E-4 };

				// ======== scrollPane5 ========
				{
					scrollPane5.setViewportView(budgetPlanningList);
				}
				pnlChartHolder.add(scrollPane5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

				// ======== pnlChart ========
				{
					pnlChart.setLayout(new BorderLayout());
				}
				pnlChartHolder.add(pnlChart, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			tabbedPane1.addTab("Budget", pnlChartHolder);
		}
		contentPane.add(tabbedPane1, new GridBagConstraints(0, 5, 4, 5, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	public static void main(String[] args) {
		AccountingFrame accountingFrame = new AccountingFrame();
		accountingFrame.setSize(new Dimension(800, 600));
		accountingFrame.setVisible(true);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Stefan Schulz
	private JToolBar tbMain;
	private JButton btnCheckSaldo;
	private JScrollPane scrollPane1;
	private JList accountingMonthList;
	private JScrollPane scrollPane3;
	private JList categoriesByMonthList;
	private JRadioButton rbIncoming;
	private JRadioButton rbOutgoing;
	private JTextField tfPaymentPeriod;
	private JTabbedPane tabbedPane1;
	private JPanel pnlData;
	private JLabel label1;
	private JComboBox cbAllCategories;
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
	private JPanel pnlChartHolder;
	private JScrollPane scrollPane5;
	private JList budgetPlanningList;
	private JPanel pnlChart;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}