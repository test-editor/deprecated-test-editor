/*******************************************************************************
 * Copyright (c) 2012 - 2015 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TeamShareAuthentificationDialog extends TitleAreaDialog {

	Text nameText;

	/**
	 * 
	 * @param parentShell
	 *            shell
	 */
	public TeamShareAuthentificationDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(200, 250);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));

		Composite gridCompo = new Composite(composite, SWT.BORDER);
		gridCompo.setLayoutData(layoutData);
		gridCompo.setLayout(new GridLayout(2, false));

		GridData labelLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		GridData fieldLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		Label name = new Label(gridCompo, SWT.NONE);
		name.setText("Name");
		name.setLayoutData(labelLayoutData);
		nameText = new Text(gridCompo, SWT.NONE);
		nameText.setLayoutData(fieldLayoutData);

		Label password = new Label(gridCompo, SWT.NONE);
		password.setText("pw");
		password.setLayoutData(labelLayoutData);
		Text passwordText = new Text(gridCompo, SWT.NONE);
		passwordText.setLayoutData(fieldLayoutData);
		return parent;

	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancle", true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		System.err.println(buttonId);
		super.buttonPressed(buttonId);

	}

}
