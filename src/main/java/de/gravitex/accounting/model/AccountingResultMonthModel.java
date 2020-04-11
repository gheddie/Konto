package de.gravitex.accounting.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.gravitex.accounting.model.AccountingResultCategoryModel;
import lombok.Data;

@Data
public class AccountingResultMonthModel {

	private HashMap<String, AccountingResultCategoryModel> categoryModels = new HashMap<String, AccountingResultCategoryModel>();

	public void addCategoryModel(AccountingResultCategoryModel accountingResultCategoryModel) {
		categoryModels.put(accountingResultCategoryModel.getCategory(), accountingResultCategoryModel);
	}

	public Set<String> getDistinctCategories() {
		Set<String> result = new HashSet<String>();
		for (String key : categoryModels.keySet()) {
			result.add(key);
		}
		return result;
	}

	public AccountingResultCategoryModel getCategoryModel(String category) {
		return categoryModels.get(category);
	}

	public BigDecimal calculateOverallSum() {
		BigDecimal result = new BigDecimal(0);
		for (String monthKey : categoryModels.keySet()) {
			result = result.add(categoryModels.get(monthKey).getSum());
		}
		return result;
	}
}