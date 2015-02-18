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
package org.testeditor.ui.handlers.teamshare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.ui.adapter.TranslationServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * 
 * Integration Tests for UpdateElementHandler.
 * 
 */
public class UpdateElementHandlerTest {

	/**
	 * 
	 * Tests the lookup of a TestStructure based on a relative path name. Path
	 * Name like: /DemoWebRapTests/DialogWidgetsSuite/ConfirmMessageTest
	 * 
	 * @throws SystemException
	 *             for test
	 */
	@Test
	public void testLookUpOfTestStructure() throws SystemException {
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(this.getClass())
				.getBundleContext());
		TestProject project = new TestProject();
		project.setName("DemoWebRapTests");
		TestSuite suite = new TestSuite();
		project.addChild(suite);
		suite.setName("DialogWidgetsSuite");
		TestCase testCase = new TestCase();
		testCase.setName("ConfirmMessageTest");
		suite.addChild(testCase);
		context.set(MApplication.class, null);
		context.set(Logger.class, null);
		context.set(TestEditorTranslationService.class, null);
		context.set(TranslationService.class, new TranslationServiceAdapter().getTranslationService());
		UpdateElementHandler handler = ContextInjectionFactory.make(UpdateElementHandler.class, context);
		TestStructure testStructure = handler.lookUpTestStructureFrom(new TeamChange(TeamChangeType.MODIFY,
				"DemoWebRapTests.DialogWidgetsSuite.ConfirmMessageTest", project));
		assertNotNull("TestStructure expected.", testStructure);
		assertEquals("Expecting MyTestCase as teststructure", "ConfirmMessageTest", testStructure.getName());
	}

}
