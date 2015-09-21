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
package org.testeditor.ui.uiscanner.ui.table;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * 
 * @author dkuhlmann
 *
 */
public class UiScannerTableSelectionChangedListener implements ISelectionChangedListener {

	private WebScanner webScanner;
	private UiScannerWebElement lastElem = null;
	private String lastValue = "5px solid transparent";

	/**
	 * Constructor.
	 * 
	 * @param webScanner
	 *            WebScanner for highlighting the WebElements.
	 */
	public UiScannerTableSelectionChangedListener(WebScanner webScanner) {
		super();
		this.webScanner = webScanner;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (lastElem != null) {
			webScanner.setWebElemntOutlineValue(lastElem.getTechnicalID(), lastValue);
		}
		if (event.getSource() instanceof GridTableViewer) {
			GridTableViewer tableViewer = (GridTableViewer) event.getSource();
			if (tableViewer.getGrid().getSelection().length > 0
					&& tableViewer.getGrid().getSelection()[0].getData() instanceof UiScannerWebElement) {
				UiScannerWebElement elem = (UiScannerWebElement) tableViewer.getGrid().getSelection()[0].getData();
				webScanner.setWebElemntOutline(elem.getTechnicalID(), 5, "solid", "red");
				lastElem = elem;
			}
		}

	}

}
