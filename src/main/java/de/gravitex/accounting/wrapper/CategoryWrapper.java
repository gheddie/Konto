package de.gravitex.accounting.wrapper;

import de.gravitex.accounting.modality.PaymentModality;
import lombok.Data;

@Data
public class CategoryWrapper {

	private String category;
	
	private PaymentModality paymentModality;
	
	private CategoryWrapper() {
		// ...
	}

	public static CategoryWrapper fromValues(String aCategory, PaymentModality aPaymentModality) {
		CategoryWrapper categoryWrapper = new CategoryWrapper();
		categoryWrapper.setCategory(aCategory);
		categoryWrapper.setPaymentModality(aPaymentModality);
		return categoryWrapper;
	}
	
	public String toString() {
		return category;
	}
}