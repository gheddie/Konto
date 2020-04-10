package de.gravitex.accounting.modality;

import de.gravitex.accounting.enumeration.PaymentPeriod;

public class UndefinedPeriodOutgoingPaymentModality extends OutgoingPaymentModality {

	public UndefinedPeriodOutgoingPaymentModality() {
		super(PaymentPeriod.UNDEFINED);
	}
}