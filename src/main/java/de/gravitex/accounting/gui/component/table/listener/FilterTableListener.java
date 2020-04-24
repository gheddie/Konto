package de.gravitex.accounting.gui.component.table.listener;

import java.awt.Color;

public interface FilterTableListener {

	Color getRowColor(int row);

	void rowDoubleClicked(int selectedRow);
}