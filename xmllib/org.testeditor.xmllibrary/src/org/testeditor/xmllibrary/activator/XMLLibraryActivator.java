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
package org.testeditor.xmllibrary.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for the bundle.
 */
// TODO Remove this class.
public class XMLLibraryActivator implements BundleActivator {

	private static BundleContext context;

	/**
	 * Returns the context.
	 * 
	 * @return context
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		XMLLibraryActivator.setContext(bundleContext);
	}

	/**
	 * 
	 * @param bundleContext
	 *            of the bundle
	 */
	protected static void setContext(BundleContext bundleContext) {
		context = bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		XMLLibraryActivator.setContext(null);
	}

}
