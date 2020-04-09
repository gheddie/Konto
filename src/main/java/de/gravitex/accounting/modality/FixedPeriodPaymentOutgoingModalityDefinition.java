package de.gravitex.accounting.modality;

public class FixedPeriodPaymentOutgoingModalityDefinition extends OutgoingPaymentModalityDefinition {

	public FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod aPaymentPeriod, int aLimit) {
		super(aPaymentPeriod, aLimit);
	}
}