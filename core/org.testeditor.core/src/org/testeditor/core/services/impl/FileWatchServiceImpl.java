/*******************************************************************************
 * Copyright (c) 2012 - 2014 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 *******************************************************************************/
package org.testeditor.core.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.FileWatchService;

public class FileWatchServiceImpl implements FileWatchService, IContextFunction {

	Map<TestProject, FileWatcher> pool = new HashMap<TestProject, FileWatcher>();
	private IEclipseContext context;

	@Override
	public void watch(TestProject testProject) {

		if (!pool.containsKey(testProject)) {
		
			context.set(TestProject.class, testProject);
			FileWatcher fileWatcher = ContextInjectionFactory.make(FileWatcher.class, context);
			
			
			//FileWatcher fileWatcher = new FileWatcher(testProject);
			pool.put(testProject, fileWatcher);
			try {
				fileWatcher.watch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		this.context = context;
		return this;
	}
}
