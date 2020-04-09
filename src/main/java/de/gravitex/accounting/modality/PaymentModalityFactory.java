package de.gravitex.accounting.modality;

import java.util.HashMap;

import de.gravitex.accounting.AccountingCategory;
import de.gravitex.accounting.AccountingError;
import de.gravitex.accounting.exception.AccountingException;

public class PaymentModalityFactory {

	private static final HashMap<AccountingCategory, PaymentModality> paymentModalitys = new HashMap<AccountingCategory, PaymentModality>();
	static {
		paymentModalitys.put(AccountingCategory.Auto, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Undefiniert, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Kreditkarte, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Paypal, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Nebenkosten, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(null)));
		paymentModalitys.put(AccountingCategory.Nahverkehr, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Telekommunikation, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Essen, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Sonstiges, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Abo, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Lebensversicherung, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(null)));
		paymentModalitys.put(AccountingCategory.Miete, PaymentModality.fromValues(530, new FixedPeriodPaymentOutgoingModalityDefinition(null)));
		paymentModalitys.put(AccountingCategory.Unterhalt, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.MONTH)));
		paymentModalitys.put(AccountingCategory.Benzin, PaymentModality.fromValues(150, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Fahrrad, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Charity, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(null)));
		paymentModalitys.put(AccountingCategory.Musik, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Einrichtung, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.AbhebungEC, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Kippen, PaymentModality.fromValues(100, new UndefinedPeriodOutgoingPaymentModalityDefinition()));
		paymentModalitys.put(AccountingCategory.Fitnessstudio, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(null)));
		paymentModalitys.put(AccountingCategory.Rundfunk, PaymentModality.fromValues(100, new FixedPeriodPaymentOutgoingModalityDefinition(PaymentPeriod.YEAR)));
		paymentModalitys.put(AccountingCategory.Krankengeld, PaymentModality.fromValues(100, new FixedPeriodIncomingPaymentModalityDefinition(null)));
	}

	public static PaymentModality getPaymentModality(String categoryKey) {
		PaymentModality paymentModality = paymentModalitys.get(AccountingCategory.valueOf(categoryKey));
		if (paymentModality == null) {
			throw new AccountingException("no payment modality found for category '" + categoryKey + "'!!",
					AccountingError.NO_PM_FOR_CATEGORY, null);
		}
		return paymentModality;
	}
}