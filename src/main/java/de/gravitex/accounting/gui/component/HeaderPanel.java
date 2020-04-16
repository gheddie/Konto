package de.gravitex.accounting.gui.component;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.gravitex.accounting.AccountingManager;

public class HeaderPanel extends JPanel {

	private static final long serialVersionUID = -2909362031626033208L;
	
	private String headerText;

	public HeaderPanel(String aHeaderText) {
		super();
		this.headerText = aHeaderText;
		setLayout(new BorderLayout());
		JLabel headerLabel = new JLabel(headerText);
		headerLabel.setHorizontalAlignment(0);
		add(headerLabel, BorderLayout.NORTH);
		JComboBox<String> comboBox = new JComboBox<String>();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		Set<String> allCategories = AccountingManager.getInstance().getAllPartners();
		for (String partner : allCategories) {
			model.addElement(partner);
		}
		comboBox.setModel(model);
		// comboBox.setEditable(true);
		// comboBox.setEnabled(true);
		// add(comboBox, BorderLayout.SOUTH);
		// comboBox.addItemListener(this);
		add(comboBox, BorderLayout.SOUTH);
	}
}