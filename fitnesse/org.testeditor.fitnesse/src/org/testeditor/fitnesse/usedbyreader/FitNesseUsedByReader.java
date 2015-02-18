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

import java.util.List;

/**
 * 
 * interface to encapsulate the functionality of the WhereUsedReading with the
 * rest-client.
 * 
 * @author llipinski
 */
public interface FitNesseUsedByReader {
	/**
	 * This Method is a dirty hack for parsing the http response page of
	 * executed test. The Assertion line will be splitted and a TestResult
	 * object will be created.
	 * 
	 * @param contentAsHtml
	 *            the content as a Html-InputStream
	 * @param nameOfProject
	 *            String
	 * @return ArrayList<String>
	 */
	List<String> readWhereUsedResult(String contentAsHtml, String nameOfProject);

}
