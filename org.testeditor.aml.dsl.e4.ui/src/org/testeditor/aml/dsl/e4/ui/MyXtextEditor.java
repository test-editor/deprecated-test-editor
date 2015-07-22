package org.testeditor.aml.dsl.e4.ui;

import javax.annotation.PostConstruct;

import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Injector;

public class MyXtextEditor extends XtextEditor {

	@PostConstruct
	public void bootInjection() {
		Injector injector = Activator.getInstance().getInjector();
		injector.injectMembers(this);
	}
	
}
