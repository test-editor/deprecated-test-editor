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
package org.testeditor.teamshare.svn;

import org.apache.log4j.Logger;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;

/**
 * Event Handler to log the svn actions and if an ProgressListener is present it
 * reports the action to the listener.
 * 
 */
public class SVNLoggingEventHandler implements ISVNEventHandler {

	private ProgressListener listener;
	private Logger logger;

	/**
	 * Constructor of the SVNLoggingEventHanlder.
	 * 
	 * @param listener
	 *            listener of the ui to show progress details.
	 * @param logger
	 *            to log the event in the log file of svn.
	 */
	public SVNLoggingEventHandler(ProgressListener listener, Logger logger) {
		this.listener = listener;
		this.logger = logger;
	}

	@Override
	public void checkCancelled() throws SVNCancelException {
		if (listener != null && listener.isCanceled()) {
			throw new SVNCancelException();
		}
	}

	@Override
	public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		if (listener != null) {
			listener.log(arg0.toString());
		}

		if (logger.isTraceEnabled()) {
			logger.trace(arg0);
		}
	}

}