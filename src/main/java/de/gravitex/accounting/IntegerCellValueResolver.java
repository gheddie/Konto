package de.gravitex.accounting;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.resolver.CellValueResolver;

public class IntegerCellValueResolver implements CellValueResolver<Integer> {

	public Integer resolveCellValue(Cell cell) {
		return (int) cell.getNumericCellValue();
	}
}