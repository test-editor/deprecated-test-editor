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
package org.testeditor.ui.analyzer;

import java.text.MessageFormat;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.analyzer.errormodel.Error;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;
import org.testeditor.ui.handlers.OpenTestStructureHandler;

/**
 * 
 * Viewpart to display Validation results of an Testcase validatitor job.
 * 
 */
public class ValidateResultsView {

	private TreeViewer treeViewer;
	private Label validatedObject;

	@Inject
	private IEclipseContext context;

	@Inject
	private TranslationService translate;

	/**
	 * 
	 * Creates a TreeViewer to display the validations.
	 * 
	 * @param parent
	 *            used to create UI Elements in.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		validatedObject = new Label(parent, SWT.NORMAL);
		validatedObject.setText(translate.translate("%viewpart.teststructurevalidation",
				"platform:/plugin/org.testeditor.ui.analyzer"));
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new ValidationContainerProvider());
		treeViewer.setLabelProvider(new ValidationLabelProvider());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent arg0) {
				Object element = ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
				TestFlow selected = null;
				if (element instanceof ErrorContainer) {
					selected = ((ErrorContainer) element).getTestFlow();
				} else {
					selected = ((Error) element).getTestFlow();
				}
				OpenTestStructureHandler handler = ContextInjectionFactory
						.make(OpenTestStructureHandler.class, context);
				handler.execute(selected, context);
			}
		});
	}

	/**
	 * Setting a result of a validation to display it.
	 * 
	 * @param values
	 *            collection or error container that represant the validation
	 *            run.
	 * @param validatedTestStructure
	 *            that was used as root element for the validation.
	 */
	public void setErrorContainers(Collection<ErrorContainer> values, TestStructure validatedTestStructure) {
		String translatedText = translate.translate("%viewpart.teststructurevalidation.header",
				"platform:/plugin/org.testeditor.ui.analyzer");
		validatedObject.setText(MessageFormat.format(translatedText, validatedTestStructure.getFullName(),
				values.size()));
		treeViewer.setInput(values);
		validatedObject.getParent().layout();
	}
}
