package org.testeditor.ui.parts.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.StyleRange;
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
import org.testeditor.core.services.interfaces.TestStructureService;
import org.testeditor.ui.adapter.MPartAdapter;
import org.testeditor.ui.adapter.TestStructureServiceAdapter;
import org.testeditor.ui.constants.ColorConstants;

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
		context.set(TestStructureService.class, new TestStructureServiceAdapter() {
			@Override
			public String getTestExecutionLog(TestStructure testStructure) throws SystemException {
				return "exec success.";
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
	 * Tests the style ranges creation based on a testlog entry.
	 * 
	 */
	@Test
	public void testGetStyleRanges() {
		TestLogView logView = new TestLogView(null);
		String logData = "No Style\nINFO Range\nERROR Range\nINFO another info\nTRACE Range\nDEBUG Range";
		StyleRange[] styleRanges = logView.getStyleRanges(logData);
		assertEquals(5, styleRanges.length);
		assertTrue(styleRanges[0].foreground.equals(ColorConstants.COLOR_DARK_GRAY));
		assertTrue(styleRanges[1].foreground.equals(ColorConstants.COLOR_RED));
		assertTrue(styleRanges[2].foreground.equals(ColorConstants.COLOR_DARK_GRAY));
		assertTrue(styleRanges[3].foreground.equals(ColorConstants.COLOR_DARK_GREEN));
		assertTrue(styleRanges[4].foreground.equals(ColorConstants.COLOR_BLUE));
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
