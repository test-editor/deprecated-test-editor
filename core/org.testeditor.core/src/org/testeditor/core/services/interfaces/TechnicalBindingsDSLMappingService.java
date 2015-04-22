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

/**
 * 
 * Reverse Mapping Service to Lookup the Test DSL with Technical Bindings.
 * 
 * <b>Example:</b> Technical Binding: |click|@guiElement| maps to:
 * !|scenario|klicke auf|guiElement|
 * 
 */
public interface TechnicalBindingsDSLMappingService {

	/**
	 * Maps a technical Binding to the Test DSL.
	 * 
	 * @param technicalBinding
	 *            to be mapped
	 * @return the TestDSL of the technicalBinding
	 */
	String mapTechnicalBindingToTestDSL(String technicalBinding);

}
