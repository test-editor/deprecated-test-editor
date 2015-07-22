
package org.testeditor.aml.dsl.e4.ui;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.testeditor.aml.dsl.e4.ui.dialog.XtextEditorDialog;
import org.testeditor.aml.dsl.ui.wizard.AmlNewProjectWizard;

import com.google.inject.Injector;

public class TestHandler {

	@Inject
	IEclipseContext context;

	@Inject
	EPartService partService;

	@Inject
	EModelService modelService;

	@Inject
	MApplication app;

	@Execute
	public void execute(Shell shell) {
		openEmbeddedEditor(shell);
//		openEditor();
//		openNewWizard(shell);
	}

	protected void openEmbeddedEditor(Shell shell) {
		XtextEditorDialog dialog = new XtextEditorDialog(shell);
		dialog.open();
	}

	protected void openEditor() {
		MPart part = partService.createPart("org.testeditor.aml.dsl.Aml");
		MPartStack editorStack = (MPartStack) modelService.find("org.testeditor.ui.partstack.0", app);
		editorStack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}
	
	protected void openNewWizard(Shell shell) {
		Injector injector = Activator.getInstance().getInjector();
		AmlNewProjectWizard wizard = injector.getInstance(AmlNewProjectWizard.class);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();
	}

}