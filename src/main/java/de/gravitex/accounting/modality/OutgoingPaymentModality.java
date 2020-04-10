package de.gravitex.accounting.modality;

import de.gravitex.accounting.AccountingManager;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;
import lombok.Data;

@Data
public abstract class OutgoingPaymentModality extends PaymentModality {
	
	private int limit;

	public OutgoingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
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
		return Math.abs(getTotalAmount().intValue()) > limit;
	}
	
	@Override
	public void prepare() {
		// request limit
		setLimit(AccountingManager.getInstance().requestLimit(getMonthKey(), getCategory()));
	}
}