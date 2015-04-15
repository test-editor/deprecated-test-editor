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
package org.testeditor.ui.constants;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.testeditor.ui.handlers.OpenTestStructureHandler;

/**
 * 
 * creates all fonts for the TestEditorView and distribute them.
 * 
 */

public final class TestEditorFontConstants {

	public static final Font FONT_NORMAL = getFont(SWT.NORMAL);
	public static final Font FONT_UNDERLINE = getFont(SWT.NORMAL | SWT.UNDERLINE_SINGLE);
	public static final Font FONT_ITALIC = getFont(SWT.ITALIC);
	public static final Font FONT_UNDERLINE_ERROR = getFont(SWT.NORMAL | SWT.UNDERLINE_ERROR);
	public static final Font FONT_BOLD = getFont(SWT.BOLD);

	/**
	 * Don't create objects of this constants class.
	 */
	private TestEditorFontConstants() {
	}

	/**
	 * 
	 * @return the display.
	 */
	private static Display getDisplay() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * creates the font.
	 * 
	 * @param style
	 *            int as SWT_STYLE
	 * @return the font.
	 */
	private static Font getFont(int style) {
		try {
			if (getDisplay().getThread() == Thread.currentThread()) {
				String systemFontName = getDisplay().getSystemFont().getFontData()[0].getName();
				int systemFontHeight = getDisplay().getSystemFont().getFontData()[0].getHeight();
				return new Font(getDisplay(), systemFontName, systemFontHeight, style);
			}
		} catch (SWTException e) {
			Logger logger = Logger.getLogger(OpenTestStructureHandler.class);
			logger.error("Can't create shared font", e);
		}
		return null;
	}

	/**
	 * this method disposes all fonts. only the Activator-class should call this
	 * method at the end!
	 */
	public static void disposeFonts() {
		if (FONT_NORMAL != null) {
			FONT_NORMAL.dispose();
		}
		if (FONT_UNDERLINE != null) {
			FONT_UNDERLINE.dispose();
		}
		if (FONT_ITALIC != null) {
			FONT_ITALIC.dispose();
		}
		if (FONT_UNDERLINE_ERROR != null) {
			FONT_UNDERLINE_ERROR.dispose();
		}

	}

}
