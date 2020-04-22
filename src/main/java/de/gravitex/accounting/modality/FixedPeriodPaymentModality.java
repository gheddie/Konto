package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;

public class FixedPeriodPaymentModality extends PaymentModality {

	public FixedPeriodPaymentModality(PaymentPeriod aPaymentPeriod, PaymentType aPaymentType) {
		super(aPaymentPeriod, aPaymentType);
	}
	
	@Override
	public boolean isProjectable() {
		return (getPaymentType().equals(PaymentType.OUTGOING));
	}
	
	@Override
	public boolean isPeriodically() {
		return true;
	}
}