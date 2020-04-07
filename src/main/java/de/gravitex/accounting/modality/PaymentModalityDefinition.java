package de.gravitex.accounting.modality;

import lombok.Data;

@Data
public abstract class PaymentModalityDefinition {
	
	private PaymentPeriod paymentPeriod;
	
	public PaymentModalityDefinition(PaymentPeriod aPaymentPeriod) {
		super();
		this.paymentPeriod = aPaymentPeriod;
	}

	protected abstract PaymentType getPaymentType();
}