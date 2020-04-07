package de.gravitex.accounting.modality;

public class UndefinedPeriodOutgoingPaymentModalityDefinition extends OutgoingPaymentModalityDefinition {

	public UndefinedPeriodOutgoingPaymentModalityDefinition() {
		super(PaymentPeriod.UNDEFINED);
	}
}