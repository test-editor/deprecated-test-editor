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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * creates all colors for the TestEditorView and distribute them.
 * 
 */
public final class ColorConstants {

	public static final Color COLOR_BLUE = getColor(SWT.COLOR_BLUE);
	public static final Color COLOR_CYAN = getColor(SWT.COLOR_CYAN);
	public static final Color COLOR_GRAY = getColor(SWT.COLOR_GRAY);
	public static final Color COLOR_DARK_GRAY = getColor(SWT.COLOR_DARK_GRAY);
	public static final Color COLOR_DARK_GREEN = getColor(SWT.COLOR_DARK_GREEN);
	public static final Color COLOR_BACKROUND_NORMAL = getColor(SWT.COLOR_WHITE);
	public static final Color COLOR_SELECTED = getColor(225, 236, 255);
	public static final Color COLOR_WHITE = getColor(SWT.COLOR_WHITE);
	public static final Color COLOR_YELLOW = getColor(SWT.COLOR_YELLOW);
	public static final Color COLOR_RED = getColor(SWT.COLOR_RED);

	/**
	 * Don't create objects of this constants class.
	 */
	private ColorConstants() {
	}

	/**
	 * Constructs a new instance of this class given a device and the desired
	 * red, green and blue values expressed as ints in the range 0 to 255 (where
	 * 0 is black and 255 is full brightness). On limited color devices, the
	 * color instance created by this call may not have the same RGB values as
	 * the ones specified by the arguments. The RGB values on the returned
	 * instance will be the color values of the operating system color.
	 * <p>
	 * You must dispose the color when it is no longer required.
	 * </p>
	 * 
	 * @param red
	 *            the amount of red in the color
	 * @param green
	 *            the amount of green in the color
	 * @param blue
	 *            the amount of blue in the color
	 * @return the Color(display, red, green,blue)
	 * 
	 */
	private static Color getColor(final int red, final int green, final int blue) {
		Display display = Display.getCurrent();
		if (display != null) {
			return new Color(getDisplay(), red, green, blue);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param colorNo
	 *            as SWT.COLOR_..
	 * @return the Color
	 */
	private static Color getColor(final int colorNo) {
		Display display = Display.getCurrent();
		if (display != null) {
			return display.getSystemColor(colorNo);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return the display
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
	 * dispose all colors.
	 */
	public static void disposeColors() {
		dispose(COLOR_BLUE);
		dispose(COLOR_BLUE);
		dispose(COLOR_DARK_GREEN);
		dispose(COLOR_BACKROUND_NORMAL);
		dispose(COLOR_SELECTED);
		dispose(COLOR_WHITE);
		dispose(COLOR_YELLOW);
		dispose(COLOR_GRAY);
		dispose(COLOR_DARK_GRAY);
		dispose(COLOR_DARK_GREEN);
		dispose(COLOR_CYAN);
	}

	/**
	 * Checks if the color is not null and disposes it.
	 * 
	 * @param color
	 */
	private static void dispose(Color color) {
		if (color != null) {
			color.dispose();
		}
	}
}
