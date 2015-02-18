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
package org.testeditor.ui.uiscanner.ui;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * UiScannerHandler.
 * 
 * @author dkuhlmann
 * 
 */
public class UiscannerHandler {

	/**
	 * Starts the UiScanner in a new Window.
	 * 
	 * @param shell
	 *            Shell for the new Dialog
	 * @param context
	 *            IEclipseContext
	 */
	@Execute
	public void execute(Shell shell, final IEclipseContext context) {
		Dialog dialog = new Dialog(shell) {
			private UIScannerViewer viewer;

			@Override
			protected org.eclipse.swt.widgets.Control createDialogArea(Composite parent) {
				Composite area = (Composite) super.createDialogArea(parent);
				context.set(Composite.class, area);
				UiScannerTranslationService trans = ContextInjectionFactory.make(UiScannerTranslationService.class,
						context);
				context.set(UiScannerTranslationService.class, trans);
				viewer = ContextInjectionFactory.make(UIScannerViewer.class, context);
				return area;
			}

			@Override
			protected Point getInitialSize() {
				return new Point(1000, 750);
			}

			@Override
			protected void createButtonsForButtonBar(Composite parent) {
				createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
			}

			@Override
			public boolean close() {
				viewer.closeBrowser();
				return super.close();
			}

			@Override
			protected boolean isResizable() {
				return true;
			}

			@Override
			protected void setShellStyle(int newShellStyle) {
				super.setShellStyle(SWT.MIN | SWT.MAX | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
				setBlockOnOpen(false);
			}

		};
		dialog.open();
	}
}