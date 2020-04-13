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
import javax.swing.*;

import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
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
import de.gravitex.accounting.enumeration.AccountingError;
import de.gravitex.accounting.exception.AccountingException;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.wrapper.CategoryWrapper;
import lombok.Data;

/**
 * @author Stefan Schulz
 */
@Data
public class AccountingFrame extends JFrame {

	private static final long serialVersionUID = -8241085588080811229L;

	private AccountingManager manager;

	protected AccountingResultMonthModel monthModel;

	public AccountingFrame() {
		initComponents();
		manager = AccountingManager.getInstance();
		accountingMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoriesByMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		budgetPlanningList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
				handleSumType(category, null);
				tfSum.setText(sum.toString());
				tfBudget.setText("---");
			}
		});
		fillBudgetPlannings();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void handleSumType(String category, String monthKey) {
		String text = "";
		if (monthKey != null) {
			text = "Einträge ("+category+") ["+monthKey+"]";	
		} else {
			text = "Einträge ("+category+") monatsübergreifend";	
		}
		categoryEntriesParent.setBorder(BorderFactory.createTitledBorder(text));
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
				Object[] selectedValues = budgetPlanningList.getSelectedValues();
				List<String> selectedValueList = new ArrayList<String>();
				for (Object value : selectedValues) {
					selectedValueList.add(String.valueOf(value));
				}
				AccountingGuiHelper.displayBudgetChart(AccountingFrame.this, selectedValueList);
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
						handleSumType(categoryModel.getCategory(), categoryModel.getMonthKey());
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

	public void clearMessages() {
		pushMessages(new AlertMessagesBuilder().getAlertMessages());
	}

	public void pushMessages(List<AlertMessage> messages) {
		
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
		tbpMain = new JTabbedPane();
		pnlData = new JPanel();
		scrollPane1 = new JScrollPane();
		accountingMonthList = new JList();
		scrollPane3 = new JScrollPane();
		categoriesByMonthList = new JList();
		rbIncoming = new JRadioButton();
		rbOutgoing = new JRadioButton();
		tfPaymentPeriod = new JTextField();
		label1 = new JLabel();
		cbAllCategories = new JComboBox();
		categoryEntriesParent = new JPanel();
		scrollPane4 = new JScrollPane();
		categoryEntriesTable = new JTable();
		lblSum = new JLabel();
		tfSum = new JTextField();
		lblBudget = new JLabel();
		tfBudget = new JTextField();
		label2 = new JLabel();
		tfMonthOverall = new JTextField();
		pnlChartHolder = new JPanel();
		panel1 = new JPanel();
		scrollPane5 = new JScrollPane();
		budgetPlanningList = new JList();
		panel3 = new JPanel();
		pnlChart = new JPanel();
		percentageBar = new JProgressBar();
		panelAlerts = new JPanel();
		scrollPane2 = new JScrollPane();
		messagesTable = new JTable();

		//======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {1076, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 169, 0, 183, 0, 129, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

		//======== tbMain ========
		{
			tbMain.setEnabled(false);

			//---- btnCheckSaldo ----
			btnCheckSaldo.setText("Check Saldo");
			tbMain.add(btnCheckSaldo);
		}
		contentPane.add(tbMain, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== tbpMain ========
		{

			//======== pnlData ========
			{
				pnlData.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing
				. border. EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder
				. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .
				awt .Font .BOLD ,12 ), java. awt. Color. red) ,pnlData. getBorder( )) )
				; pnlData. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e
				) {if ("bord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} )
				;
				pnlData.setLayout(new GridBagLayout());
				((GridBagLayout)pnlData.getLayout()).columnWidths = new int[] {0, 254, 232, 312, 0};
				((GridBagLayout)pnlData.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 106, 0, 0, 0, 0};
				((GridBagLayout)pnlData.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout)pnlData.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};

				//======== scrollPane1 ========
				{
					scrollPane1.setViewportView(accountingMonthList);
				}
				pnlData.add(scrollPane1, new GridBagConstraints(0, 0, 2, 4, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//======== scrollPane3 ========
				{
					scrollPane3.setViewportView(categoriesByMonthList);
				}
				pnlData.add(scrollPane3, new GridBagConstraints(2, 0, 1, 4, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- rbIncoming ----
				rbIncoming.setText("eingehend");
				rbIncoming.setEnabled(false);
				pnlData.add(rbIncoming, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- rbOutgoing ----
				rbOutgoing.setText("ausgehend");
				rbOutgoing.setEnabled(false);
				pnlData.add(rbOutgoing, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- tfPaymentPeriod ----
				tfPaymentPeriod.setEditable(false);
				pnlData.add(tfPaymentPeriod, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label1 ----
				label1.setText("Per Kategorie (\u00fcbergreifend):");
				pnlData.add(label1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				pnlData.add(cbAllCategories, new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//======== categoryEntriesParent ========
				{
					categoryEntriesParent.setBorder(new TitledBorder("Eintr\u00e4ge"));
					categoryEntriesParent.setLayout(new GridBagLayout());
					((GridBagLayout)categoryEntriesParent.getLayout()).columnWidths = new int[] {0, 254, 232, 312, 0};
					((GridBagLayout)categoryEntriesParent.getLayout()).rowHeights = new int[] {101, 0};
					((GridBagLayout)categoryEntriesParent.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
					((GridBagLayout)categoryEntriesParent.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

					//======== scrollPane4 ========
					{
						scrollPane4.setViewportView(categoryEntriesTable);
					}
					categoryEntriesParent.add(scrollPane4, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				pnlData.add(categoryEntriesParent, new GridBagConstraints(0, 5, 4, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- lblSum ----
				lblSum.setText("Summe:");
				pnlData.add(lblSum, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- tfSum ----
				tfSum.setEditable(false);
				pnlData.add(tfSum, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- lblBudget ----
				lblBudget.setText("Budget:");
				pnlData.add(lblBudget, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- tfBudget ----
				tfBudget.setEditable(false);
				pnlData.add(tfBudget, new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label2 ----
				label2.setText("Monatsabschluss:");
				pnlData.add(label2, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- tfMonthOverall ----
				tfMonthOverall.setEditable(false);
				pnlData.add(tfMonthOverall, new GridBagConstraints(1, 8, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			tbpMain.addTab("Daten", pnlData);

			//======== pnlChartHolder ========
			{
				pnlChartHolder.setLayout(new GridBagLayout());
				((GridBagLayout)pnlChartHolder.getLayout()).columnWidths = new int[] {216, 0, 0};
				((GridBagLayout)pnlChartHolder.getLayout()).rowHeights = new int[] {345, 30, 0};
				((GridBagLayout)pnlChartHolder.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
				((GridBagLayout)pnlChartHolder.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

				//======== panel1 ========
				{
					panel1.setBorder(new TitledBorder("Verf\u00fcgbare Planungen"));
					panel1.setLayout(new GridBagLayout());
					((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {216, 0};
					((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
					((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

					//======== scrollPane5 ========
					{
						scrollPane5.setViewportView(budgetPlanningList);
					}
					panel1.add(scrollPane5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				pnlChartHolder.add(panel1, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));

				//======== panel3 ========
				{
					panel3.setBorder(new TitledBorder("Grafische Darstellung"));
					panel3.setLayout(new GridBagLayout());
					((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0};
					((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
					((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

					//======== pnlChart ========
					{
						pnlChart.setLayout(new BorderLayout());
					}
					panel3.add(pnlChart, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				pnlChartHolder.add(panel3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
				pnlChartHolder.add(percentageBar, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			tbpMain.addTab("Budget", pnlChartHolder);
		}
		contentPane.add(tbpMain, new GridBagConstraints(0, 1, 1, 5, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== panelAlerts ========
		{
			panelAlerts.setBorder(new TitledBorder("Meldungen"));
			panelAlerts.setLayout(new GridBagLayout());
			((GridBagLayout)panelAlerts.getLayout()).columnWidths = new int[] {173, 43, 444, 102, 0};
			((GridBagLayout)panelAlerts.getLayout()).rowHeights = new int[] {129, 0};
			((GridBagLayout)panelAlerts.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0, 1.0E-4};
			((GridBagLayout)panelAlerts.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

			//======== scrollPane2 ========
			{
				scrollPane2.setViewportView(messagesTable);
			}
			panelAlerts.add(scrollPane2, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		contentPane.add(panelAlerts, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
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
	private JTabbedPane tbpMain;
	private JPanel pnlData;
	private JScrollPane scrollPane1;
	private JList accountingMonthList;
	private JScrollPane scrollPane3;
	private JList categoriesByMonthList;
	private JRadioButton rbIncoming;
	private JRadioButton rbOutgoing;
	private JTextField tfPaymentPeriod;
	private JLabel label1;
	private JComboBox cbAllCategories;
	private JPanel categoryEntriesParent;
	private JScrollPane scrollPane4;
	private JTable categoryEntriesTable;
	private JLabel lblSum;
	private JTextField tfSum;
	private JLabel lblBudget;
	private JTextField tfBudget;
	private JLabel label2;
	private JTextField tfMonthOverall;
	private JPanel pnlChartHolder;
	private JPanel panel1;
	private JScrollPane scrollPane5;
	private JList budgetPlanningList;
	private JPanel panel3;
	private JPanel pnlChart;
	private JProgressBar percentageBar;
	private JPanel panelAlerts;
	private JScrollPane scrollPane2;
	private JTable messagesTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}