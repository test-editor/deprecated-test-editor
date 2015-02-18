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
package org.testeditor.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.ui.adapter.TestProjectServiceAdapter;

/**
 * 
 * Mock Factory for Handler Tests.
 * 
 */
public final class HandlerMockFactory {

	/**
	 * Utility Class should not be instantiated.
	 */
	private HandlerMockFactory() {
	}

	/**
	 * 
	 * @return a TestProjectService Mock with one Project in the project list.
	 */
	public static TestProjectService getNonEmptyTestProjectService() {
		return new TestProjectServiceAdapter() {

			public List<TestProject> getProjects() {
				ArrayList<TestProject> list = new ArrayList<TestProject>();
				list.add(new TestProject());
				return list;
			}

		};
	}

	/**
	 * 
	 * @return a TestProjectService Mock with empty Project list.
	 */
	public static TestProjectService getEmptyTestProjectService() {
		return new TestProjectServiceAdapter() {
			public List<TestProject> getProjects() {
				return new ArrayList<TestProject>();
			}
		};
	}

}
