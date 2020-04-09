package de.gravitex.accounting.modality;

public class UndefinedPeriodOutgoingPaymentModality extends OutgoingPaymentModality {

	public UndefinedPeriodOutgoingPaymentModality() {
		super(PaymentPeriod.UNDEFINED);
	}
}