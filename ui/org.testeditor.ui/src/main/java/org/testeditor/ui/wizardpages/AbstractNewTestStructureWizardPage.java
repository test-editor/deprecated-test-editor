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
package org.testeditor.ui.wizardpages;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract wizard for any create operation regarding the test structure. E.g. a
 * lower class could implement a test case creation or a test suite creation.
 */
public abstract class AbstractNewTestStructureWizardPage extends AbstractTestStructureWizardPage {

	private static final Logger LOGGER = Logger.getLogger(AbstractNewTestStructureWizardPage.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isNameValid(String name) {
		if (!super.isNameValid(name)) {
			return false;
		}
		// ...by comparing all children with the new name
		if (getSelectedTestStrucutureElement() != null) {
			setSelectedTestStructure(getSelectedTestStrucutureElement());
			if (isNamePartOfChildren(name, getSelectedTestStructure())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		LOGGER.info("setTreeFilter");
		setTreeFilter();
		LOGGER.info("setTreeFilter done");
	}

	/**
	 * this method removes not correct elements from the tree and should be
	 * implemented in the subclasses.
	 */
	protected abstract void setTreeFilter();
}
