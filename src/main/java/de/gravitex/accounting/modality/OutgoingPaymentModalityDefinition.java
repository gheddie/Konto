package de.gravitex.accounting.modality;

public abstract class OutgoingPaymentModalityDefinition extends PaymentModalityDefinition {

	public OutgoingPaymentModalityDefinition(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.OUTGOING;
	}
}