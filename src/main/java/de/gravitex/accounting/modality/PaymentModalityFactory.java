package de.gravitex.accounting.modality;

import java.util.HashMap;

import de.gravitex.accounting.AccountingCategory;
import de.gravitex.accounting.AccountingError;
import de.gravitex.accounting.exception.AccountingException;

public class PaymentModalityFactory {

	private static final HashMap<AccountingCategory, PaymentModality> paymentModalityDefinitions = new HashMap<AccountingCategory, PaymentModality>();
	static {
		paymentModalityDefinitions.put(AccountingCategory.Auto, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Undefiniert, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Kreditkarte, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Paypal, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Nebenkosten, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Nahverkehr, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Telekommunikation, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Essen, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Sonstiges, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Abo, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Lebensversicherung, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.HALF_YEAR));
		paymentModalityDefinitions.put(AccountingCategory.Miete, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Unterhalt, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Benzin, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Fahrrad, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Charity, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.QUARTER));
		paymentModalityDefinitions.put(AccountingCategory.Musik, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Einrichtung, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.AbhebungEC, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Kippen, new UndefinedPeriodOutgoingPaymentModality());
		paymentModalityDefinitions.put(AccountingCategory.Fitnessstudio, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.MONTH));
		paymentModalityDefinitions.put(AccountingCategory.Rundfunk, new FixedPeriodPaymentOutgoingModality(PaymentPeriod.YEAR));
		paymentModalityDefinitions.put(AccountingCategory.Krankengeld, new FixedPeriodIncomingPaymentModality(PaymentPeriod.MONTH));
	}

	public static PaymentModality getPaymentModality(String categoryKey) {
		PaymentModality paymentModality = paymentModalityDefinitions.get(AccountingCategory.valueOf(categoryKey));
		if (paymentModality == null) {
			throw new AccountingException("no payment modality found for category '" + categoryKey + "'!!",
					AccountingError.NO_PM_FOR_CATEGORY, null);
		}
		return paymentModality;
	}
}