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
package org.testeditor.core.services.dispatcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.services.interfaces.LibraryConstructionException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.plugins.LibraryReaderServicePlugIn;

/**
 * Dispatcher of the LibraryReaderService. The Dispatcher makes a lookup for the
 * plug-in fitting the model.
 *
 */
public class LibraryReaderServiceDispatcher implements LibraryReaderService {

	private static final Logger LOGGER = Logger.getLogger(LibraryReaderServiceDispatcher.class);
	private Map<String, LibraryReaderServicePlugIn> libraryReaderServices = new HashMap<String, LibraryReaderServicePlugIn>();

	/**
	 * 
	 * @param readerService
	 *            to be bind to this service.
	 */
	public void bind(LibraryReaderServicePlugIn readerService) {
		libraryReaderServices.put(readerService.getId(), readerService);
		LOGGER.info("Binding LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	/**
	 * 
	 * @param readerService
	 *            to be removed.
	 */
	public void unBind(LibraryReaderServicePlugIn readerService) {
		libraryReaderServices.remove(readerService.getId());
		LOGGER.info("Removing LibraryReaderService Plug-In " + readerService.getClass().getName());
	}

	@Override
	public ProjectActionGroups readBasisLibrary(ProjectLibraryConfig libraryConfig) throws SystemException,
			LibraryConstructionException {
		return libraryReaderServices.get(libraryConfig.getId()).readBasisLibrary(libraryConfig);
	}

}
