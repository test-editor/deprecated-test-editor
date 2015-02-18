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
package org.testeditor.core.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.testeditor.core.services.interfaces.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.TestEditorReservedNamesService;

/**
 * 
 * TestEditor implementation of the <code>TestEditorReservedNamesService</code>
 * to handle the TestEditor name conventions.
 * 
 */
public class TestEditorReservedNamesServiceImpl implements TestEditorReservedNamesService {

	@Override
	public Set<String> getReservedTestStructureNames() {
		Set<String> result = new HashSet<String>();
		result.add(TestEditorGlobalConstans.TEST_SCENARIO_SUITE);
		result.add(TestEditorGlobalConstans.TEST_KOMPONENTS);
		return result;
	}

	@Override
	public boolean isReservedName(String name) {
		return getReservedTestStructureNames().contains(name);
	}

}
