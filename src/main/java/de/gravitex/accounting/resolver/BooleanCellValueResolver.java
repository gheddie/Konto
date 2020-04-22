package de.gravitex.accounting.resolver;

import org.apache.poi.ss.usermodel.Cell;

public class BooleanCellValueResolver implements CellValueResolver<Boolean> {
	
	private static final String CHECKED = "X";

	public Boolean resolveCellValue(Cell cell) {
		String value = cell.getStringCellValue();
		if (value != null && value.equals(CHECKED)) {
			return true;
		}
		return false;
	}
}