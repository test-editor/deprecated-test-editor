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
/**
 * 
 */
package org.testeditor.teamshare.svn.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * Helper class for svn tests.
 */
public class SvnHelper {

	private static final Logger LOGGER = Logger.getLogger(SvnHelper.class);

	/**
	 * Creates a new test page.
	 * 
	 * @param destination
	 *            this is the absolute path of the parent testpage
	 * @param testPageName
	 *            the name of the new FitNesse testpage
	 * @return Directory of new created file
	 * @throws IOException
	 *             caused by a file interaction
	 */
	public static Path createNewTestPage(String destination, String testPageName) throws IOException {

		Path newTestPage = Paths.get(destination, testPageName);

		FileUtils.deleteDirectory(newTestPage.toFile());

		Path createDirectory = Files.createDirectory(newTestPage);

		Path contentTxt = Paths.get(createDirectory.toString(), "content.txt");
		Files.createFile(contentTxt);

		Path propertiesXml = Paths.get(createDirectory.toString(), "properties.xml");
		Files.createFile(propertiesXml);

		return createDirectory;

	}

	/**
	 * Updates file.
	 * 
	 * @param updateFile
	 *            File to update
	 * @param newData
	 *            String
	 * @throws IOException
	 *             if writing in file fails
	 */
	public static void updateFile(File updateFile, String newData) throws IOException {

		FileUtils.write(updateFile, newData, true);

	}

	/**
	 * Creates a new local repository for testing.
	 * 
	 * @param absolutPath
	 *            Example: d:\\myrep
	 */
	public static void createLocalRepository(String absolutPath) {
		SVNRepositoryFactoryImpl.setup();
		try {
			SVNRepositoryFactory.createLocalRepository(new File(absolutPath), true, false);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static boolean existsTestCase(String destination, String testPageName) {
		Path testPage = Paths.get(destination, testPageName);
		return testPage.toFile().exists();
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *            for main
	 */
	public static void main(String[] args) {
		createLocalRepository("d:\\rep");
	}
}
