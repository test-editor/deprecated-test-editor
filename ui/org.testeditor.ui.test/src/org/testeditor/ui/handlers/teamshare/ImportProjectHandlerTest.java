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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.wizard.IWizard;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.adapter.TranslationServiceAdapter;
import org.testeditor.ui.utilities.TestEditorTranslationService;
import org.testeditor.ui.wizardpages.teamshare.TeamShareImportProjectWizardPage;

/**
 * 
 * Test the ImportProjectHandler.
 * 
 */
public class ImportProjectHandlerTest {

	/**
	 * Test the creation of a Wizard with a TeamShareImportProjectWizardPage.
	 */
	@Test
	public void testCreateNewTeamShareImportWizard() {
		ImportProjectHandler importProjectHandler = new ImportProjectHandler();
		IEclipseContext context = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass())
				.getBundleContext());
		context.set(TestEditorTranslationService.class, null);
		context.set(TranslationService.class, new TranslationServiceAdapter().getTranslationService());
		IWizard wizard = importProjectHandler.createNewTeamShareImportWizard(context);
		assertNotNull("Expecting creation of the new wizard", wizard);
		assertTrue("First Page is a TeamShareImportProjectWizardPage.",
				wizard.getStartingPage() instanceof TeamShareImportProjectWizardPage);
	}
		
}
