package org.testeditor.ui.parts.reporting;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.constants.TestEditorUIEventConstants;

/**
 * Viewpart to display the test log of the last run.
 *
 */
public class TestLogView {

	public static final String ID = "org.testeditor.ui.parts.reporting.TestLogView";

	private MPart part;
	private Text testLog;

	private static final Logger LOGGER = Logger.getLogger(TestLogView.class);

	@Inject
	private TestStructureService testStructureService;

	/**
	 * Default Constructor of the TestLogView.
	 * 
	 * @param part
	 *            to be used to communicate with the application model.
	 */
	@Inject
	public TestLogView(MPart part) {
		this.part = part;
	}

	/**
	 * Constructs the UI after creating and building this object.
	 * 
	 * @param parent
	 *            composite to build the ui on.
	 */
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		testLog = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		testLog.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * Sets the Focus in the text widget of this view.
	 */
	@Focus
	public void setFocus() {
		testLog.setFocus();
	}

	/**
	 * 
	 * @param testStructure
	 *            which last log should be displayed.
	 */
	public void setTestStructure(TestStructure testStructure) {
		try {
			String logData = testStructureService.getLogData(testStructure);
			part.setLabel("Test log: " + testStructure.getName());
			testLog.setText(logData);
		} catch (SystemException e) {
			LOGGER.error("Reading Testlog", e);
		}
	}

	/**
	 * Consumes the
	 * <code>TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED</code> event and
	 * updates the log content with the new execution result.
	 * 
	 * @param testStructure
	 *            which was executed.
	 */
	@Inject
	@Optional
	public void onTestExecutionShowTestLogForLastRun(
			@UIEventTopic(TestEditorUIEventConstants.TESTSTRUCTURE_EXECUTED) TestStructure testStructure) {
		setTestStructure(testStructure);
	}

}