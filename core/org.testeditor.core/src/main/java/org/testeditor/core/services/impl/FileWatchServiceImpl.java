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
package org.testeditor.core.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.FileWatchService;

/**
 * Service for watching on file system for check modifications of library files.
 *
 */
public class FileWatchServiceImpl implements FileWatchService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(FileWatchServiceImpl.class);

	private Map<TestProject, FileWatcher> pool = new HashMap<TestProject, FileWatcher>();

	private IEclipseContext context;

	@Override
	public void watch(TestProject testProject) {

		if (!pool.containsKey(testProject)) {
			if (context != null) {
				context.set(TestProject.class, testProject);
				FileWatcher fileWatcher = ContextInjectionFactory.make(FileWatcher.class, context);

				pool.put(testProject, fileWatcher);
				try {
					fileWatcher.watch();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		this.context = context;
		return this;
	}

	@Override
	public void setContext(IEclipseContext context) {
		this.context = context;

	}

	@Override
	public void dropWatchers() {
		Collection<FileWatcher> values = pool.values();
		for (FileWatcher fileWatcher : values) {
			fileWatcher.stop();
		}
		pool = new HashMap<TestProject, FileWatcher>();
	}

	@Override
	public void remove(TestProject testProject) {
		if (pool.containsKey(testProject)) {
			FileWatcher fileWatcher = pool.get(testProject);
			fileWatcher.stop();
			pool.remove(testProject);
		}
	}
}
