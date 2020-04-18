package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;

public class FixedPeriodIncomingPaymentModality extends IncomingPaymentModality {

	public FixedPeriodIncomingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}
	
	@Override
	public boolean isPeriodically() {
		return true;
	}
}