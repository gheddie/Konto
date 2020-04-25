package de.gravitex.accounting.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.AccountingUtil;
import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.enumeration.AlertMessageType;
import de.gravitex.accounting.util.MonthKey;

public class AccountingGuiHelper {
	
	private static final Logger logger = Logger.getLogger(AccountingGuiHelper.class);


}