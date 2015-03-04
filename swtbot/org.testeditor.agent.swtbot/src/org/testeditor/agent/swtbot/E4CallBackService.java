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

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;

/**
 * 
 * Callback Service to get the Application Context.
 *
 */
public class E4CallBackService implements IContextFunction {

	private IEclipseContext context;

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		this.context = context;
		return this;
	}

	/**
	 * 
	 * @return the Eclipse Context of the AUT.
	 */
	public IEclipseContext getContext() {
		return context;
	}

}
