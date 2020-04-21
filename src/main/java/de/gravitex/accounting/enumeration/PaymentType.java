package de.gravitex.accounting.enumeration;

public enum PaymentType {
	OUTGOING("Ausgehend"),
	INCOMING("Eingehend"),
	IN_OUT("Ein- und Ausgehend");
	
	private String translation;
	
	PaymentType(String aTranslation) {
		this.translation = aTranslation;
	}
	
	public String getTranslation() {
		return translation;
	}
}