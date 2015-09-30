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

import javax.annotation.PreDestroy;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.constants.CustomWidgetIdConstants;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTestLogAddErrorStyle;

/**
 * Scrollable info dialog for show text (e.g. a log file or the source code).
 */
public class InfoDialog extends TitleAreaDialog {

	private StyledText styledText;

	/**
	 * 
	 * @param parentShell
	 *            shell
	 */
	public InfoDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setImage(IconConstants.ICON_TESTEDITOR);
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		styledText = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		styledText.setEditable(false);
		styledText.setWordWrap(true);
		styledText.setData(CustomWidgetIdConstants.TEST_EDITOR_WIDGET_ID_SWT_BOT_KEY,
				CustomWidgetIdConstants.INFO_DIALOG_STYLED_TEXT);

		return parent;

	}

	@Override
	public void setMessage(String message) {
		styledText.setText(message);
		TestEditorTestLogAddErrorStyle testEditorTestLogAddErrorStyle = new TestEditorTestLogAddErrorStyle();
		styledText.setStyleRanges(testEditorTestLogAddErrorStyle.addErrorStyle(styledText.getText()));
		styledText.redraw();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}

	/**
	 * pre destroy, dispose all SWT-resources.
	 */
	@PreDestroy
	public void preDestroy() {
		if (styledText != null && !styledText.isDisposed()) {
			styledText.dispose();
		}
	}
}