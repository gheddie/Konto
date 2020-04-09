package de.gravitex.accounting.modality;

public abstract class IncomingPaymentModality extends PaymentModality {

	public IncomingPaymentModality(PaymentPeriod aPaymentPeriod) {
		super(aPaymentPeriod);
	}

	@Override
	protected PaymentType getPaymentType() {
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
}