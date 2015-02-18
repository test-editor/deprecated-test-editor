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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Tests the FitNesse REST client.
 */
public class FitNesseRestClientTest {

	/**
	 * Test the construction of the fitnesse url based on a testprojectConfig.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testFitnesseURL() throws Exception {
		TestProject tp = new TestProject();
		TestProjectConfig cfg = new TestProjectConfig();
		cfg.setPort("8082");
		tp.setTestProjectConfig(cfg);
		TestStructure testStructure = new TestCase();
		tp.addChild(testStructure);
		assertEquals("http://localhost:8082/", FitNesseRestClient.getFitnesseUrl(testStructure));
		cfg.setPort("8088");
		assertEquals("http://localhost:8088/", FitNesseRestClient.getFitnesseUrl(testStructure));
	}
}
