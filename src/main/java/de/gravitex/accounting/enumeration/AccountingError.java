package de.gravitex.accounting.enumeration;

public enum AccountingError {
	
	NO_DATE,
	
	// Undefind category without a text set...
	UNDEF_NO_TEXT,
	
	NO_CATEGORY,
	
	// more than one category read in  a row...
	MULTIPLE_CATEGORIES,
	
	NO_PM_FOR_CATEGORY,
	
	NO_RUNNING_INDEX,
	
	INVALID_SALDO_REF,
	
	BUDGET_OVERPLANNED,
	
	NO_AMOUNT,
	
	NO_VALID_PERIOD
}