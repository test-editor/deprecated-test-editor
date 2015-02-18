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
package org.testeditor.fitnesse.filesystem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * 
 * Tests for the FitnesseFileSystemTestStructureContentService.
 *
 */
public class FitnesseFileSystemTestStructureContentServiceTest extends FitnesseFileSystemAbstractTest {

	/**
	 * Test the loading of the text content of a teststructure.
	 * 
	 * @throws Exception
	 *             on loading tests.
	 */
	@Test
	public void testGetTestStructureAsText() throws Exception {
		FitnesseFileSystemTestStructureContentService service = new FitnesseFileSystemTestStructureContentService();
		TestProject testProject = createTestProjectsInWS();
		TestStructure structure = testProject.getTestChildByFullName("tp.tc");
		assertEquals(TEST_TEXT, service.getTestStructureAsSourceText(structure));
	}

}
