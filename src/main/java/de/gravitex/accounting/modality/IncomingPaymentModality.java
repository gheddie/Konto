package de.gravitex.accounting.modality;

import java.math.BigDecimal;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;

public abstract class IncomingPaymentModality extends PaymentModality {

	public IncomingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.INCOMING;
	}
	
	@Override
	public StringBuffer getCalculationFooter() {
		return new StringBuffer().append("SUMME ------> " + getTotalAmount());
	}
	
	@Override
	public void prepare() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean checkAmount(AccountingRow accountingRow) {
		boolean result = accountingRow.getAmount().compareTo(new BigDecimal(0)) > 0;
		if (!result) {
			int werner = 5;
		}
		return result;
	}
}