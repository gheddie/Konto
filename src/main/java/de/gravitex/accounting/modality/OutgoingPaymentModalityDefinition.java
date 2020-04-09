package de.gravitex.accounting.modality;

import lombok.Data;

@Data
public abstract class OutgoingPaymentModalityDefinition extends PaymentModalityDefinition {
	
	private int limit;

	public OutgoingPaymentModalityDefinition(PaymentPeriod aPaymentPeriod, int aLimit) {
		super(aPaymentPeriod);
		this.limit = aLimit;
	}

	@Override
	protected PaymentType getPaymentType() {
		return PaymentType.OUTGOING;
	}
	
	@Override
	public StringBuffer getCalculationFooter() {
		StringBuffer buffer = new StringBuffer();
		if (amountExceeded()) {
			buffer.append("SUMME ------> " + getTotalAmount() + " [BUDGET EXCEEDED (" + limit + ")]");
		} else {
			buffer.append("SUMME ------> " + getTotalAmount() + " [IN BUDGET (" + limit + ")]");
		}
		return buffer;
	}

	private boolean amountExceeded() {
		return getTotalAmount().intValue() > limit;
	}
}