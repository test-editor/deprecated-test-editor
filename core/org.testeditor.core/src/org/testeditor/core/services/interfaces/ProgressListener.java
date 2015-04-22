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
/**
 * 
 */
package org.testeditor.core.services.interfaces;

/**
 * Listener for logging of progress information.
 * 
 * Any long running job inside the test editor gets injected or passed a
 * ProgressListener. Any relevant information about the running transaction can
 * be passed to the Listener and is displayed in a matching ProgressMonitor
 * Dialog. This indirection is preferred to the injection of an eclipse specific
 * Component to an independent job.
 */
public interface ProgressListener {

	/**
	 * Display some information about a job's progress to the user
	 * 
	 * @param progressInfo
	 *            text to be log
	 */
	void log(String progressInfo);

}
