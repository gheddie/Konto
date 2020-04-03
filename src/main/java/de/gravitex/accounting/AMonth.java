package de.gravitex.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class AMonth {
	
	private String monthKey;
	
	private List<RowObject> rowObjects;

	private AMonth() {

	}

	public static AMonth fromValues(String monthKey, List<RowObject> rowObjects) {
		AMonth aMonth = new AMonth();
		aMonth.setMonthKey(monthKey);
		aMonth.setRowObjects(rowObjects);
		return aMonth;
	}

	public void printSorted(boolean showObjects) {
		System.out.println(" ------ "+monthKey+" ------ ");
		BigDecimal totalPlusMinusAmount = new BigDecimal(0);
		HashMap<String, List<RowObject>> sortedByWhat = getSortedByWhat();
		for (String whatKey : sortedByWhat.keySet()) {
			BigDecimal totalWHATAmount = new BigDecimal(0);
			System.out.println(" ###### "+whatKey+" ###### ");
			for (RowObject obj : sortedByWhat.get(whatKey)) {
				if (showObjects) {
					System.out.println(obj.getBETRAG() + " [" + obj.getWHAT() + "] (" + obj.getDATUM() + ") ["
							+ Moo.kuh(obj.getDATUM()) + "]");	
				}
				totalWHATAmount = totalWHATAmount.add(obj.getBETRAG());
			}
			System.out.println(" ---------------> total WHAT : " + totalWHATAmount);
			totalPlusMinusAmount = totalPlusMinusAmount.add(totalWHATAmount);
		}
		System.out.println(" ---------------> total +/- : " + totalPlusMinusAmount );
	}

	private HashMap<String, List<RowObject>> getSortedByWhat() {
		HashMap<String, List<RowObject>> result = new HashMap<String, List<RowObject>>();
		for (RowObject rowObject : rowObjects) {
			if (result.get(rowObject.getWHAT()) == null) {
				result.put(rowObject.getWHAT(), new ArrayList());
			}
			result.get(rowObject.getWHAT()).add(rowObject);
		}
		return result;
	}
}