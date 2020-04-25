package de.gravitex.accounting.modality;

import java.math.BigDecimal;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;
import de.gravitex.accounting.util.MonthKey;
import lombok.Data;

@Data
public abstract class PaymentModality {
	
	private static final BigDecimal ZERO = new BigDecimal(0);

	private MonthKey monthKey;
	
	private String category;
	
	private PaymentPeriod paymentPeriod;
	
	private BigDecimal totalAmount = new BigDecimal(0);

	private PaymentType paymentType;
	
	public PaymentModality(PaymentPeriod aPaymentPeriod, PaymentType aPaymentType) {
		super();
		this.paymentPeriod = aPaymentPeriod;
		this.paymentType = aPaymentType;
	}

	public void reset() {
		totalAmount = new BigDecimal(0);
	}

	public boolean isProjectable() {
		// to be overwritten...
		return false;
	}

	public boolean isPeriodically() {
		return false;
	}

	public boolean checkAmount(AccountingRow accountingRow) {
		
		boolean result = false;
		BigDecimal amount = accountingRow.getAmount();
		switch (paymentType) {
		case IN_OUT:
			result = true;
			break;
		case INCOMING:
			result = amount.compareTo(ZERO) > 0;
			break;
		case OUTGOING:
			result = amount.compareTo(ZERO) < 0;
			break;
		}
		return result ;
	}

	public boolean isOutgoing() {
		return (paymentType.equals(PaymentType.OUTGOING) || paymentType.equals(PaymentType.IN_OUT));
	}
}