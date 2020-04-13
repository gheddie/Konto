package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.exception.AccountingException;

public class AccountingGuiHelper {

	public static void displayBudgetChart(AccountingFrame accountingFrame, List<String> monthKeys) {

		System.out.println("displayBudgetChart: " + monthKeys);

		String title = "";
		if (monthKeys.size() == 1) {
			fillPercentages(accountingFrame, monthKeys.get(0));
			title = "Budgetplanung (verf�gbar: " + AccountingManager.getInstance().getAvailableIncome(monthKeys.get(0))
					+ " Euro)";
		} else {
			title = "Budgetplanung";
			resetPercentages(accountingFrame);
		}

		accountingFrame.getPnlChart().removeAll();
		
		JFreeChart chart = ChartFactory.createBarChart(title, "Kategorie", "Aufwand", createDataset(monthKeys),
				PlotOrientation.HORIZONTAL, true, true, false);
		accountingFrame.getPnlChart().add(new ChartPanel(chart), BorderLayout.CENTER);
		accountingFrame.getPnlChart().validate();
		accountingFrame.clearMessages();
	}

	private static void resetPercentages(AccountingFrame accountingFrame) {
		accountingFrame.getPercentageBar().setValue(0);
	}

	private static void fillPercentages(AccountingFrame accountingFrame, String monthKey) {

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
					new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, "Budgetplanung (�berschritten): "+percentage+"%").getAlertMessages());
			percentageBar.setForeground(Color.RED);
		}
		
		percentageBar.setValue(percentage);
		percentageBar.setStringPainted(true);
	}

	private static CategoryDataset createDataset(List<String> monthKeys) {

		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (String monthKey : monthKeys) {
			addMonthData(monthKey, dataset);
		}
		return dataset;
	}

	private static void addMonthData(String monthKey, DefaultCategoryDataset dataset) {
		Properties budgetPlanningForMonth = AccountingManager.getInstance().getBudgetPlannings().get(monthKey);

		for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
			dataset.addValue(Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget))), monthKey,
					(Comparable) categoryBudget);
		}
	}
}