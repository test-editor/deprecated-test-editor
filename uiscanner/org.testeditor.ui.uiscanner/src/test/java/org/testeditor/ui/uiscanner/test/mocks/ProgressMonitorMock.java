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
package org.testeditor.ui.uiscanner.test.mocks;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Ignore;

/**
 * calls to create a ProgressMonitorMock for scanning the websites.
 * 
 *
 */
public final class ProgressMonitorMock {

	/**
	 * creates a ProgressMonitorMock for scanning the websites.
	 * 
	 * @return IProgressMonitor
	 */
	@Ignore
	public IProgressMonitor getProgressMonitor() {
		return new IProgressMonitor() {

			@Override
			public void worked(int work) {
			}

			@Override
			public void subTask(String name) {
			}

			@Override
			public void setTaskName(String name) {
			}

			@Override
			public void setCanceled(boolean value) {
			}

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public void internalWorked(double work) {
			}

			@Override
			public void done() {
			}

			@Override
			public void beginTask(String name, int totalWork) {
			}
		};
	}
}
