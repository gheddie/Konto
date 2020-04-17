package de.gravitex.accounting;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Test;

import de.gravitex.accounting.enumeration.PaymentPeriod;
import de.gravitex.accounting.modality.FixedPeriodIncomingPaymentModality;
import de.gravitex.accounting.modality.FixedPeriodOutgoingPaymentModality;
import de.gravitex.accounting.modality.PaymentModality;
import de.gravitex.accounting.setting.AccountManagerSettings;
import de.gravitex.accounting.util.MonthKey;

public class PrepareBudgetsTest {

	private static final MonthKey MONTHKEY_JANUARY = MonthKey.fromValues(1, 2020);
	private static final MonthKey MONTHKEY_FEBRUARY = MonthKey.fromValues(2, 2020);

	private static final String CATEGORY_A = "CAT_A";
	private static final String CATEGORY_B = "CAT_B";

	@Test
	public void testPrepareBudgets() {

		HashMap<String, PaymentModality> paymentModalitys = new HashMap<String, PaymentModality>();
		paymentModalitys.put(CATEGORY_A, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));
		paymentModalitys.put(CATEGORY_B, new FixedPeriodOutgoingPaymentModality(PaymentPeriod.MONTH));

		HashMap<MonthKey, BudgetPlanning> budgetPlannings = new HashMap<MonthKey, BudgetPlanning>();

		// --- January

		Properties propertiesJanuary = new Properties();
		budgetPlannings.put(MONTHKEY_JANUARY, BudgetPlanning.fromValues(propertiesJanuary));

		// --- February

		Properties propertiesFebruary = new Properties();
		budgetPlannings.put(MONTHKEY_FEBRUARY, BudgetPlanning.fromValues(propertiesFebruary));

		// ---

		HashMap<MonthKey, Properties> preparedBudgets = new AccountingManager().withPaymentModalitys(paymentModalitys)
				.withBudgetPlannings(budgetPlannings).withAccountingData(createAccountingData())
				.withSettings(AccountManagerSettings.fromValues(true, 12, true, true)).prepareBudgets();

		/**
		 * In Jan, an amount was spent for fixed modality 'CATEGORY_A', so there should
		 * be a budget planned for next month, too...
		 */
		assertNotNull(preparedBudgets.get(getFollowingMonthKey()).get(CATEGORY_B));
	}

	private MonthKey getFollowingMonthKey() {
		LocalDate following = LocalDate.now().plusMonths(1);
		MonthKey result = MonthKey.fromValues(following.getMonthValue(), following.getYear());
		return result;
	}

	private AccountingData createAccountingData() {

		AccountingData accountingData = new AccountingData();
		HashMap<MonthKey, AccountingMonth> data = new HashMap<MonthKey, AccountingMonth>();

		// --- January

		ArrayList<AccountingRow> rowObjectsJanuary = new ArrayList<AccountingRow>();
		rowObjectsJanuary.add(getAccountingRow(1, LocalDate.of(2020, 1, 1), CATEGORY_A, new BigDecimal(10)));
		rowObjectsJanuary.add(getAccountingRow(2, LocalDate.of(2020, 1, 1), CATEGORY_B, new BigDecimal(20)));

		// --- February

		ArrayList<AccountingRow> rowObjectsFebruary = new ArrayList<AccountingRow>();
		rowObjectsFebruary.add(getAccountingRow(3, LocalDate.of(2020, 2, 1), CATEGORY_A, new BigDecimal(10)));
		rowObjectsFebruary.add(getAccountingRow(4, LocalDate.of(2020, 2, 1), CATEGORY_B, new BigDecimal(20)));

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