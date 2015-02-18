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
package org.testeditor.xmllibrary.utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.testeditor.xmllibrary.activator.XMLLibraryActivator;

/**
 * 
 * Helper Class to access the Bundle Location in a static way for tests.
 * 
 */
public final class JaxbTestHelper {
	
	private static final Logger LOGGER = Logger.getLogger(JaxbTestHelper.class);
	
	/**
	 * no supposed to build an instance.
	 */
	private JaxbTestHelper() {
	}
	
	/**
	 * 
	 * @return the Bundlel ocation as string
	 */
	public static String getBundleLocation() {
	String path = null;
	try {
		File bundleFile = FileLocator.getBundleFile(XMLLibraryActivator.getContext().getBundle());
		path = bundleFile.getAbsolutePath();
	} catch (IOException e) {
		LOGGER.error("Error get bundleLocation:: FAILED", e);
	}
	return path;
}

}
