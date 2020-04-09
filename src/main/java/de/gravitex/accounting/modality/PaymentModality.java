package de.gravitex.accounting.modality;

import java.math.BigDecimal;

import lombok.Data;

@Data
public abstract class PaymentModality {
	
	private String monthKey;
	
	private String category;
	
	private PaymentPeriod paymentPeriod;
	
	private BigDecimal totalAmount = new BigDecimal(0);
	
	public PaymentModality(PaymentPeriod aPaymentPeriod) {
		super();
		this.paymentPeriod = aPaymentPeriod;
	}

	protected abstract PaymentType getPaymentType();

	public abstract StringBuffer getCalculationFooter();

	public void addAmount(BigDecimal anAmount) {
		totalAmount = totalAmount.add(anAmount);
	}

	public abstract void prepare();

	public void reset() {
		totalAmount = new BigDecimal(0);
	}
}