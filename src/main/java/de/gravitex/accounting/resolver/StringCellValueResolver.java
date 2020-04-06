package de.gravitex.accounting.resolver;

import org.apache.poi.ss.usermodel.Cell;

public class StringCellValueResolver implements CellValueResolver<String> {

	public String resolveCellValue(Cell cell) {
		return cell.getStringCellValue();
	}
}