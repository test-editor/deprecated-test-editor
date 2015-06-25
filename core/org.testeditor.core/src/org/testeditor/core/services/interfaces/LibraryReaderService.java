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
package org.testeditor.core.services.interfaces;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;

/**
 * 
 * A LibraryReaderService loads a Library, i.e. a project specific collection of
 * ActionGroups and matching TechnicalBindings. That library is used as a basis
 * for a concrete ActionGroupService, which exposes the ActionGroups from the
 * library to its client.
 * 
 * This interface is intended to be used by clients and not to be implemented by
 * plug-ins. Plug-In developer should implement the interface:
 * LibraryReaderServicePlug-In.
 * 
 */
public interface LibraryReaderService {

	/**
	 * Reads the basis library for a project.
	 * 
	 * @param libraryConfig
	 *            the current ProjectLibraryConfig (e.g. object for
	 *            "DemoWebTests")
	 * @throws SystemException
	 *             on problems reading the library.
	 * @return {@link ProjectActionGroups}
	 * @throws LibraryConstructionException
	 *             on inconsistent library definition.
	 */
	ProjectActionGroups readBasisLibrary(ProjectLibraryConfig libraryConfig) throws SystemException,
			LibraryConstructionException;

}
