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

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.testeditor.ui.uiscanner.webscanner.UiScannerWebElement;
import org.testeditor.ui.uiscanner.webscanner.WebScanner;

/**
 * 
 * @author dkuhlmann
 *
 */
public class UiScannerTableMouseMove implements MouseMoveListener {

	private UiScannerWebElement lastHovered = null;
	private WebScanner webScanner = null;
	private GridTableViewer tableViewer = null;

	/**
	 * creator.
	 * 
	 * @param webScanner
	 *            WebScanner
	 * @param tableViewer
	 *            GridTableViewer
	 */
	public UiScannerTableMouseMove(WebScanner webScanner, GridTableViewer tableViewer) {
		super();
		this.webScanner = webScanner;
		this.tableViewer = tableViewer;
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (webScanner != null && tableViewer != null && webScanner.isWebDriverAktive()
				&& tableViewer.getGrid().getItemCount() > 0) {
			GridItem gridItem = tableViewer.getGrid().getItem(new Point(e.x, e.y));
			if (gridItem != null) {
				Object elem = tableViewer.getGrid().getItem(new Point(e.x, e.y)).getData();
				if (!elem.equals(lastHovered) && elem instanceof UiScannerWebElement) {
					if (lastHovered != null) {
						webScanner.setWebElemntOutline(lastHovered.getTechnicalID(), 5, "solid", "transparent");
						webScanner
								.setWebElemntOutline(((UiScannerWebElement) elem).getTechnicalID(), 5, "solid", "red");
						lastHovered = (UiScannerWebElement) elem;
					} else {
						webScanner
								.setWebElemntOutline(((UiScannerWebElement) elem).getTechnicalID(), 5, "solid", "red");
						lastHovered = (UiScannerWebElement) elem;
					}

				}
			} else {
				if (lastHovered != null) {
					webScanner.setWebElemntOutline(lastHovered.getTechnicalID(), 5, "solid", "transparent");
					lastHovered = null;
				}
			}
		}
	}
}
