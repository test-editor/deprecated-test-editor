package org.testeditor.ui.parts.reporting;

import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.mocks.TestEditorPluginServiceMock;

/**
 * 
 * Modultests for TestLogView.
 *
 */
public class TestLogViewTest {

	private Shell shell;

	/**
	 * Tests the construction of the ui.
	 * 
	 * @throws Exception
	 *             on testfailure.
	 */
	@Test
	public void testRetrivingTestcaseOnTestStructureExecutedEvent() throws Exception {
		MPart part = new MPartAdapter();
		IEclipseContext context = EclipseContextFactory.create();
		context.set(MPart.class, part);
		context.set(TestEditorPlugInService.class, new TestEditorPluginServiceMock() {
			@Override
			public TestStructureService getTestStructureServiceFor(String testServerID) {
				return new TestStructureServiceAdapter() {
					@Override
					public String getLogData(TestStructure testStructure) throws SystemException {
						return "exec success.";
					}
				};
			}
		});
		context.set(Composite.class, shell);
		TestLogView testLogView = ContextInjectionFactory.make(TestLogView.class, context);
		TestProject testProject = new TestProject();
		testProject.setTestProjectConfig(new TestProjectConfig());
		TestCase testCase = new TestCase();
		testProject.addChild(testCase);
		testCase.setName("MyName");
		testLogView.onTestExecutionShowTestLogForLastRun(testCase);
		assertTrue(part.getLabel().endsWith(testCase.getName()));
	}

	/**
	 * Setup ui object for the test.
	 */
	@Before
	public void setUp() {
		shell = new Shell();
	}

	/**
	 * Cleanups resources after test.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

}
