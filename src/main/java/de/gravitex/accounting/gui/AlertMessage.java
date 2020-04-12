package de.gravitex.accounting.gui;

import lombok.Data;

@Data
public class AlertMessage {

	private AlertMessageType alertMessageType;
	
	private String text;
	
	private AlertMessage() {
		// ...
	}
	
	public static AlertMessage fromValues(AlertMessageType anAlertMessageType, String aText) {
		AlertMessage alertMessage = new AlertMessage();
		alertMessage.setAlertMessageType(anAlertMessageType);
		alertMessage.setText(aText);
		return alertMessage;
	}
}