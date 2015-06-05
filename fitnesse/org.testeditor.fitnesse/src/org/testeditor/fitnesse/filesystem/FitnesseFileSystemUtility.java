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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Utlitity Class for common File Operations.
 * 
 */
public final class FitnesseFileSystemUtility {

	private static final Logger LOGGER = Logger
			.getLogger(FitnesseFileSystemUtility.class);

	/**
	 * Utility Class can not be instantiated.
	 */
	private FitnesseFileSystemUtility() {

	}

	/**
	 * Creates the Path to the Directory of the TestStructure in the FileSystem
	 * as a string.
	 * 
	 * @param testStructure
	 *            to be used for lookup.
	 * @return the path as string to the TestStructure.
	 */
	public static String getPathToTestStructureDirectory(
			TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append(getPathToProject(testStructure));
		String pathInProject = testStructure.getFullName().replace('.',
				File.separator.toCharArray()[0]);
		sb.append(File.separator).append("FitNesseRoot").append(File.separator)
				.append(pathInProject);
		return sb.toString();
	}

	/**
	 * Creates the ErrorLog Directory Path of the TestStructure in the
	 * FileSystem as a string.
	 * 
	 * @param testStructure
	 *            to be used for lookup.
	 * @return the path as string to the TestStructure.
	 */
	public static String getPathToTestStructureErrorDirectory(
			TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append(getPathToProject(testStructure));
		String pathInProject = "ErrorLogs"
				+ File.separator
				+ testStructure.getFullName().replace('.',
						File.separator.toCharArray()[0]);
		sb.append(File.separator).append("FitNesseRoot").append(File.separator)
				.append(pathInProject);
		return sb.toString();
	}

	/**
	 * Checks if for given testStructure if an content.txt exixts in error log
	 * path of fitnesse.
	 * 
	 * @param testStructure
	 *            to be used for lookup.
	 * @return the path as string to the TestStructure.
	 */
	public static boolean existsContentTxtInPathOfTestStructureInErrorDirectory(
			TestStructure testStructure) {
		String pathToTestStructureErrorDirectory = getPathToTestStructureErrorDirectory(testStructure);
		return new File(pathToTestStructureErrorDirectory + File.separatorChar
				+ "content.txt").exists();
	}

	/**
	 * 
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return Returns true if path of structure exists.
	 */
	public static boolean existsPathToTestStructureDirectory(
			TestStructure testStructure) {
		String pathToTestStructureDirectory = getPathToTestStructureDirectory(testStructure);
		return new File(pathToTestStructureDirectory).exists();
	}

	/**
	 * 
	 * @param testStructure
	 *            used to get the TestProcject and looks it's location in the
	 *            filesystem.
	 * @return path as string of the root element of the given teststructure.
	 */
	public static String getPathToProject(TestStructure testStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append(Platform.getLocation().toFile().toPath().toString())
				.append(File.separator)
				.append(testStructure.getRootElement().getName());
		return sb.toString();
	}

	/**
	 * 
	 * @param fileAsString
	 *            the fitnesse file
	 * @return Returns true if absolute path contains the component node.
	 *         Component node can be
	 *         TestEditorGlobalConstans.TEST_SCENARIO_SUITE or
	 *         TestEditorGlobalConstans.TEST_KOMPONENTS
	 */
	public static boolean isComponentNode(String fileAsString) {

		return fileAsString
				.contains(TestEditorGlobalConstans.TEST_SCENARIO_SUITE)
				|| fileAsString
						.contains(TestEditorGlobalConstans.TEST_KOMPONENTS);
	}

	/**
	 * 
	 * @return directory filter to ignore directory with . as prefix.
	 */
	public static FilenameFilter getDirectoryFilter() {
		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(".")) {
					return false;
				}
				return new File(dir.getAbsoluteFile() + File.separator + name)
						.isDirectory();
			}
		};
	}

	/**
	 * 
	 * @return Filename filter to get only properties.xml.
	 */
	public static FilenameFilter getPropertyFiler() {
		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals("properties.xml");
			}
		};
	}

	/**
	 * Reads the content of a textfile and returns it as a string. The lines are
	 * seperated by \n. THe encoding of the file is expected as UTF-8.
	 * 
	 * @param testStructure
	 *            used to report on errors.
	 * @param pathToFitnesseFile
	 *            to the content File relatet to the teststructure. This can be
	 *            the sourcecode of a teststructure or the execution log.
	 * @return content of the file with \n line seperator.
	 * @throws SystemException
	 *             on io problems.
	 */
	public static String getContentOfFitnesseFileForTestStructure(
			TestStructure testStructure, String pathToFitnesseFile)
			throws SystemException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(pathToFitnesseFile), "UTF8"))) {
			while (br.ready()) {
				sb.append(br.readLine()).append("\n");
			}
		} catch (IOException e) {
			LOGGER.error("Error reading content of teststructrue: "
					+ testStructure, e);
			throw new SystemException(
					"Error reading content of teststructrue: " + testStructure
							+ "\n" + e.getMessage(), e);
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @return FileVisitor to delete a Directory with all content recursive.
	 */
	public static FileVisitor<Path> getDeleteRecursiveVisitor() {
		return new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {

				if (Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
					Files.delete(dir);
				}

				return FileVisitResult.CONTINUE;
			}

		};
	}

}
