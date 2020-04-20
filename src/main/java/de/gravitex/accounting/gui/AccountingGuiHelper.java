package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingSingleton;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.util.MonthKey;

public class AccountingGuiHelper {
	
	private static final Logger logger = Logger.getLogger(AccountingGuiHelper.class);

	public static void displayBudgetChart(AccountingFrame accountingFrame, List<MonthKey> monthKeys, AccountingManager manager) {

		logger.info("displayBudgetChart: " + monthKeys);

		String title = "";
		if (monthKeys.size() == 1) {
			displayBudgetPercentage(accountingFrame, monthKeys.get(0), manager);
			title = "Budgetplanung (verfügbar: " + manager.getAvailableIncome(monthKeys.get(0))
					+ " Euro)";
		} else {
			title = "Budgetplanung";
			resetPercentages(accountingFrame);
		}

		accountingFrame.getPnlChart().removeAll();
		
		JFreeChart chart = ChartFactory.createBarChart(title, "Kategorie", "Aufwand", createDataset(monthKeys, manager),
				PlotOrientation.HORIZONTAL, true, true, false);
		
		chart.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		accountingFrame.getPnlChart().add(new ChartPanel(chart), BorderLayout.CENTER);
		accountingFrame.getPnlChart().validate();
		accountingFrame.clearMessages();
	}

	private static void resetPercentages(AccountingFrame accountingFrame) {
		accountingFrame.getPercentageBar().setValue(0);
	}

	private static void displayBudgetPercentage(AccountingFrame accountingFrame, MonthKey monthKey, AccountingManager manager) {

		BigDecimal availableIncome = manager.getAvailableIncome(monthKey);
		Properties budgetPlanningForMonth = manager.getBudgetPlannings().get(monthKey).getProperties();
		int totalyPlanned = 0;
		for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
			totalyPlanned += Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget)));
		}
		
		if (totalyPlanned > availableIncome.intValue()) {
			/*
			JOptionPane.showMessageDialog(accountingFrame,
					"Budget überplant (" + availableIncome.intValue() + " verfügbar, " + totalyPlanned + " verplant)",
					"Achtung", JOptionPane.INFORMATION_MESSAGE);
					*/
			accountingFrame.updateBudgetState("Budget überplant (" + availableIncome.intValue() + " verfügbar, " + totalyPlanned + " verplant)");
		} else {
			/*
			JOptionPane.showMessageDialog(accountingFrame,
					"Noch "+(availableIncome.intValue()-totalyPlanned)+" Euro verfügbar!!",
					"Info", JOptionPane.INFORMATION_MESSAGE);
					*/
			accountingFrame.updateBudgetState("Noch "+(availableIncome.intValue()-totalyPlanned)+" Euro verfügbar!!");
		}
		
		int percentage = (int) AccountingUtil.getPercentage(totalyPlanned, availableIncome.doubleValue());
		
		JProgressBar percentageBar = accountingFrame.getPercentageBar();
		
		// TODO alerting does not work
		if (percentage <= 100) {
			accountingFrame.pushMessages(
					new AlertMessagesBuilder().withMessage(AlertMessageType.OK, "Budgetplanung: "+percentage+"%").getAlertMessages());
			percentageBar.setForeground(Color.GREEN);
		} else {
			accountingFrame.pushMessages(
					new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, "Budgetplanung (überschritten): "+percentage+"%").getAlertMessages());
			percentageBar.setForeground(Color.RED);
		}
		
		percentageBar.setValue(percentage);
		percentageBar.setStringPainted(true);
	}

	private static CategoryDataset createDataset(List<MonthKey> monthKeys, AccountingManager manager) {
		
		HashMap<String, BigDecimal> categorySums = null;
		if (monthKeys.size() == 1) {
			categorySums = AccountingSingleton.getInstance().getCategorySums(monthKeys.get(0));			
		}

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (MonthKey monthKey : monthKeys) {
			addMonthData(monthKey, dataset, categorySums, manager);
		}
		return dataset;
	}

	private static void addMonthData(MonthKey monthKey, DefaultCategoryDataset dataset, HashMap<String, BigDecimal> categorySums, AccountingManager mananger) {
		
		Properties budgetPlanningForMonth = mananger.getBudgetPlannings().get(monthKey).getProperties();

		int categoryBudget = 0;
		for (Object categoryBudgetKey : budgetPlanningForMonth.keySet()) {
			categoryBudget = Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudgetKey)));
			dataset.addValue(categoryBudget, monthKey.toString(), (String) categoryBudgetKey);
			if (categorySums != null) {
				BigDecimal categorySum = categorySums.get((String) categoryBudgetKey);
				if (categorySum != null) {
					if (AccountingSingleton.getInstance().getAccountingManager().getAccountManagerSettings().isShowActualValuesInBidgetPlanning()) {
						dataset.addValue(Math.abs(categorySum.intValue()), monthKey + " (aktuell)",
								(String) categoryBudgetKey);						
					}
				}
			}
		}
	}
}