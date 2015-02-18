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

import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * 
 * This Class is only for Tests. Production code should use e4 Dependency
 * Injection to consume services.
 * 
 */
public final class ServiceLookUpForTest {

	/**
	 * private Constructor. Clients should not instantiate this class.
	 */
	private ServiceLookUpForTest() {

	}

	/**
	 * Look Up Utility to find Services in the OSGi Context.
	 * 
	 * @param clazz
	 *            interface of the service.
	 * @param <S>
	 *            Service type.
	 * @return the service implementation.
	 */
	public static <S> S getService(Class<S> clazz) {
		ServiceReference<S> serviceReference = FrameworkUtil.getBundle(ServiceLookUpForTest.class).getBundleContext()
				.getServiceReference(clazz);
		return FrameworkUtil.getBundle(ServiceLookUpForTest.class).getBundleContext().getService(serviceReference);
	}

}
