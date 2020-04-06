package de.gravitex.accounting;

import org.apache.poi.ss.usermodel.Cell;

public interface CellValueResolver<T> {

	public T resolveCellValue(Cell cell);
}