package org.testeditor.aml.dsl.e4.ui.dialog;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditor;
import org.eclipse.xtext.ui.editor.embedded.EmbeddedEditorFactory;
import org.eclipse.xtext.ui.editor.embedded.IEditedResourceProvider;
import org.testeditor.aml.dsl.e4.ui.Activator;

import com.google.inject.Injector;

@SuppressWarnings("restriction")
public class XtextEditorDialog extends TitleAreaDialog {

	@Inject
	private EmbeddedEditorFactory factory;
	
	@Inject
	private Provider<XtextResourceSet> resourceSetProvider;
	
	public XtextEditorDialog(Shell parentShell) {
		super(parentShell);
		Injector injector = Activator.getInstance().getInjector();
		injector.injectMembers(this);
	}
	
	@Override
	protected Control createDialogArea(Composite superParent) {
		Composite parent = (Composite) super.createDialogArea(superParent);
		final XtextResourceSet resourceSet = resourceSetProvider.get();
		EmbeddedEditor editor = factory.newEditor(new IEditedResourceProvider() {
			@Override
			public XtextResource createResource() {
				return (XtextResource) resourceSet.createResource(URI.createURI("_virtual.aml"));
			}
		}).showErrorAndWarningAnnotations().withParent(parent);
		editor.createPartialEditor("", "package com.example", "", true);
		editor.getViewer().getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setMessage("Modify the AML with this awesome embedded editor.");
		return parent;
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Embedded Xtext Editor");
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
}
