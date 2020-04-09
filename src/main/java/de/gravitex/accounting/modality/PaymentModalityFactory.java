package de.gravitex.accounting.modality;

import java.util.HashMap;

import de.gravitex.accounting.AccountingCategory;
import de.gravitex.accounting.AccountingError;
import de.gravitex.accounting.exception.AccountingException;

public class PaymentModalityFactory {

	private static final HashMap<AccountingCategory, PaymentModalityDefinition> paymentModalityDefinitions = new HashMap<AccountingCategory, PaymentModalityDefinition>();
	static {
		paymentModalityDefinitions.put(AccountingCategory.Auto, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Undefiniert, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Kreditkarte, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Paypal, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Nebenkosten, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.MONTH, 100));
		paymentModalityDefinitions.put(AccountingCategory.Nahverkehr, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Telekommunikation, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Essen, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Sonstiges, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Abo, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Lebensversicherung, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.HALF_YEAR, 100));
		paymentModalityDefinitions.put(AccountingCategory.Miete, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.MONTH, 530));
		paymentModalityDefinitions.put(AccountingCategory.Unterhalt, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.MONTH, 100));
		paymentModalityDefinitions.put(AccountingCategory.Benzin, new UndefinedPeriodOutgoingPaymentModalityDefinition(150));
		paymentModalityDefinitions.put(AccountingCategory.Fahrrad, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Charity, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.QUARTER, 100));
		paymentModalityDefinitions.put(AccountingCategory.Musik, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Einrichtung, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.AbhebungEC, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Kippen, new UndefinedPeriodOutgoingPaymentModalityDefinition(100));
		paymentModalityDefinitions.put(AccountingCategory.Fitnessstudio, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.MONTH, 100));
		paymentModalityDefinitions.put(AccountingCategory.Rundfunk, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.YEAR, 100));
		paymentModalityDefinitions.put(AccountingCategory.Krankengeld, new FixedPeriodIncomingPaymentModalityDefinition(PaymentPeriod.MONTH));
	}

	public static PaymentModalityDefinition getPaymentModality(String categoryKey) {
		PaymentModalityDefinition paymentModality = paymentModalityDefinitions.get(AccountingCategory.valueOf(categoryKey));
		if (paymentModality == null) {
			throw new AccountingException("no payment modality found for category '" + categoryKey + "'!!",
					AccountingError.NO_PM_FOR_CATEGORY, null);
		}
		return paymentModality;
	}
}