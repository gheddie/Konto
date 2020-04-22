package de.gravitex.accounting.filter.interfacing;

import java.awt.Color;

public interface FilteredComponentListener {

	void filterDataChanged();

	void itemSelected(Object object);

	Color getRowColor(Object object);
}