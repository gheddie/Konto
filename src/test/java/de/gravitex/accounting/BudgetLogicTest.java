package de.gravitex.accounting;

import java.util.Properties;

import org.junit.Test;

import de.gravitex.accounting.application.AccountingLoader;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.logic.BudgetModel;
import de.gravitex.accounting.util.MonthKey;

public class BudgetLogicTest {

	@Test
	public void testBudgetLogic() {
		
		MonthKey monthKey = MonthKey.fromValues(4, 2020);

		AccountingManager manager = new AccountingManager();
		AccountingData mainAccountVB = new AccountingLoader().loadAccountingData("VB", AccountingType.MAIN_ACCOUNT);
		manager.setMainAccount(mainAccountVB);
		Properties aProperties = new Properties();
		aProperties.put(monthKey.toString(), "10000");
		manager.withIncome(Income.fromValues(aProperties));
		
		BudgetModel budgetModel = manager.getBudgetModel(monthKey, false, false, false);
		System.out.println(budgetModel);
	}

	private AccountingData getAccount(String string, AccountingType mainAccount) {
		
		AccountingData account = new AccountingData();
		account.setAccountingType(AccountingType.MAIN_ACCOUNT);
		return account;
	}
}