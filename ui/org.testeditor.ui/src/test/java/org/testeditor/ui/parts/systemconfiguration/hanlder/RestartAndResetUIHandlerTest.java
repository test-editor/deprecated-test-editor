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
package org.testeditor.ui.parts.systemconfiguration.hanlder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.junit.Test;
import org.test.ui.mock.PartServiceAdapter;
import org.test.ui.mock.TestEditorConfigurationServiceMock;
import org.testeditor.ui.parts.systemconfiguration.handler.RestartAndResetUIHandler;

/**
 * 
 * Modul Test for RestartAndResetUIHandler.
 * 
 */
public class RestartAndResetUIHandlerTest {

	/**
	 * Test the Restart Call.
	 */
	@Test
	public void testRestart() {
		RestartAndResetUIHandler handler = new RestartAndResetUIHandler();
		final Map<String, String> map = new HashMap<String, String>();
		handler.resetAndRestart(new IWorkbench() {

			@Override
			public boolean restart() {
				map.put("restart", "true");
				return true;
			}

			@Override
			public String getId() {
				return null;
			}

			@Override
			public MApplication getApplication() {
				return null;
			}

			@Override
			public boolean close() {
				return false;
			}
		}, null, null, new TestEditorConfigurationServiceMock() {
			@Override
			public void setResetApplicationState(boolean resetState) {
				map.put("resetState", String.valueOf(resetState));
			}
		}, new PartServiceAdapter());
		assertTrue("Expecting restart call.", map.containsKey("restart"));
		assertTrue("Expecting restart state.", map.containsKey("resetState"));
		assertEquals("Expecting restart state is set to true in the config service.", "true", map.get("resetState"));
	}

}
