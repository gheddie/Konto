package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JProgressBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.util.MonthKey;

public class AccountingGuiHelper {

	public static void displayBudgetChart(AccountingFrame accountingFrame, List<MonthKey> monthKeys) {

		System.out.println("displayBudgetChart: " + monthKeys);

		String title = "";
		if (monthKeys.size() == 1) {
			displayBudgetPercentage(accountingFrame, monthKeys.get(0));
			title = "Budgetplanung (verfügbar: " + AccountingManager.getInstance().getAvailableIncome(monthKeys.get(0))
					+ " Euro)";
		} else {
			title = "Budgetplanung";
			resetPercentages(accountingFrame);
		}

		accountingFrame.getPnlChart().removeAll();
		
		JFreeChart chart = ChartFactory.createBarChart(title, "Kategorie", "Aufwand", createDataset(monthKeys),
				PlotOrientation.HORIZONTAL, true, true, false);
		
		chart.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		accountingFrame.getPnlChart().add(new ChartPanel(chart), BorderLayout.CENTER);
		accountingFrame.getPnlChart().validate();
		accountingFrame.clearMessages();
	}

	private static void resetPercentages(AccountingFrame accountingFrame) {
		accountingFrame.getPercentageBar().setValue(0);
	}

	private static void displayBudgetPercentage(AccountingFrame accountingFrame, MonthKey monthKey) {

		BigDecimal availableIncome = AccountingManager.getInstance().getAvailableIncome(monthKey);
		Properties budgetPlanningForMonth = AccountingManager.getInstance().getBudgetPlannings().get(monthKey);
		int totalyPlanned = 0;
		for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
			totalyPlanned += Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget)));
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

	private static CategoryDataset createDataset(List<MonthKey> monthKeys) {
		
		HashMap<String, BigDecimal> categorySums = null;
		if (monthKeys.size() == 1) {
			categorySums = AccountingManager.getInstance().getCategorySums(monthKeys.get(0));			
		}

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (MonthKey monthKey : monthKeys) {
			addMonthData(monthKey, dataset, categorySums);
		}
		return dataset;
	}

	private static void addMonthData(MonthKey monthKey, DefaultCategoryDataset dataset, HashMap<String, BigDecimal> categorySums) {
		
		Properties budgetPlanningForMonth = AccountingManager.getInstance().getBudgetPlannings().get(monthKey);

		int categoryBudget = 0;
		for (Object categoryBudgetKey : budgetPlanningForMonth.keySet()) {
			categoryBudget = Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudgetKey)));
			dataset.addValue(categoryBudget, monthKey.toString(), (String) categoryBudgetKey);
			if (categorySums != null) {
				BigDecimal categorySum = categorySums.get((String) categoryBudgetKey);
				if (categorySum != null) {
					if (AccountingManager.getInstance().getAccountManagerSettings().isShowActualValuesInBidgetPlanning()) {
						dataset.addValue(Math.abs(categorySum.intValue()), monthKey + " (aktuell)",
								(String) categoryBudgetKey);						
					}
				}
			}
		}
	}
}