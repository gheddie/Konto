package de.gravitex.accounting.resolver;

import java.time.LocalDate;
import java.time.ZoneId;

import org.apache.poi.ss.usermodel.Cell;

import de.gravitex.accounting.CellValueResolver;

public class LocalDateCellValueResolver implements CellValueResolver<LocalDate> {

	public LocalDate resolveCellValue(Cell cell) {
		return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}