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
package org.testeditor.ui.mocks;

import java.util.Map;

import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;

/**
 * 
 * Mock for TestEditorConfigurationService.
 * 
 */
public class TestEditorConfigurationServiceMock implements TestEditorConfigurationService {

	@Override
	public void loadGlobalVariablesAsSystemProperties() throws BackingStoreException {
	}

	@Override
	public void updatePair(String key, String value) {
	}

	@Override
	public void clearKey(String key) throws BackingStoreException {
	}

	@Override
	public void storeChanges() throws BackingStoreException {
	}

	@Override
	public Map<String, String> getGlobalVariables() throws BackingStoreException {
		return null;
	}

	@Override
	public void setResetApplicationState(boolean resetState) {
	}

	@Override
	public boolean isResetApplicationState() {
		return false;
	}

}
