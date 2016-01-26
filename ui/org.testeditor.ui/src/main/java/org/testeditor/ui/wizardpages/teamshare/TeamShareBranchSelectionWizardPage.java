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
package org.testeditor.ui.wizardpages.teamshare;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Wizard Page to show available branches of the team provider and select one of
 * them. Multiple selection is not supported.
 *
 */
public class TeamShareBranchSelectionWizardPage extends WizardPage {

	private TableViewer releaseViewer;
	private List<String> availableReleaseNames;
	protected String selectedReleaseName;

	/**
	 * Default constructor.
	 */
	public TeamShareBranchSelectionWizardPage() {
		this("");
	}

	/**
	 * 
	 * @param pageName
	 *            of the wizard page.
	 */
	protected TeamShareBranchSelectionWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		setControl(parent);
		releaseViewer = new TableViewer(parent);
		releaseViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		releaseViewer.setContentProvider(new ArrayContentProvider());
		if (availableReleaseNames != null) {
			releaseViewer.setInput(availableReleaseNames);
		}
		setPageComplete(!releaseViewer.getSelection().isEmpty());
		releaseViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!releaseViewer.getSelection().isEmpty()) {
					setPageComplete(true);
					IStructuredSelection sec = (IStructuredSelection) releaseViewer.getSelection();
					selectedReleaseName = (String) sec.getFirstElement();
				}
			}
		});
	}

	public void setAvailableReleaseNames(List<String> availableReleaseNames) {
		this.availableReleaseNames = availableReleaseNames;
		if (releaseViewer != null) {
			releaseViewer.setInput(availableReleaseNames);
		}
	}

}
