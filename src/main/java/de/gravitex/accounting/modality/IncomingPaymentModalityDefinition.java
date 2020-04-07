package de.gravitex.accounting.modality;

public abstract class IncomingPaymentModalityDefinition extends PaymentModalityDefinition {

	public IncomingPaymentModalityDefinition(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.INCOMING;
	}
}