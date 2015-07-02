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
package org.testeditor.core.services.interfaces;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.model.teststructure.TestProject;

public interface FileWatchService {

	void watch(TestProject testProject);

	void setContext(IEclipseContext context);
}
