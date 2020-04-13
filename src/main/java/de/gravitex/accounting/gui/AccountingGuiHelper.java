package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Properties;

import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.exception.AccountingException;

public class AccountingGuiHelper {

	public static void displayBudgetChart(AccountingFrame accountingFrame, List<String> monthKeys) {
		
		System.out.println("displayBudgetChart: " + monthKeys);
		
		accountingFrame.getPnlChart().removeAll();
		try {
		      JFreeChart chart = ChartFactory.createBarChart(
		    	         "Budgetplanung",           
		    	         "Kategorie",            
		    	         "Aufwand",            
		    	         createDataset(monthKeys),          
		    	         PlotOrientation.HORIZONTAL,           
		    	         true, true, false);
			accountingFrame.getPnlChart().add(new ChartPanel(chart), BorderLayout.CENTER);
			accountingFrame.getPnlChart().validate();		
			accountingFrame.clearMessages();
		} catch (AccountingException e) {
			accountingFrame.getPnlChart().add(new JLabel(), BorderLayout.CENTER);
			accountingFrame.getPnlChart().validate();
			accountingFrame.pushMessages(new AlertMessagesBuilder().withMessage(AlertMessageType.ERROR, e.getMessage()).getAlertMessages());
		}
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
	
	/*
	private static PieDataset createDataset(String monthKey) {
		
		BigDecimal availableIncome = AccountingManager.
				getInstance().getAvailableIncome(monthKey);
		Properties budgetPlanningForMonth = AccountingManager.
				getInstance().getBudgetPlannings().get(monthKey);
		int totalyPlanned = 0;
		for (Object categoryBudget : budgetPlanningForMonth.keySet()) {
			totalyPlanned += Integer.parseInt(String.valueOf(budgetPlanningForMonth.get(categoryBudget)));
		}
		if (totalyPlanned > availableIncome.intValue()) {
			throw new AccountingException("budget ("+availableIncome+") was overplanned ("+totalyPlanned+")", AccountingError.BUDGET_OVERPLANNED, null);
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
	*/
}