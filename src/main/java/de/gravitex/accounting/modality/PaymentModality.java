package de.gravitex.accounting.modality;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentModality {
	
	private BigDecimal limit;
	
	private PaymentModalityDefinition paymentModalityDefinition;
	
	public static PaymentModality fromValues(int aLimit, PaymentModalityDefinition aPaymentModalityDefinition) {
		PaymentModality paymentModality = new PaymentModality();
		paymentModality.setLimit(new BigDecimal(aLimit));
		paymentModality.setPaymentModalityDefinition(aPaymentModalityDefinition);
		return paymentModality;
	}

	public boolean amountExceeded(BigDecimal totalAmount) {
		return limit.compareTo(totalAmount.abs()) < 0;
	}
}