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
package org.testeditor.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.log4j.Logger;

/**
 * Util class for file operations.
 *
 */
public final class FileUtils {

	/**
	 * 
	 */
	private FileUtils() {
		// do nothing
	}

	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

	/**
	 * copies the directories.
	 * 
	 * @param src
	 *            source-directory
	 * @param dest
	 *            destination-directory
	 * @throws IOException
	 *             IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			// if directory not exists, create it
			if (!dest.exists() && dest.mkdir()) {
				LOGGER.info("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String[] files = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}
		} else {
			Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			LOGGER.debug("File copied from " + src + " to " + dest);
		}
	}

	/**
	 * 
	 * @return FileVisitor to delete a Directory with all content recursive.
	 */
	public static FileVisitor<Path> getDeleteRecursiveVisitor() {
		return new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				if (Files.exists(dir, LinkOption.NOFOLLOW_LINKS)) {
					Files.delete(dir);
				}

				return FileVisitResult.CONTINUE;
			}

		};
	}

}
