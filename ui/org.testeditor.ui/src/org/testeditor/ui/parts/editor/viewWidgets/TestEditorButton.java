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
package org.testeditor.ui.parts.editor.viewWidgets;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.ui.constants.IconConstants;

/**
 * special button to add a tooltip and a mousemovelistener.
 * 
 * @author llipinski
 * 
 */
public class TestEditorButton {

	private Button wrappedButton;

	/**
	 * constructor.
	 * 
	 * @param parent
	 *            Composite
	 * @param style
	 *            SWT.Style
	 * @param image
	 *            Image for the button
	 * @param tooltip
	 *            String tooltip on the button
	 */
	public TestEditorButton(final Composite parent, final int style, final Image image, final String tooltip) {
		wrappedButton = new Button(parent, style);
		wrappedButton.setImage(image);
		wrappedButton.setToolTipText(tooltip);
		wrappedButton.setCursor(IconConstants.CURSOR_ARROW);
		wrappedButton.setVisible(false);
	}

	/**
	 * 
	 * @return the wrapped button
	 */
	public Button getButton() {
		return wrappedButton;
	}
}
