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

import java.util.List;

import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;

/**
 * Interface for a service input of a tree.
 * 
 */
public interface TestStructureTreeModelService {
	
	/**
	 * 
	 * The Root Elements of the Test Structures used in the Tree. 
	 * 
	 * @return a list of TestCompositeStructure
	 * @throws SystemException
	 *             while reading elements
	 */
	List<? extends TestStructure> getElements() throws SystemException;
	
}
