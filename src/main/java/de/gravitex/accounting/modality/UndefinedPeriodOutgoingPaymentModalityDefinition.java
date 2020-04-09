package de.gravitex.accounting.modality;

public class UndefinedPeriodOutgoingPaymentModalityDefinition extends OutgoingPaymentModalityDefinition {

	public UndefinedPeriodOutgoingPaymentModalityDefinition(int aLimit) {
		super(PaymentPeriod.UNDEFINED, aLimit);
	}
}