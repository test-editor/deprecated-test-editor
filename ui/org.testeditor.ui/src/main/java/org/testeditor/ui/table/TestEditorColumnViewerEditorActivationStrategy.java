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
package org.testeditor.ui.table;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * special ColumnViewerEditorActivationStrategy.
 * 
 * @author llipinski
 * 
 */
public class TestEditorColumnViewerEditorActivationStrategy extends ColumnViewerEditorActivationStrategy {
	/**
	 * constructor.
	 * 
	 * @param viewer
	 *            ColumnViewer
	 */
	public TestEditorColumnViewerEditorActivationStrategy(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
		if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC) {
			return true;
		} else if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			KeyEvent evt = (KeyEvent) event.sourceEvent;
			if (!Character.isISOControl(event.character)) {
				return true;
			} else if (evt.keyCode == SWT.CR) {
				return true;
			}
		} else if (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			return true;
		}

		return false;
	}

}
