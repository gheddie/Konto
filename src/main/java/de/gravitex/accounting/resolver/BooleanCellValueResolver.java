package de.gravitex.accounting.resolver;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.CellValueResolver;

public class BooleanCellValueResolver implements CellValueResolver<Boolean> {
	
	private static final String CHECKED_CATEGORY = "X";

	public Boolean resolveCellValue(Cell cell) {
		String value = cell.getStringCellValue();
		if (value != null && value.equals(CHECKED_CATEGORY)) {
			return true;
		}
		return false;
	}
}