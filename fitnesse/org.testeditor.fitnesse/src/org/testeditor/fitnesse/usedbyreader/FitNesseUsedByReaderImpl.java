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
package org.testeditor.fitnesse.usedbyreader;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * reads the result of the restcall to the fitness-server usedWhere.
 * 
 * @author llipinski
 */
public class FitNesseUsedByReaderImpl implements FitNesseUsedByReader {

	@Override
	public ArrayList<String> readWhereUsedResult(String contentAsHtml, String nameOfProject) {

		ArrayList<String> testFlowList = new ArrayList<String>();

		Pattern pattern = Pattern.compile("a href=\"(" + nameOfProject + "[A-Z|a-z|0-9|.]*)", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(contentAsHtml);

		while (matcher.find()) {
			String group = matcher.group(1);
			if (!testFlowList.contains(group)) {
				testFlowList.add(group);
			}
		}

		return testFlowList;
	}
}
