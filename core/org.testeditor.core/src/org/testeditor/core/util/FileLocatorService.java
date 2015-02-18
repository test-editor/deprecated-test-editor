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

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Utility service to lookup files in the OSGi context.
 */
public class FileLocatorService {

	/**
	 * Returns if exists the absolute location of a bundle at the file system
	 * related to the given bundle name.
	 * 
	 * @param bundleName
	 *            bundle name (e.g. "org.testeditor.ui")
	 * @return string representation of the absolute path to the bundle
	 * @throws IOException
	 *             is thrown if file-operations failed
	 */
	public String findBundleFileLocationAsString(String bundleName) throws IOException {
		Bundle[] bundles = getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equals(bundleName)) {
				return FileLocator.getBundleFile(bundle).getAbsolutePath();
			}
		}
		return null;
	}

	/**
	 * Returns the absolute location of a bundle at the file system related to
	 * the given class.
	 * 
	 * @param class1
	 *            class that is member of the bundle to be looked up
	 * @return string representation of the absolute path to the bundle
	 * @throws IOException
	 *             is thrown if file-operations failed
	 */
	public String getBundleLocationFor(Class<?> class1) throws IOException {
		File bundleFile = FileLocator.getBundleFile(FrameworkUtil.getBundle(class1));
		return bundleFile.getAbsolutePath();
	}

	/**
	 * Returns the absolute location of a bundle at the file system related to
	 * the given class.
	 * 
	 * @param class1
	 *            class that is member of the bundle to be looked up
	 * @return string representation of the absolute path to the bundle
	 * @throws IOException
	 *             is thrown if file-operations failed
	 */
	public File getBundleFileFor(Class<?> class1) throws IOException {
		File bundleFile = FileLocator.getBundleFile(FrameworkUtil.getBundle(class1));
		return bundleFile;
	}

	/**
	 * Returns the bundle context. This implementation doesn't use any reference
	 * to the core activator.
	 * 
	 * @return the bundle context of this class
	 */
	BundleContext getBundleContext() {
		return FrameworkUtil.getBundle(FileLocatorService.class).getBundleContext();
	}

}
