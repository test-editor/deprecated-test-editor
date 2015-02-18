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
package org.testeditor.ui.uiscanner.ui.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.ui.uiscanner.ui.UiScannerTranslationService;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * 
 * @author dkuhlmann
 *
 */
public class ScanHandler {

	@Inject
	private UiScannerTranslationService translate;

	/**
	 * 
	 * @param shell
	 *            Shell
	 * @param webScanner
	 *            WebScanner
	 * @param filters
	 *            ArrayList<String>
	 * @param xPath
	 *            String
	 * @param webElements
	 *            ArrayList<UiScannerWebElement>
	 * @throws InvocationTargetException
	 *             InvocationTargetException
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, final WebScanner webScanner,
			final ArrayList<String> filters, final String xPath, final ArrayList<UiScannerWebElement> webElements)
			throws InvocationTargetException, InterruptedException {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		dialog.open();
		dialog.run(true, true, new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask(translate.translate("%PROGRESS_SCAN_WEBSITE"), filters.size());
				for (UiScannerWebElement elem : webScanner.scanWebsite(filters, xPath, monitor)) {
					webElements.add(elem);
				}
			}
		});
	}
}
