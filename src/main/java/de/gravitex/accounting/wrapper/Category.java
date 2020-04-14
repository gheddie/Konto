package de.gravitex.accounting.wrapper;

import de.gravitex.accounting.modality.PaymentModality;
import lombok.Data;

@Data
public class Category {

	private String category;
	
	private PaymentModality paymentModality;
	
	private Category() {
		// ...
	}

	public static Category fromValues(String aCategory, PaymentModality aPaymentModality) {
		Category categoryWrapper = new Category();
		categoryWrapper.setCategory(aCategory);
		categoryWrapper.setPaymentModality(aPaymentModality);
		return categoryWrapper;
	}
	
	public String toString() {
		return category;
	}
}