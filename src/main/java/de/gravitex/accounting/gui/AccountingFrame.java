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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import de.gravitex.accounting.AccountingData;
import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.BudgetEvaluation;
import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.enumeration.SubAccountReferenceCheck;
import de.gravitex.accounting.exception.GenericAccountingException;
import de.gravitex.accounting.filter.interfacing.FilteredComponentListener;
import de.gravitex.accounting.gui.component.FilterComboBox;
import de.gravitex.accounting.gui.component.FromToDateFilter;
import de.gravitex.accounting.gui.component.table.FilterTable;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.model.AccountingResultCategoryModel;
import de.gravitex.accounting.model.AccountingResultModelRow;
import de.gravitex.accounting.model.AccountingResultMonthModel;
import de.gravitex.accounting.util.MonthKey;
import de.gravitex.accounting.validation.SubAccountValidation;
import de.gravitex.accounting.wrapper.Category;
import lombok.Data;

@Data
public class AccountingFrame extends JFrame implements FilteredComponentListener {
	
	private static final Logger logger = Logger.getLogger(AccountingFrame.class);

	private static final long serialVersionUID = -8241085588080811229L;
	
	private static final int TAB_INDEX_DATA = 0;
	private static final int TAB_INDEX_BUDGET = 1;
	private static final int TAB_INDEX_FILTER = 2;
	private static final int TAB_INDEX_SETTINGS = 3;
	private static final int TAB_INDEX_OUTPUT = 4;

	private AccountingSingleton singleton;

	protected AccountingResultMonthModel monthModel;
	
	public AccountingFrame() {
		
		initComponents();
		
		try {
			singleton = AccountingSingleton.getInstance();
			
			accountingMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			categoriesByMonthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			budgetPlanningList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			btnCheckSaldo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						singleton.saldoCheck();
						pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.OK, "Saldo OK!!")
								.getAlertMessages());
					} catch (GenericAccountingException accountingException) {
						pushMessages(new AlertMessagesBuilder()
								.withMessage(AlertMessageType.ERROR, "Saldo error: " + accountingException.getMessage())
								.getAlertMessages());
					}
				}
			});
			btnCheckValidities.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					tbpMain.setSelectedIndex(TAB_INDEX_OUTPUT);
					StringBuffer buffer = new StringBuffer();
					AccountingManager accountingManager = AccountingSingleton.getInstance().getAccountingManager();
					for (Category category : accountingManager
							.getPeriodicalPaymentCategories()) {
						buffer.append("------------ "+category.getCategory()+" ------------\n");
						try {
							accountingManager.checkValidities(category.getCategory());	
							buffer.append("OK\n");
						} catch (GenericAccountingException e2) {
							buffer.append(e2.asStringBuffer());
						}
					}
					taOutput.setText(buffer.toString());
				}
			});
			btnPrepareBudgets.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					HashMap<MonthKey, Properties> extendedBudgets = AccountingSingleton.getInstance().getAccountingManager()
							.prepareBudgets(LocalDate.now(), false);
					tbpMain.setSelectedIndex(TAB_INDEX_OUTPUT);
					StringBuffer buffer = new StringBuffer();
					List<MonthKey> keyList = new ArrayList<MonthKey>(extendedBudgets.keySet());
					Collections.sort(keyList);
					for (MonthKey monthKey : keyList) {
						buffer.append(monthKey + "\n");
						buffer.append("------------------------------------------------------\n");
						Properties properties = extendedBudgets.get(monthKey);
						for (Entry<Object, Object> entry : properties.entrySet()) {
							buffer.append(entry.getKey() + "=" + entry.getValue() + "\n");
						}
						buffer.append("\n");
					}
					taOutput.setText(buffer.toString());
					pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.OK, "Budgets vorbereitet!!")
							.getAlertMessages());
				}
			});
			btnReloadData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// AccountingSingleton.getInstance().initialize();
				}
			});
			btnClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			initFilters();
			fillAccountingMonths();
			cbFilterAllPartners.initData();
			cbFilterAllCategories.initData();
			fillBudgetPlannings();
			initSettings();
			filterTable.loadData();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		} catch (Exception e) {
			e.printStackTrace();
			pushMessages(
					new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, "Fehler beim Starten des Managers!!")
							.withMessage(AlertMessageType.ERROR, e.getMessage()).getAlertMessages());
		}
	}
	
	private void initFilters() {
		
		AccountingManager accountingManager = AccountingSingleton.getInstance().getAccountingManager();
		
		// cbFilterAlarm.setMvcData(accountingManager, filterTable, AccountingManager.ATTR_ALARM);
		cbFilterAllCategories.setMvcData(accountingManager, filterTable, AccountingManager.ATTR_MAIN_CATEGORY);
		cbFilterAllPartners.setMvcData(accountingManager, filterTable, AccountingManager.ATTR_MAIN_PARTNER);
		fromToDateFilter.setMvcData(accountingManager, filterTable, AccountingManager.ATTR_MAIN_DATE);
		
		filterTable.acceptFilteredComponentListener(this);
	}

	private void initSettings() {
		
		chkRealValuesInBudgets.setSelected(
				AccountingSingleton.getInstance().getAccountingManager().getAccountManagerSettings().isShowActualValuesInBidgetPlanning());
		chkRealValuesInBudgets.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AccountingSingleton.getInstance().getAccountingManager().getAccountManagerSettings()
						.setShowActualValuesInBidgetPlanning(chkRealValuesInBudgets.isSelected());
			}
		});
	}

	private void handleSumType(String category, MonthKey monthKey) {
		String text = "";
		if (monthKey != null) {
			text = "Einträge ("+category+") ["+monthKey+"]";	
		} else {
			text = "Einträge ("+category+") monatsübergreifend";	
		}
		categoryEntriesParent.setBorder(BorderFactory.createTitledBorder(text));
	}
	
	private void fillBudgetPlannings() {
		final DefaultListModel<MonthKey> budgetPlanningModel = new DefaultListModel<MonthKey>();
		Set<MonthKey> keySet = AccountingSingleton.getInstance().getAccountingManager().getBudgetPlannings().keySet();
		ArrayList<MonthKey> keyList = new ArrayList<MonthKey>(keySet);
		Collections.sort(keyList);
		for (MonthKey budgetKey : keyList) {
			budgetPlanningModel.addElement(budgetKey);
		}
		budgetPlanningList.setModel(budgetPlanningModel);
		budgetPlanningList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object[] selectedValues = budgetPlanningList.getSelectedValues();
				List<MonthKey> selectedValueList = new ArrayList<MonthKey>();
				for (Object value : selectedValues) {
					selectedValueList.add((MonthKey) value);
				}
				AccountingGuiHelper.displayBudgetChart(AccountingFrame.this, selectedValueList, AccountingSingleton.getInstance().getAccountingManager());
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void fillAccountingMonths() {
		
		final DefaultListModel<MonthKey> monthKeyModel = new DefaultListModel<MonthKey>();
		Set<MonthKey> keySet = AccountingSingleton.getInstance().getAccountingManager().getMainAccount().keySet();
		List<MonthKey> keyList = new ArrayList<MonthKey>(keySet);
		Collections.sort(keyList);
		for (MonthKey monthKey : keyList) {
			monthKeyModel.addElement(monthKey);
		}
		accountingMonthList.setModel(monthKeyModel);
		accountingMonthList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				logger.info(accountingMonthList.getSelectedValue());
				MonthKey monthKey = (MonthKey) accountingMonthList.getSelectedValue();
				monthModel = singleton.getAccountingManager().getAccountingResultMonthModel(monthKey);
				clearMessages();
				fillCategoriesForMonth(monthModel);
				BigDecimal overallSum = monthModel.calculateOverallSum();
				tfMonthOverall.setText(overallSum.toString());
				if (overallSum.intValue() > 0) {
					tfMonthOverall.setBackground(Color.WHITE);
				} else {
					tfMonthOverall.setBackground(Color.RED);
				}
			}
		});
	}
	
	private void fillCategoriesForMonth(AccountingResultMonthModel accountingResultMonthModel) {
		try {
			final DefaultListModel<Category> categoriesByMonthModel = new DefaultListModel<Category>();
			for (String category : accountingResultMonthModel.getDistinctCategories()) {
				categoriesByMonthModel.addElement(Category.fromValues(category,
						AccountingSingleton.getInstance().getAccountingManager().initPaymentModality(accountingResultMonthModel.getMonthKey(), category)));
			}
			categoriesByMonthList.setModel(categoriesByMonthModel);
			categoriesByMonthList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					Category categoryWrapper = (Category) categoriesByMonthList.getSelectedValue();
					if (categoryWrapper == null) {
						return;
					}
					AccountingResultCategoryModel categoryModel = monthModel
							.getCategoryModel(categoryWrapper.getCategory());
					fillCategoryEntries(categoryModel);
					updatePaymentModality(categoryWrapper.getPaymentModality());
					if (singleton.getAccountingManager().getAccountManagerSettings().isBudgetProjectionsEnabled()) {
						List<BudgetEvaluation> evaluationResult = AccountingSingleton.getInstance()
								.getAccountingManager().evaluateBudgetProjection(categoryWrapper, LocalDate.now());
						if (evaluationResult.size() > 0) {
							AlertMessagesBuilder builder = new AlertMessagesBuilder();
							for (BudgetEvaluation budgetEvaluation : evaluationResult) {
								builder.withMessage(AlertMessageType.WARNING, budgetEvaluation.generateMessage());
							}
							pushMessages(builder.getAlertMessages());
						} else {
							clearMessages();
						}
					}
				}
			});			
		} catch (GenericAccountingException e) {
			pushMessages(
					new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, e.getMessage()).getAlertMessages());
		}
	}
	
	private void updatePaymentModality(PaymentModality paymentModality) {
		tfPaymentType.setText(paymentModality.getPaymentType().getTranslation());
		tfPaymentPeriod.setText(paymentModality.getPaymentPeriod().getTranslation());
	}

	public void clearMessages() {
		pushMessages(new AlertMessagesBuilder().getAlertMessages());
	}
	
	@SuppressWarnings("static-access")
	private void fillCategoryEntries(AccountingResultCategoryModel categoryModel) {

		if (categoryModel == null) {
			return;
		}
		
		/*
		boolean categoryPeriodically = AccountingSingleton.getInstance().getAccountingManager().isCategoryPeriodically(categoryModel.getCategory());
		btnCheckValidities.setEnabled(categoryPeriodically);
		*/

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
		btnReloadData = new JButton();
		btnClose = new JButton();
		btnCheckValidities = new JButton();
		tbpMain = new JTabbedPane();
		pnlData = new JPanel();
		scrollPane1 = new JScrollPane();
		panel4 = new JPanel();
		accountingMonthList = new JList();
		scrollPane3 = new JScrollPane();
		panel5 = new JPanel();
		categoriesByMonthList = new JList();
		label3 = new JLabel();
		tfPaymentType = new JTextField();
		label8 = new JLabel();
		tfPaymentPeriod = new JTextField();
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
		btnPrepareBudgets = new JButton();
		checkBox2 = new JCheckBox();
		panel3 = new JPanel();
		pnlChart = new JPanel();
		lblBudgetState = new JLabel();
		percentageBar = new JProgressBar();
		pnlFilter = new JPanel();
		label1 = new JLabel();
		cbFilterAllCategories = new FilterComboBox();
		filterTable = new FilterTable();
		label4 = new JLabel();
		cbFilterAllPartners = new FilterComboBox();
		label5 = new JLabel();
		fromToDateFilter = new FromToDateFilter();
		label6 = new JLabel();
		tfFilterSum = new JTextField();
		pnlSettings = new JPanel();
		chkRealValuesInBudgets = new JCheckBox();
		pnlOutput = new JPanel();
		scrollPane6 = new JScrollPane();
		taOutput = new JTextArea();
		panelAlerts = new JPanel();
		scrollPane2 = new JScrollPane();
		messagesTable = new JTable();

		//======== this ========
		setTitle("Accounting Manager");
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 1076, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 169, 183, 129, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0, 0.0, 1.0E-4};

		//======== tbMain ========
		{
			tbMain.setEnabled(false);

			//---- btnCheckSaldo ----
			btnCheckSaldo.setText("Saldo pr\u00fcfen");
			tbMain.add(btnCheckSaldo);

			//---- btnReloadData ----
			btnReloadData.setText("Neu laden");
			tbMain.add(btnReloadData);

			//---- btnClose ----
			btnClose.setText("Beenden");
			tbMain.add(btnClose);

			//---- btnCheckValidities ----
			btnCheckValidities.setText("Zeitr\u00e4ume pr\u00fcfen");
			tbMain.add(btnCheckValidities);
		}
		contentPane.add(tbMain, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== tbpMain ========
		{

			//======== pnlData ========
			{
				pnlData.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.
				swing.border.EmptyBorder(0,0,0,0), "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn",javax.swing.border
				.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java.awt.Font("Dia\u006cog"
				,java.awt.Font.BOLD,12),java.awt.Color.red),pnlData. getBorder
				()));pnlData. addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override public void propertyChange(java
				.beans.PropertyChangeEvent e){if("\u0062ord\u0065r".equals(e.getPropertyName()))throw new RuntimeException
				();}});
				pnlData.setLayout(new GridBagLayout());
				((GridBagLayout)pnlData.getLayout()).columnWidths = new int[] {0, 254, 651, 114, 0};
				((GridBagLayout)pnlData.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 106, 0, 0, 0, 0};
				((GridBagLayout)pnlData.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
				((GridBagLayout)pnlData.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};

				//======== scrollPane1 ========
				{

					//======== panel4 ========
					{
						panel4.setBorder(new TitledBorder("Buchungen"));
						panel4.setLayout(new BorderLayout());
						panel4.add(accountingMonthList, BorderLayout.CENTER);
					}
					scrollPane1.setViewportView(panel4);
				}
				pnlData.add(scrollPane1, new GridBagConstraints(0, 0, 2, 4, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//======== scrollPane3 ========
				{

					//======== panel5 ========
					{
						panel5.setBorder(new TitledBorder("Buchungskategorien"));
						panel5.setLayout(new BorderLayout());
						panel5.add(categoriesByMonthList, BorderLayout.CENTER);
					}
					scrollPane3.setViewportView(panel5);
				}
				pnlData.add(scrollPane3, new GridBagConstraints(2, 0, 1, 4, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- label3 ----
				label3.setText("Typ:");
				pnlData.add(label3, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- tfPaymentType ----
				tfPaymentType.setEditable(false);
				pnlData.add(tfPaymentType, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label8 ----
				label8.setText("Periode:");
				pnlData.add(label8, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- tfPaymentPeriod ----
				tfPaymentPeriod.setEditable(false);
				pnlData.add(tfPaymentPeriod, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
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
				pnlData.add(categoryEntriesParent, new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- lblSum ----
				lblSum.setText("Summe:");
				pnlData.add(lblSum, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- tfSum ----
				tfSum.setEditable(false);
				pnlData.add(tfSum, new GridBagConstraints(1, 5, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- lblBudget ----
				lblBudget.setText("Budget:");
				pnlData.add(lblBudget, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- tfBudget ----
				tfBudget.setEditable(false);
				pnlData.add(tfBudget, new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label2 ----
				label2.setText("Monatsabschluss:");
				pnlData.add(label2, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- tfMonthOverall ----
				tfMonthOverall.setEditable(false);
				pnlData.add(tfMonthOverall, new GridBagConstraints(1, 7, 3, 1, 0.0, 0.0,
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
					((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {154, 62, 0};
					((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
					((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

					//======== scrollPane5 ========
					{
						scrollPane5.setViewportView(budgetPlanningList);
					}
					panel1.add(scrollPane5, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- btnPrepareBudgets ----
					btnPrepareBudgets.setText("Prepare Budgets");
					panel1.add(btnPrepareBudgets, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- checkBox2 ----
					checkBox2.setText("komplett");
					panel1.add(checkBox2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
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
					((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0, 0};
					((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
					((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

					//======== pnlChart ========
					{
						pnlChart.setLayout(new BorderLayout());
					}
					panel3.add(pnlChart, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));

					//---- lblBudgetState ----
					lblBudgetState.setText("123");
					panel3.add(lblBudgetState, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
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

			//======== pnlFilter ========
			{
				pnlFilter.setLayout(new GridBagLayout());
				((GridBagLayout)pnlFilter.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
				((GridBagLayout)pnlFilter.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
				((GridBagLayout)pnlFilter.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout)pnlFilter.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

				//---- label1 ----
				label1.setText("Kategorie:");
				pnlFilter.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				pnlFilter.add(cbFilterAllCategories, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				pnlFilter.add(filterTable, new GridBagConstraints(2, 0, 1, 4, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

				//---- label4 ----
				label4.setText("Partner:");
				pnlFilter.add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				pnlFilter.add(cbFilterAllPartners, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- label5 ----
				label5.setText("Zeitraum:");
				pnlFilter.add(label5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
				pnlFilter.add(fromToDateFilter, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

				//---- label6 ----
				label6.setText("Summe:");
				pnlFilter.add(label6, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- tfFilterSum ----
				tfFilterSum.setEditable(false);
				pnlFilter.add(tfFilterSum, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			tbpMain.addTab("Filter", pnlFilter);

			//======== pnlSettings ========
			{
				pnlSettings.setLayout(new GridBagLayout());
				((GridBagLayout)pnlSettings.getLayout()).columnWidths = new int[] {74, 0};
				((GridBagLayout)pnlSettings.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
				((GridBagLayout)pnlSettings.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
				((GridBagLayout)pnlSettings.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

				//---- chkRealValuesInBudgets ----
				chkRealValuesInBudgets.setText("Realwerte in Budgetplanung");
				pnlSettings.add(chkRealValuesInBudgets, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
			}
			tbpMain.addTab("Einstellungen", pnlSettings);

			//======== pnlOutput ========
			{
				pnlOutput.setLayout(new BorderLayout());

				//======== scrollPane6 ========
				{
					scrollPane6.setViewportView(taOutput);
				}
				pnlOutput.add(scrollPane6, BorderLayout.CENTER);
			}
			tbpMain.addTab("Ausgabe", pnlOutput);
		}
		contentPane.add(tbpMain, new GridBagConstraints(0, 1, 2, 3, 0.0, 0.0,
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
		contentPane.add(panelAlerts, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
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
	private JButton btnReloadData;
	private JButton btnClose;
	private JButton btnCheckValidities;
	private JTabbedPane tbpMain;
	private JPanel pnlData;
	private JScrollPane scrollPane1;
	private JPanel panel4;
	private JList accountingMonthList;
	private JScrollPane scrollPane3;
	private JPanel panel5;
	private JList categoriesByMonthList;
	private JLabel label3;
	private JTextField tfPaymentType;
	private JLabel label8;
	private JTextField tfPaymentPeriod;
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
	private JButton btnPrepareBudgets;
	private JCheckBox checkBox2;
	private JPanel panel3;
	private JPanel pnlChart;
	private JLabel lblBudgetState;
	private JProgressBar percentageBar;
	private JPanel pnlFilter;
	private JLabel label1;
	private FilterComboBox cbFilterAllCategories;
	private FilterTable filterTable;
	private JLabel label4;
	private FilterComboBox cbFilterAllPartners;
	private JLabel label5;
	private FromToDateFilter fromToDateFilter;
	private JLabel label6;
	private JTextField tfFilterSum;
	private JPanel pnlSettings;
	private JCheckBox chkRealValuesInBudgets;
	private JPanel pnlOutput;
	private JScrollPane scrollPane6;
	private JTextArea taOutput;
	private JPanel panelAlerts;
	private JScrollPane scrollPane2;
	private JTable messagesTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	@Override
	public void filterDataChanged() {
		logger.info("filter data changed...");
		BigDecimal sum = new BigDecimal(0);
		for (AccountingRow accountingRow : AccountingSingleton.getInstance().getAccountingManager().getFilteredEntries()) {
			sum = sum.add(accountingRow.getAmount());
		}
		tfFilterSum.setText(sum.toString());
	}

	public void updateBudgetState(String text) {
		lblBudgetState.setText(text);
	}

	@Override
	public void itemSelected(Object selectedItem) {
		AccountingManager accountingManager = AccountingSingleton.getInstance().getAccountingManager();
		AccountingRow accountingRow = (AccountingRow) selectedItem;
		List<AccountingRow> subEntries = accountingManager
				.getSubEntries(accountingRow);
		logger.info(subEntries.size() + " sub entries loaded.");
		SubAccountValidation checkSubEntries = accountingManager.checkSubEntries(accountingRow);
		if (!checkSubEntries.getSubAccountReferenceCheck().equals(SubAccountReferenceCheck.NONE)) {
			pushMessages(new AlertMessagesBuilder()
					.withMessage(AlertMessageType.WARNING, checkSubEntries.toString())
					.getAlertMessages());			
		}
	}

	@Override
	public Color getRowColor(Object object) {
		switch (AccountingSingleton.getInstance().getAccountingManager().checkSubEntries((AccountingRow) object)
				.getSubAccountReferenceCheck()) {
		case VALID:
			return Color.GREEN;
		case INVALID:
			return Color.RED;
		default:
			return Color.WHITE;
		}
	}
}