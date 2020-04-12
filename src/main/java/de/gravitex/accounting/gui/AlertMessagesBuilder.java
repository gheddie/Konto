package de.gravitex.accounting.gui;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AlertMessagesBuilder {
	
	private List<AlertMessage> alertMessages = new ArrayList<AlertMessage>();

	public AlertMessagesBuilder withMessage(AlertMessageType alertMessageType, String text) {
		alertMessages.add(AlertMessage.fromValues(alertMessageType, text));
		return this;
	}
}