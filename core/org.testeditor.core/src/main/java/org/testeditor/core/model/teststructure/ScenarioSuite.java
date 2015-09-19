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
package org.testeditor.core.model.teststructure;


/**
 * Test suite includes a hierarchical structure of sub test suites or/and test
 * cases.
 */
public class ScenarioSuite extends TestCompositeStructure {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceCode() {
		return "!contents";
	}

	@Override
	public String getPageType() {
		return "Scenariosuite";
	}

	@Override
	public String getTypeName() {
		return TestType.SCENARIOSUITE.getName();
	}

}
