package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;

public abstract class IncomingPaymentModality extends PaymentModality {

	public IncomingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.INCOMING;
	}
	
	@Override
	public StringBuffer getCalculationFooter() {
		return new StringBuffer().append("SUMME ------> " + getTotalAmount());
	}
	
	@Override
	public void prepare() {
		// TODO Auto-generated method stub
	}
}