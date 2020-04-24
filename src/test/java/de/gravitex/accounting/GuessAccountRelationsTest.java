package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.gravitex.accounting.application.AccountingLoader;
import de.gravitex.accounting.enumeration.AccountingType;
import de.gravitex.accounting.enumeration.SubAccountReferenceCheck;
import de.gravitex.accounting.validation.SubAccountValidation;
import lombok.Data;

public class GuessAccountRelationsTest {

	// @Test
	public void testGuessAccountRelations() {
		
		AccountingManager manager = new AccountingManager();
		AccountingData mainAccountVB = new AccountingLoader().loadAccountingData("VB", AccountingType.MAIN_ACCOUNT);
		AccountingData subAccount = mainAccountVB.assertSubAccountPresent("VISA");
		manager.setMainAccount(mainAccountVB);
		
		// checkCategory(manager, "Kreditkarte");
		checkCategory(manager, "Paypal", filterForNoMainAccountReference(subAccount.getAllEntriesSorted()));
	}

	private List<AccountingRow> filterForNoMainAccountReference(List<AccountingRow> allEntriesSorted) {
		List<AccountingRow> result = new ArrayList<AccountingRow>();
		for (AccountingRow accountingRow : allEntriesSorted) {
			if (accountingRow.getMainAccountReference() == null) {
				result.add(accountingRow);
			}
		}
		return result;
	}

	private void checkCategory(AccountingManager manager, String category, List<AccountingRow> subAccountEntries) {
		System.out.println("checking category [" + category + "]...............................................");
		SubAccountValidation subAccountValidation = null;
		for (AccountingRow mainAccountingRow : manager.getAllEntriesForCategory(category)) {
			subAccountValidation = manager.checkSubEntries(mainAccountingRow);
			if (subAccountValidation.getSubAccountReferenceCheck().equals(SubAccountReferenceCheck.INVALID)) {
				// System.out.println(subAccountValidation.getSubAccountReferenceCheck() + " ("+subAccountValidation+")");
				findMatch(mainAccountingRow, subAccountEntries);
			}
		}
	}

	private void findMatch(AccountingRow mainAccountingRow, List<AccountingRow> subAccountEntries) {
		
		new Shaker(mainAccountingRow, subAccountEntries).shake(50);
		
		/*
		System.out.println("finding match [" + mainAccountingRow + "]...............................................");
		for (AccountingRow subAccountingRow : subAccountEntries) {
			if (mainAccountingRow.getAmount().abs().equals(subAccountingRow.getAmount().abs())) {
				System.out.println("[" + mainAccountingRow.getDate() + "]" + mainAccountingRow.getAmount()
				+ " <-> [" + subAccountingRow.getDate() + "] " + subAccountingRow.getAmount()
				+ " <--- MATCH? [MAIN ROW="+mainAccountingRow.getRunningIndex()+", SUB ROW="+subAccountingRow.getRunningIndex()+"]");
			} else {
				System.out.println("[" + mainAccountingRow.getDate() + "]" + mainAccountingRow.getAmount()
				+ " <-> [" + subAccountingRow.getDate() + "] " + subAccountingRow.getAmount());
			}
		}
		*/
	}
	
	// ---
	
	private static class Shaker {
		
		private static final Random random = new Random();

		private AccountingRow mainAccountingRow;
		
		private List<AccountingRow> subAccountRows;
		
		// private Set<Integer> subRowIndicies = new HashSet<Integer>();

		public Shaker(AccountingRow aMainAccountingRow, List<AccountingRow> aSubAccountRows) {
			super();
			this.mainAccountingRow = aMainAccountingRow;
			this.subAccountRows = aSubAccountRows;
		}

		public ShakerResult shake(int tries) {
			ShakerResult result = tryShake();
			System.out.println(result);
			int count = 0;
			while (!result.isValid() && count < tries) {
				result = tryShake();
				System.out.println(result);
				count++;
			}
			return result;
		}

		private ShakerResult tryShake() {
			int howManyRows = random.nextInt(subAccountRows.size());
			Set<Integer> subRowIndicies = new HashSet<Integer>();
			while (subRowIndicies .size() < howManyRows) {
				subRowIndicies.add(random.nextInt(subAccountRows.size()));
			}
			BigDecimal sum = new BigDecimal(0);
			for (Integer subRowIndex : subRowIndicies) {
				sum = sum.add(subAccountRows.get(subRowIndex).getAmount().abs());
			}
			BigDecimal target = mainAccountingRow.getAmount().abs();
			if (target.equals(sum)) {
				return ShakerResult.fromValues(ShakerResultType.VALID, getRows(subRowIndicies), target, sum, subRowIndicies);
			} else {
				return ShakerResult.fromValues(ShakerResultType.INVALID, getRows(subRowIndicies), target, sum, subRowIndicies);
			}
		}

		private List<AccountingRow> getRows(Set<Integer> subRowIndicies) {
			List<AccountingRow> result = new ArrayList<AccountingRow>();
			for (Integer index : subRowIndicies) {
				result.add(subAccountRows.get(index));
			}
			return result;
		}
	}
	
	// ---
	
	@Data
	public static class ShakerResult {
		
		private List<AccountingRow> rows;
		
		private ShakerResultType shakerResultType;
		
		private BigDecimal targetAmount;
		
		private BigDecimal actualAmount;
		
		private Set<Integer> randomIndicies;
	
		private ShakerResult() {
			super();
		}

		public boolean isValid() {
			return shakerResultType.equals(ShakerResultType.VALID);
		}

		public static ShakerResult fromValues(ShakerResultType aShakerResultType, List<AccountingRow> aRows,
				BigDecimal aTargetAmount, BigDecimal anActualAmount, Set<Integer> aRandomIndicies) {
			ShakerResult shakerResult = new ShakerResult();
			shakerResult.setShakerResultType(aShakerResultType);
			shakerResult.setRows(aRows);
			shakerResult.setTargetAmount(aTargetAmount);
			shakerResult.setActualAmount(anActualAmount);
			shakerResult.setRandomIndicies(aRandomIndicies);
			return shakerResult;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("------------------------------\n");
			sb.append(shakerResultType);
			sb.append("\n");
			sb.append(rows.size() + " rows");
			sb.append("\n");
			sb.append("target: " + targetAmount + ", actual: " + actualAmount);
			sb.append("\n");
			sb.append("indicies: " + randomIndicies);
			sb.append("\n");
			sb.append("------------------------------\n");
			return sb.toString();
		}
	}
	
	// ---
	
	private enum ShakerResultType {
		VALID, INVALID
	}
}