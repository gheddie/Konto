package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;

public class FixedPeriodOutgoingPaymentModality extends OutgoingPaymentModality {

	public FixedPeriodOutgoingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}
}