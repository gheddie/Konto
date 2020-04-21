package de.gravitex.accounting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;

import de.gravitex.accounting.application.AccountingSingleton;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.modality.FixedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;

public class PrepareBudgetsTest {

	private static final MonthKey MONTHKEY_JANUARY = MonthKey.fromValues(1, 2020);
	private static final MonthKey MONTHKEY_FEBRUARY = MonthKey.fromValues(2, 2020);

	private static final String CATEGORY_A_MONTH = "CAT_A";
	private static final String CATEGORY_B_MONTH = "CAT_B";
	private static final String CATEGORY_C_HALF_YEAR = "CAT_C";

	@Test
	public void testPrepareBudgets() {

		HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
		paymentModalitys.put(CATEGORY_A_MONTH, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));
		paymentModalitys.put(CATEGORY_B_MONTH, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));
		paymentModalitys.put(CATEGORY_C_HALF_YEAR, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.HALF_YEAR));

		// ---

		AccountingManager accountingManager = new AccountingManager()
				.withAccountingData(createAccountingData())
				.withSettings(AccountManagerSettings.fromValues(true, 12, true, true));
		HashMap<MonthKey, Properties> preparedBudgets = accountingManager
				.prepareBudgets(LocalDate.of(2020, 1, 1), false);

		/**
		 * In Jan, an amount was spent for fixed modality 'CATEGORY_B', so there should
		 * be a budget planned for Feb month, too...
		 */
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(2, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(3, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(4, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(5, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(6, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(7, 2020)).get(CATEGORY_B_MONTH));
		assertEquals(String.valueOf(75), preparedBudgets.get(MonthKey.fromValues(8, 2020)).get(CATEGORY_B_MONTH));
		
		/**
		 * 'CAT_C' was spent in Feb, so again in august (NOT before)...
		 */
		assertNull(preparedBudgets.get(MonthKey.fromValues(2, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertNull(preparedBudgets.get(MonthKey.fromValues(3, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertNull(preparedBudgets.get(MonthKey.fromValues(4, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertNull(preparedBudgets.get(MonthKey.fromValues(5, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertNull(preparedBudgets.get(MonthKey.fromValues(6, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertNull(preparedBudgets.get(MonthKey.fromValues(7, 2020)).get(CATEGORY_C_HALF_YEAR));
		assertEquals(String.valueOf(50), preparedBudgets.get(MonthKey.fromValues(8, 2020)).get(CATEGORY_C_HALF_YEAR));
	}

	private AccountingData createAccountingData() {

		AccountingData accountingData = new AccountingData();
		
		HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
		paymentModalitys.put(CATEGORY_A_MONTH, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));
		paymentModalitys.put(CATEGORY_B_MONTH, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));
		paymentModalitys.put(CATEGORY_C_HALF_YEAR, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.HALF_YEAR));
		
		accountingData.setPaymentModalitys(paymentModalitys);
		accountingData.setAccountingType(AccountingType.MAIN_ACCOUNT);
		
		HashMap<MonthKey, AccountingMonth> data = new HashMap<MonthKey, AccountingMonth>();

		// --- January

		ArrayList<AccountingRow> rowObjectsJanuary = new ArrayList<AccountingRow>();
		rowObjectsJanuary.add(getAccountingRow(1, LocalDate.of(2020, 1, 1), CATEGORY_A_MONTH, new BigDecimal(10)));
		rowObjectsJanuary.add(getAccountingRow(2, LocalDate.of(2020, 1, 1), CATEGORY_B_MONTH, new BigDecimal(20)));

		// --- February

		ArrayList<AccountingRow> rowObjectsFebruary = new ArrayList<AccountingRow>();
		rowObjectsFebruary.add(getAccountingRow(3, LocalDate.of(2020, 2, 1), CATEGORY_A_MONTH, new BigDecimal(10)));
		rowObjectsFebruary.add(getAccountingRow(4, LocalDate.of(2020, 2, 1), CATEGORY_B_MONTH, new BigDecimal(75)));
		rowObjectsFebruary.add(getAccountingRow(4, LocalDate.of(2020, 2, 1), CATEGORY_C_HALF_YEAR, new BigDecimal(50)));

		// --- Accounting data

		data.put(MONTHKEY_JANUARY, AccountingMonth.fromValues(MONTHKEY_JANUARY, rowObjectsJanuary));
		data.put(MONTHKEY_FEBRUARY, AccountingMonth.fromValues(MONTHKEY_FEBRUARY, rowObjectsFebruary));

		accountingData.setData(data);
		
		return accountingData;
	}

	private AccountingRow getAccountingRow(Integer runningIndex, LocalDate date, String category, BigDecimal amount) {

		AccountingRow accountingRow = new AccountingRow();
		accountingRow.setRunningIndex(runningIndex);
		accountingRow.setDate(date);
		accountingRow.setCategory(category);
		accountingRow.setAmount(amount);
		return accountingRow;
	}
}