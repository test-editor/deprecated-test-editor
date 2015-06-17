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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Tests the creation of JAXB sources via the console invoke "xjc". Sources
 * created by this test are located at the "target/generated-sources" folder of
 * this test bundle. After the test succeeded it is possible to copy these
 * sources to the "srcGenerated" folder of the scanner bundle if needed.
 */
public class JaxbSourceCreatorIntTest {

	private static final Logger LOGGER = Logger.getLogger(JaxbSourceCreatorIntTest.class);

	private static final String PATH_ACTION_GROUPS_XSD = JaxbTestHelper.getBundleLocation() + File.separatorChar
			+ "resources" + File.separatorChar + "AllActionGroups.xsd";
	private static final String PATH_TECHNICAL_BINDINGS_XSD = JaxbTestHelper.getBundleLocation() + File.separatorChar
			+ "resources" + File.separatorChar + "TechnicalBindingTypeCollection.xsd";
	private static final String PATH_GENERATED_SOURCE_OUTPUT = getPathToTestBundle() + File.separatorChar + "target"
			+ File.separatorChar + "generated-sources";

	/**
	 * Creates the JAXB objects for the action groups.
	 * 
	 * @throws Exception
	 *             if IO or Interrupt exceptions occurred
	 */
	@Ignore
	@Test
	public void testCreateActionGroups() throws Exception {
		String appCommand = "xjc -d " + PATH_GENERATED_SOURCE_OUTPUT + " -p org.testeditor.xmllibrary.domain.action "
				+ PATH_ACTION_GROUPS_XSD;
		Process p = Runtime.getRuntime().exec(appCommand);
		p.waitFor();
		assertTrue("invalid exit value: " + p.exitValue(), p.exitValue() == 0);
	}

	/**
	 * Look up for test Bundle Path.
	 * 
	 * @return path to test bundle
	 */
	private static String getPathToTestBundle() {
		BundleContext context = FrameworkUtil.getBundle(JaxbSourceCreatorIntTest.class).getBundleContext();
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals("org.testeditor.xmllibrary.test")) {
				try {
					return FileLocator.getBundleFile(bundle).getAbsolutePath();
				} catch (IOException e) {
					LOGGER.error("Look up test bundle failed", e);
				}
			}
		}
		return "";
	}

	/**
	 * Creates the JAXB objects for the technical bindings.
	 * 
	 * @throws Exception
	 *             if IO or Interrupt exceptions occurred
	 */
	@Ignore
	@Test
	public void testCreateTechnicalBindings() throws Exception {
		String appCommand = "xjc -d " + PATH_GENERATED_SOURCE_OUTPUT + " -p org.testeditor.xmllibrary.domain.binding "
				+ PATH_TECHNICAL_BINDINGS_XSD;

		Process p = Runtime.getRuntime().exec(appCommand);
		p.waitFor();
		assertTrue("invalid exit value: " + p.exitValue(), p.exitValue() == 0);
	}

}
