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
package org.testeditor.agent.swtbot;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.testing.TestableObject;
import org.osgi.framework.ServiceReference;

/**
 * 
 * Application of the Agent to wrap the AUT to create a Server for calls of the
 * Fixture.
 * 
 */
public class AgentApplication implements IApplication {

	private TestableObject testableObject;
	private TEAgentServer teAgentServer;
	private static final Logger LOGGER = Logger.getLogger(TEAgentServer.class);

	@Override
	public Object start(IApplicationContext context) throws Exception {
		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		IApplication app = getApplication(args);

		ServiceReference<TestableObject> serviceReference = Activator.getContext().getServiceReference(
				TestableObject.class);
		testableObject = Activator.getContext().getService(serviceReference);
		teAgentServer = new TEAgentServer(testableObject);
		testableObject.setTestHarness(teAgentServer);

		teAgentServer.start();
		try {
			return app.start(context);
		} catch (Exception e) {
			LOGGER.error("Erro executing AUT", e);
			return new Integer(13);
		}
	}

	/**
	 * This method scans the arguments for an application parameter. With the
	 * value of the parameter it looks up for an <code>IApplication</code> in
	 * the Plug-In Registry. If there is a Plug-In for that ApplicationId it
	 * will create an object and return it. If there is no Application it
	 * returns null.
	 * 
	 * @param args
	 *            of the RCP Launch.
	 * @return the Application specified in the arguments.
	 * @throws CoreException
	 *             accessing the plug-in registry
	 */
	private IApplication getApplication(String[] args) throws CoreException {
		String applicationToRun = getApplicationToRun(args);
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME,
				Platform.PT_APPLICATIONS, applicationToRun);
		Assert.isNotNull(extension, "Could not find IExtension for application: " + applicationToRun);
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length > 0) {
			IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
			if (runs.length > 0) {
				Object runnable = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
				if (runnable instanceof IApplication) {
					return (IApplication) runnable;
				}
			}
		}
		return null;
	}

	/**
	 * Extracts the application form the arguments.
	 * 
	 * @param args
	 *            of the RCP Launch.
	 * @return the Application specified in the arguments.
	 */
	protected String getApplicationToRun(String[] args) {
		IProduct product = Platform.getProduct();
		if (product != null) {
			return product.getApplication();
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-aut") && i < args.length - 1) { //$NON-NLS-1$
				return args[i + 1];
			}
		}
		return null;
	}

	@Override
	public void stop() {
		teAgentServer.interrupt();
	}

}
