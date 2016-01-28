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
package org.testeditor.ui.handlers.teamshare;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Utility class to get Mock implementation for the TeamShare Handler.
 *
 */
public class TeamShareHandlerMockFactory {

	/**
	 * 
	 * @param testProject
	 *            used in this Mock to be returned in the selection.
	 * @return StructuredSelection with the testProject.
	 */
	TestExplorer getTestExplorerWith(final TestProject testProject) {
		return new TestExplorer(null) {

			@Override
			public IStructuredSelection getSelection() {
				return new StructuredSelection() {
					@Override
					public Object getFirstElement() {
						return testProject;
					}

					@Override
					public int size() {
						return 1;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}
				};
			}

		};
	}

}
