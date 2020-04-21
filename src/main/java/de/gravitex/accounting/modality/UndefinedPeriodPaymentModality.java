package de.gravitex.accounting.modality;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.enumeration.PaymentType;

public class UndefinedPeriodPaymentModality extends PaymentModality {

	public UndefinedPeriodPaymentModality(PaymentType aPaymentType) {
		super(PaymentPeriod.UNDEFINED, aPaymentType);
	}
}