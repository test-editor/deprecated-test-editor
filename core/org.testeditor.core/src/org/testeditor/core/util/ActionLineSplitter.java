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
package org.testeditor.core.util;

import java.util.ArrayList;

/**
 * Splits action lines (e.g. |gebe in das Feld|Name|den Wert|xyz|ein) into the
 * internal object structure.
 */
public final class ActionLineSplitter {

	/**
	 * Returns the value in the input line at a given position.
	 * 
	 * @param line
	 *            inputline
	 * @param delimiter
	 *            the delimiter
	 * @param valuePos
	 *            position
	 * @return the value
	 */
	public static String getValueAtPos(String line, String delimiter, int valuePos) {
		String maskedDelimiter = "\\" + delimiter;
		String[] lineParts = line.split(maskedDelimiter);
		if (valuePos <= lineParts.length - 1) {
			return lineParts[valuePos];
		}
		return "";
	}

	/**
	 * Remove the rest before the given patternNo.
	 * 
	 * @param line
	 *            inputLine
	 * @param delimiter
	 *            the delimiter
	 * @param patternNo
	 *            number of the pattern
	 * @return beginning of the inputline until the patternNo
	 */
	public static String splitLineBeforePatternNo(String line, String delimiter, int patternNo) {
		String lineBegin = line;
		String maskedDelimiter = "\\" + delimiter;
		String[] lineParts = line.split(maskedDelimiter);
		ArrayList<String> linePartsList = createArrayList(lineParts);
		if (line.endsWith("||")) {
			linePartsList.add("");
		}
		if (patternNo <= linePartsList.size() - 1) {
			// if (lineParts[patternNo].length() > 0) {
			int pos = 1;
			for (int j = 0; j < patternNo; j++) {
				pos = lineParts[j].length() + pos + 1;
			}
			lineBegin = line.substring(0, pos - 2) + delimiter;
			// }
			return lineBegin;
		}
		return "";
	}

	/**
	 * creates an ArrayList<String> out of an Array<String>.
	 * 
	 * @param lineParts
	 *            String[]
	 * @return the lineParts an ArrayList<String>
	 */
	private static ArrayList<String> createArrayList(String[] lineParts) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String part : lineParts) {
			arrayList.add(part);
		}
		return arrayList;

	}

	/**
	 * Splits a line in patterns, removes the part with the patternNo and
	 * returns the rest.
	 * 
	 * @param line
	 *            inputLine
	 * @param delimiter
	 *            the delimiter
	 * @param patternNo
	 *            number of the pattern begins with 0
	 * @return the line without the numbered pattern
	 */
	public static String removePatternNofromLine(String line, String delimiter, int patternNo) {
		String result = splitLineBeforePatternNo(line, delimiter, patternNo);
		String maskedDelimiter = "\\" + delimiter;
		String[] lineParts = line.split(maskedDelimiter);
		patternNo = patternNo + 1;
		while (lineParts.length > patternNo) {
			result = result + lineParts[patternNo] + delimiter;
			patternNo++;
		}
		return result;
	}

	/**
	 * Don't create objects of this utility class.
	 */
	private ActionLineSplitter() {
	}
}
