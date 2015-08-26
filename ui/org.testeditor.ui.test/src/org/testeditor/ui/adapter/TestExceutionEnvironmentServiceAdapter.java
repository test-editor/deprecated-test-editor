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
package org.testeditor.ui.adapter;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestExceutionEnvironmentService;

/**
 * Adapter to build mocks of TestExceutionEnvironmentService.
 *
 */
public class TestExceutionEnvironmentServiceAdapter implements TestExceutionEnvironmentService {

	@Override
	public void setUpEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {
	}

	@Override
	public TestResult executeTests(TestStructure testStructure, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		return null;
	}

	@Override
	public void shutDownEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {

	}

	@Override
	public void resetEnvironment(TestProject testProject, IProgressMonitor monitor)
			throws IOException, InterruptedException {

	}

	@Override
	public void tearDownAllEnvironments() throws IOException, InterruptedException {
	}

	@Override
	public boolean isTestEnvironmentLaunchedFor(TestProject testProject) {
		return false;
	}

}
