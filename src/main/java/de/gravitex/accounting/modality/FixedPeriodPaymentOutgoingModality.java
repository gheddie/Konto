package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;

public class FixedPeriodPaymentOutgoingModality extends OutgoingPaymentModality {

	public FixedPeriodPaymentOutgoingModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}
}