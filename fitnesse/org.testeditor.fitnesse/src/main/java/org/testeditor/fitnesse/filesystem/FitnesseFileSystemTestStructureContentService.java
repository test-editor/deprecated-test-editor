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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestComponent;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestStructureContentService;
import org.testeditor.fitnesse.util.FitNesseWikiParser;

/**
 * 
 * Fitnesse FileSystem Implemenation of the TestStructureContentService.
 *
 */
public class FitnesseFileSystemTestStructureContentService implements TestStructureContentService {

	private static final Logger LOGGER = Logger.getLogger(FitnesseFileSystemTestStructureContentService.class);

	@Override
	public void refreshTestCaseComponents(TestStructure testStructure) throws SystemException {
		String content = getTestStructureAsSourceText(testStructure);

		FitNesseWikiParser fitNesseWikiParser = createNewWikiParser();
		if (testStructure instanceof TestFlow) {
			TestFlow testFlow = (TestFlow) testStructure;
			LinkedList<TestComponent> testComponents = fitNesseWikiParser.parse(testFlow, content);
			testFlow.setTestComponents(testComponents);
		}
		if (testStructure instanceof TestSuite) {
			List<TestStructure> referredTestCases = fitNesseWikiParser.parseReferredTestCases(
					(TestSuite) testStructure, content);
			((TestSuite) testStructure).setReferredTestStructures(referredTestCases);
		}
	}

	@Override
	public void saveTestStructureData(TestStructure testStructure) throws SystemException {
		Path path = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure) + File.separator
				+ "content.txt");
		try {
			Files.write(path, testStructure.getSourceCode().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Unable to store code in filesystem", e);
			throw new SystemException("Unable to store code in filesystem", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reparseChangedTestFlow(TestFlow testFlow) throws SystemException {
		testFlow.setTestComponents(createNewWikiParser().parse(testFlow, testFlow.getSourceCode()));
	}

	@Override
	public List<TestComponent> parseFromString(TestFlow testFlow, String storedTestComponents) throws SystemException {
		return createNewWikiParser().parse(testFlow, storedTestComponents);
	}

	/**
	 * creates a wikiParser with the EclipseContextFactory.
	 * 
	 * @return a FitNesseWikiParser
	 */
	private FitNesseWikiParser createNewWikiParser() {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		FitNesseWikiParser fitNesseWikiParser = ContextInjectionFactory.make(FitNesseWikiParser.class, context);
		return fitNesseWikiParser;
	}

	@Override
	public String getTestStructureAsSourceText(TestStructure testStructure) throws SystemException {
		Path pathToTestStructure = Paths.get(FitnesseFileSystemUtility.getPathToTestStructureDirectory(testStructure));
		return FitnesseFileSystemUtility.getContentOfFitnesseFileForTestStructure(testStructure,
				pathToTestStructure.toString() + File.separator + "content.txt");
	}

}
