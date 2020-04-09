package de.gravitex.accounting.modality;

import java.math.BigDecimal;

import lombok.Data;

@Data
public abstract class PaymentModalityDefinition {
	
	private PaymentPeriod paymentPeriod;
	
	private BigDecimal totalAmount = new BigDecimal(0);
	
	public PaymentModalityDefinition(PaymentPeriod aPaymentPeriod) {
		super();
		this.paymentPeriod = aPaymentPeriod;
	}

	protected abstract PaymentType getPaymentType();

	public abstract StringBuffer getCalculationFooter();

	public void addAmount(BigDecimal anAmount) {
		totalAmount = totalAmount.add(anAmount);
	}
}