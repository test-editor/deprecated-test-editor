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
package org.testeditor.ui.handlers.rename;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.wizardpages.AbstractRenameTestStructureWizardPage;
import org.testeditor.ui.wizardpages.RenameScenarioSuiteWizardPage;

/**
 * Rename-Handler for the ScenarioSuite.
 * 
 * @author llipinski
 * 
 */
public class RenameScenarioSuiteHandler extends AbstractRenameHandler {
	@Inject
	private IEclipseContext context;

	@Override
	protected AbstractRenameTestStructureWizardPage getRenameTestStructureWizardPage(TestStructure selectedTS) {
		RenameScenarioSuiteWizardPage sceanrioSuiteWizardPage = ContextInjectionFactory.make(
				RenameScenarioSuiteWizardPage.class, context);
		sceanrioSuiteWizardPage.setSelectedTestStructure(selectedTS);
		return sceanrioSuiteWizardPage;
	}

	@Override
	protected void executeSpecials(TestStructure selected, String sbname) throws SystemException {
	}

}