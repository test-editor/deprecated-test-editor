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
package org.testeditor.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * test the language-bundle-files. the default and the specific should have the
 * same length and including the same keys.
 * 
 * @author llipinski
 * 
 */
public class TestBundleProperties {
	private Properties messagePropertiesDe = new Properties();
	private Properties messagePropertiesDefault = new Properties();

	/**
	 * before the class.
	 * 
	 * @throws IOException
	 *             on file-operation
	 */

	@Before
	public void beforeClass() throws IOException {
		Bundle uiBundle = getUIBundle();
		String messagesDe = FileLocator.toFileURL(uiBundle.getEntry("/OSGI-INF/l10n/bundle_de.properties")).getFile();
		String messagesDefault = FileLocator.toFileURL(uiBundle.getEntry("/OSGI-INF/l10n/bundle.properties")).getFile();
		putFileContentsToProperties(messagesDe, messagePropertiesDe);
		putFileContentsToProperties(messagesDefault, messagePropertiesDefault);

	}

	/**
	 * test the same length.
	 * 
	 */
	@Test
	public void testBundlePropertiesEqual() {

		assertEquals(messagePropertiesDefault.size(), messagePropertiesDe.size());

	}

	/**
	 * test all keys in the de-language-file in the default-file.
	 */

	@Test
	public void allKeysFromDeFileInDefaultFile() {
		assertTrue(writeDifferent(messagePropertiesDe, messagePropertiesDefault, "default"));

	}

	/**
	 * test all keys from the default-file in the de-language-file.
	 */

	@Test
	public void allKeysFromDefaultFileInTheDe() {
		assertTrue(writeDifferent(messagePropertiesDe, messagePropertiesDefault, "default"));

	}

	/**
	 * compares to language-property-files.
	 * 
	 * @param messagePropertiesKey
	 *            the properties form one language used for the keys in the test
	 * @param messagePropertiesValue
	 *            the properties from an other language used for the values
	 * @param language
	 *            as a string
	 * @return true, if all keys are in the value-properties included.
	 */
	private boolean writeDifferent(Properties messagePropertiesKey, Properties messagePropertiesValue, String language) {
		boolean allKeysInValueProperties = true;
		Enumeration<Object> keys = messagePropertiesKey.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			if (messagePropertiesValue.getProperty(key) == null) {
				allKeysInValueProperties = false;
			}
		}
		return allKeysInValueProperties;
	}

	/**
	 * puts the content of the file into the properties.
	 * 
	 * @param propertiesFile
	 *            the file
	 * @param properties
	 *            the properties
	 * @throws IOException
	 *             on file-operation
	 */
	private void putFileContentsToProperties(String propertiesFile, Properties properties) throws IOException {
		InputStreamReader inputStream = new InputStreamReader(new FileInputStream(propertiesFile), "UTF-8");
		properties.load(inputStream);
		inputStream.close();
	}

	/**
	 * Look up for UI Bundle.
	 * 
	 * @throws IOException
	 *             on file-operation
	 * @return the ui UI Bundle.
	 */
	private static Bundle getUIBundle() throws IOException {
		BundleContext context = FrameworkUtil.getBundle(TestBundleProperties.class).getBundleContext();
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals("org.testeditor.ui")) {
				return bundle;
			}
		}
		return null;
	}
}
