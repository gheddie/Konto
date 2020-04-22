package de.gravitex.accounting.validation;

import java.math.BigDecimal;

import de.gravitex.accounting.enumeration.SubAccountReferenceCheck;
import lombok.Data;

@Data
public class SubAccountValidation {

	private SubAccountReferenceCheck subAccountReferenceCheck;
	
	private BigDecimal targetAmount;
	
	private BigDecimal actualAmount;
	
	private SubAccountValidation() {
		super();
	}

	public static SubAccountValidation fromValues(SubAccountReferenceCheck subAccountReferenceCheck, BigDecimal targetAmount,
			BigDecimal actualAmount) {
		SubAccountValidation subAccountValidation = new SubAccountValidation();
		subAccountValidation.setSubAccountReferenceCheck(subAccountReferenceCheck);
		subAccountValidation.setTargetAmount(targetAmount);
		subAccountValidation.setActualAmount(actualAmount);
		return subAccountValidation;
	}
	
	public String toString() {
		return "[" + subAccountReferenceCheck + "] (Soll=" + targetAmount + " , Ist=" + actualAmount + ")";
	}
}