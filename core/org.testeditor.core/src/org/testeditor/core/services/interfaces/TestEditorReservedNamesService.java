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

import java.util.Set;

/**
 * 
 * This Service gives informations about reserved names of the testeditor.
 * 
 */
public interface TestEditorReservedNamesService {

	/**
	 * Gives a list of names that are not allowed to be used as names for
	 * <code>TestStructure</code>. This are special TestSuite like
	 * TestKomponents, which contains Scenarios.
	 * 
	 * @return a set with names that are reserved.
	 */
	Set<String> getReservedTestStructureNames();

	/**
	 * Checks if the parameter is a reserved name in the context of the
	 * TestEditor.
	 * 
	 * @param name
	 *            to be checked.
	 * @return true if the name is reserved.
	 */
	boolean isReservedName(String name);

}
