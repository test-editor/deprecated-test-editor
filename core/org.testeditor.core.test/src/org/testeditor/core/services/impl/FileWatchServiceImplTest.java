/*******************************************************************************
 * Copyright (c) 2012 - 2014 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.core.services.impl;

import org.junit.Ignore;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.FileWatchService;

/**
 * 
 *
 */
public class FileWatchServiceImplTest {

	@Ignore("test is in work")
	public void testFileWatchService() {

		FileWatchService fileWatchService = new FileWatchServiceImpl();

		TestProject testProject1 = new TestProject();
		testProject1.setName("testproject1");
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath("c:\\transfer\\p1\\");
		testProject1.setTestProjectConfig(testProjectConfig);
		fileWatchService.watch(testProject1);

		testProject1 = new TestProject();
		testProject1.setName("testproject2");
		testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath("c:\\transfer\\p2\\");
		testProject1.setTestProjectConfig(testProjectConfig);
		fileWatchService.watch(testProject1);

		System.out.println();

	}
}
