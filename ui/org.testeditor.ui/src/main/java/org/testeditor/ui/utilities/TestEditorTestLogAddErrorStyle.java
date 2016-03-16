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
package org.testeditor.ui.utilities;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.swt.custom.StyleRange;
import org.testeditor.ui.constants.ColorConstants;

/**
 * this class adds an error-style to the styledText in the testLog-dialog, if
 * there is an error-message.
 * 
 * @author llipinski
 * 
 */
public class TestEditorTestLogAddErrorStyle {

	/**
	 * adds an error-style if the pattern "] ERROR (" is in the line.
	 * 
	 * @param messageText
	 *            the messageText
	 * @return StyleRange[]
	 */
	public StyleRange[] addErrorStyle(String messageText) {
		StyleRange[] glStyleRanges = new StyleRange[0];
		StyleRange lastStyle = new StyleRange();
		lastStyle.start = 0;
		int actOffset = 0;
		lastStyle.length = 0;
		glStyleRanges = addNewStyleRange(lastStyle, glStyleRanges);

		if (containsErrorString(messageText)) {

			StringTokenizer stringTokenizer = new StringTokenizer(messageText, "\n");
			while (stringTokenizer.hasMoreTokens()) {
				String nextToken = stringTokenizer.nextToken();
				if (containsErrorString(nextToken)) {
					// add an error-style
					StyleRange actStyle = new StyleRange();
					actStyle.background = ColorConstants.COLOR_LIGHT_RED;
					actStyle.start = actOffset;
					actStyle.length = nextToken.length() + 1;
					glStyleRanges = addNewStyleRange(actStyle, glStyleRanges);
					// add a normal style
					if (stringTokenizer.hasMoreTokens()) {
						lastStyle = new StyleRange();
						lastStyle.start = actOffset + nextToken.length() + 1;
						glStyleRanges = addNewStyleRange(lastStyle, glStyleRanges);
					}
				} else {
					lastStyle.length = lastStyle.length + nextToken.length();
				}
				actOffset = actOffset + nextToken.length() + 1;
			}
		}

		return glStyleRanges;
	}

	/**
	 * 
	 * @param messageText
	 *            the checked string
	 * @return true, if the messageText contains an error string
	 */
	private static boolean containsErrorString(String messageText) {

		Pattern compile = Pattern.compile(".*ERROR.*", Pattern.DOTALL);
		boolean matches = compile.matcher(messageText).matches();

		return matches;
	}

	/**
	 * this method adds a range to the styledRanges.
	 * 
	 * @param range2
	 *            StyleRange
	 * @param glStyleRanges
	 *            StyleRange[]
	 * @return StyleRange[]
	 */
	private StyleRange[] addNewStyleRange(StyleRange range2, StyleRange[] glStyleRanges) {
		StyleRange[] ranges = new StyleRange[glStyleRanges.length + 1];
		for (int i = 0; i < glStyleRanges.length; i++) {
			ranges[i] = glStyleRanges[i];
		}
		ranges[ranges.length - 1] = range2;

		return ranges;
	}
}
