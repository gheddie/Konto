/*
 * Created by JFormDesigner on Fri Apr 24 09:44:33 CEST 2020
 */

package de.gravitex.accounting.gui.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import de.gravitex.accounting.AccountingRow;
import de.gravitex.accounting.gui.component.table.*;

/**
 * @author Stefan Schulz
 */
public class SubRowDialog extends JDialog {
	
	private List<AccountingRow> accountingRows;
	
	private String subAccountName;
	
	public SubRowDialog(Window owner) {
		super(owner);
		initComponents();
		setSize(800, 600);
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Stefan Schulz
		subRowsTable = new FilterTable();
		btnClose = new JButton();

		//======== this ========
		var contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {398, 104, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
		contentPane.add(subRowsTable, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- btnClose ----
		btnClose.setText("OK");
		contentPane.add(btnClose, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Stefan Schulz
	private FilterTable subRowsTable;
	private JButton btnClose;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		setTitle("Gegenkonto ("+subAccountName+")");
		subRowsTable.setData(accountingRows);
	}
	
	public SubRowDialog withRows(List<AccountingRow> accountingRows) {
		this.accountingRows = accountingRows;
		return this;
	}

	public SubRowDialog withSubAccount(String aSubAccountName) {
		this.subAccountName = aSubAccountName;
		return this;
	}
}
