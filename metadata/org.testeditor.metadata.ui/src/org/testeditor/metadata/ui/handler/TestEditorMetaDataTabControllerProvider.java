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
package org.testeditor.metadata.ui.handler;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.parts.editor.ITestEditorTabController;
import org.testeditor.ui.parts.editor.ITestEditorTabContollerProvider;

public class TestEditorMetaDataTabControllerProvider implements ITestEditorTabContollerProvider {

	private IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(
			TestEditorMetaDataTabController.class).getBundleContext());

	@Override
	public ITestEditorTabController get() {
		return ContextInjectionFactory.make(TestEditorMetaDataTabController.class, context);
	}

}
