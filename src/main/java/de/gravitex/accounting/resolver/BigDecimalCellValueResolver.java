package de.gravitex.accounting.resolver;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;

public class BigDecimalCellValueResolver implements CellValueResolver<BigDecimal> {

	public BigDecimal resolveCellValue(Cell cell) {
		// TODO works, but a better way?!?
		return new BigDecimal(String.valueOf(cell.getNumericCellValue()));
	}
}