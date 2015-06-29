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
package org.testeditor.core.services.interfaces;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Provides read, write, delete and execution services regarding the content of
 * any test structure. A test structure could be a simple test, a scenario, a
 * test suite or a project (this data is used in the left view - the Test
 * explorer - of the Test-Editor).
 * 
 * This interface is intended to be used by clients and not to be implemented by
 * plug-ins. Plug-In developer should implement the interface:
 * TestStructureServicePlugIn.
 * 
 */
public interface TestStructureService {

	/**
	 * Loads the children of the given test structure and adds them to it.
	 * 
	 * @param testCompositeStructure
	 *            composite structure
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void loadChildrenInto(TestCompositeStructure testCompositeStructure) throws SystemException;

	/**
	 * Creates the given test structure in the backend system.
	 * 
	 * @param testStructure
	 *            test structure which should be created
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void create(TestStructure testStructure) throws SystemException;

	/**
	 * Removes the test structure.
	 * 
	 * @param testStructure
	 *            test structure which should be deleted
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void delete(TestStructure testStructure) throws SystemException;

	/**
	 * Renames a given test structure.
	 * 
	 * @param testStructure
	 *            test structure which should be renamed (contains the
	 *            old/current name)
	 * @param newName
	 *            new name
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void rename(TestStructure testStructure, String newName) throws SystemException;

	/**
	 * Executes a given test structure.
	 * 
	 * @param testStructure
	 *            test structure which should be executed
	 * @param monitor
	 *            monitor for cancel running test
	 * @return TestResult
	 * @throws SystemException
	 *             is thrown if a system exception occurred (e.g. third party
	 *             system unavailable
	 * @throws InterruptedException
	 *             test was interrupted
	 */
	TestResult executeTestStructure(TestStructure testStructure, IProgressMonitor monitor) throws SystemException,
			InterruptedException;

	/**
	 * Returns the latest test execution log.
	 * 
	 * @param testStructure
	 *            text of structure which should be shown
	 * @return String
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	String getTestExecutionLog(TestStructure testStructure) throws SystemException;

	/**
	 * tests, if a log of a test execution for this structure exists.
	 * 
	 * @param testStructure
	 *            structure which should be checked
	 * @return true, if there is any log data to, false otherwise
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	boolean hasTestExecutionLog(TestStructure testStructure) throws SystemException;

	/**
	 * Gets the history of the test-results sorted by ResultDate.
	 * 
	 * @param testStructure
	 *            {@link TestStructure}
	 * @return List<TestResult>
	 * @throws SystemException
	 *             on error
	 */
	List<TestResult> getTestHistory(TestStructure testStructure) throws SystemException;

	/**
	 * Removes the testHistory of a test structure.
	 * 
	 * @param testStructure
	 *            test structure which should be deleted
	 * @throws SystemException
	 *             is thrown if a system exception occurred
	 */
	void clearTestHistory(TestStructure testStructure) throws SystemException;

	/**
	 * Checks if the parameter is a reserved name in the context of the
	 * TestServer.
	 * 
	 * @param testProtect
	 *            used to determine the correct teststructure plugin.
	 * @param name
	 *            to be checked.
	 * @return true if the name is reserved.
	 */
	boolean isReservedName(TestProject testProtect, String name);

	/**
	 * Creates a Runnable which can used for lazy loading the children in the
	 * model class.
	 * 
	 * @param toBeLoadedLazy
	 *            TestCompositeStructure to be lazy loaded
	 * @return runnable to execute the lazy loading.
	 */
	Runnable getTestProjectLazyLoader(TestCompositeStructure toBeLoadedLazy);

}
