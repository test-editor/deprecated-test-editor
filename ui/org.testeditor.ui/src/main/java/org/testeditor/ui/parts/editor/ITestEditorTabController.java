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
package org.testeditor.ui.parts.editor;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Interface that has to be implemented to add an additional tag to the
 * testEditorEditor.
 * 
 *
 */
public interface ITestEditorTabController {

	/**
	 * Method to create the composite that will be rendered in the tab.
	 * 
	 * @param parent
	 *            - the tabfolder or parent of the new composite element
	 * @param mpart
	 *            - the mpart-Object of the TestEditorController about changes
	 *            in the tab.
	 * @param translationService
	 *            used to translate the ui.
	 * @return the composite
	 */
	Composite createTab(CTabFolder parent, MPart mpart, TestEditorTranslationService translationService);

	/**
	 * Sets the testFlow for the tab.
	 * 
	 * @param testFlow
	 *            - the testFlow
	 */
	void setTestFlow(TestFlow testFlow);

	/**
	 * Saves the data in the tab (if needed). This is done after the testFlow
	 * was stored and is not handled in a transaction.
	 * 
	 * @throws SystemException
	 *             on save failure in the backend.
	 */
	void save() throws SystemException;

	/**
	 * the label of the tab. It is in the responsibility of the implementation
	 * to use the translation service.
	 * 
	 * @param translationService
	 *            used to translate the ui.
	 * 
	 * @return the label
	 */
	String getLabel(TestEditorTranslationService translationService);

	/**
	 * Defines whether a tab will be visible.
	 * 
	 * @return true if the editor will be rendered
	 */
	boolean isVisible();
}
