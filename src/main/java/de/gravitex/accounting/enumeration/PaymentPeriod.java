package de.gravitex.accounting.enumeration;

public enum PaymentPeriod {
	MONTH("Monatlich", 1),
	QUARTER("Quartalsweise", 3),
	HALF_YEAR("Halbj�hrlich", 6),
	YEAR("J�hrlich", 12),
	UNDEFINED("Undefiniert", 0);
	
	private String translation;
	
	private int durationInMonths;

	PaymentPeriod(String aTranslation, int aDurationInMonths) {
		this.translation = aTranslation;
		this.durationInMonths = aDurationInMonths;
	}
	
	public String getTranslation() {
		return translation;
	}
	
	public int getDurationInMonths() {
		return durationInMonths;
	}
}