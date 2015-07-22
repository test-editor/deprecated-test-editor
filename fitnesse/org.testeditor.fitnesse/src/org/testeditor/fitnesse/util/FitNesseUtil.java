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
package org.testeditor.fitnesse.util;

import java.io.File;

/**
 * Utility class for special FitNesse string/file operations.
 *
 */
public final class FitNesseUtil {

	/**
	 * because of final util class.
	 */
	private FitNesseUtil() {
	}

	/**
	 * Converts given String represents full path on file system.
	 * 
	 * @param file
	 *            e.g c:/folder/folder2/file.txt
	 * @return file.txt
	 */
	public static String convertToFitNessePath(String file) {

		String pointSeparatedFile = file.replace(File.separatorChar, '.');

		String result = "";
		if (pointSeparatedFile.indexOf(".content.txt") > 0) {
			result = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".content.txt"));
		} else if (pointSeparatedFile.indexOf(".metadata.xml") > 0) {
			result = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".metadata.xml"));
		} else if (pointSeparatedFile.indexOf(".properties.xml") > 0) {
			result = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".properties.xml"));
		} else if (pointSeparatedFile.contains("FitNesseRoot")) {
			result = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.length());
		} else {
			String[] split = pointSeparatedFile.split("\\.");

			result = split[split.length - 2] + "." + split[split.length - 1];
		}

		return result;
	}

	/**
	 * 
	 * @param input
	 *            (input) delimited with points "a.b.cde"
	 * @param modified
	 *            (modified) delimited with points "a.b.cd"
	 * @return true if input is not part of modified
	 */
	public static boolean contains(String input, String modified) {

		String[] inputString = input.split("\\.");
		String[] modifiedString = modified.split("\\.");

		if (inputString.length > modifiedString.length) {
			return false;
		} else if (inputString.length <= modifiedString.length) {

			for (int i = 0; i < inputString.length; i++) {
				if (!inputString[i].equals(modifiedString[i])) {
					return false;
				}
			}

			return modified.contains(input);
		}

		return true;
	}
}
