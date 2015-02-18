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
package org.testeditor.ui.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.ui.mocks.EventBrokerMock;

/**
 * 
 * Integration Tests for TestLogViewer.
 * 
 * @author karsten
 */
public class TestLogViewerTest {

	private TestLogViewer testLogViewer;
	private Shell shell;
	private List<Object> events;

	/**
	 * Test that the logViewer handles missing files.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testTailChecksForMissingFiles() throws Exception {
		TestLogViewer testLogViewer = new TestLogViewer();
		testLogViewer.setRefreshTime(1);
		testLogViewer.setAbsolutelogFileName("/This Is Not a regularFileOn../.../System");
		try {
			testLogViewer.startTailOnTestLog(new TestCase());
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * Init the Object Under Test.
	 */
	@Before
	public void initOUT() {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(IEventBroker.class, getEventBrokerMock());
		testLogViewer = ContextInjectionFactory.make(TestLogViewer.class, context);
		shell = new Shell();
		events = new ArrayList<Object>();
		testLogViewer.createUI(shell);
		TestCase tc = new TestCase();
		tc.setName("Test");
		testLogViewer.setExecutingTestStructure(tc);
	}

	/**
	 * 
	 * @return Mock of the <code>IEventBroker</code>
	 */
	private IEventBroker getEventBrokerMock() {
		return new EventBrokerMock() {

			@Override
			public boolean send(String topic, Object data) {
				events.add(data);
				return false;
			}

			@Override
			public boolean post(String topic, Object data) {
				events.add(data);
				return false;
			}
		};
	}

	/**
	 * Cleanup Ressources like SWT Widgets used in the Test.
	 */
	@After
	public void cleanUpRessources() {
		shell.dispose();
	}

	/**
	 * Test The Runnable to update the styledtext.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testRunnableForUIUpdate() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader("Hello World")) {
			private int count = 0;

			@Override
			public boolean ready() throws IOException {
				count++;
				return count < 3;
			}

			@Override
			public String readLine() throws IOException {
				return count + "";
			}
		};
		testLogViewer.getUIUpdateRunnable(br, "MyTestCase").run();
		br.close();
		assertEquals("1\n2\n", testLogViewer.getTestLogText());
	}

	/**
	 * Receive the Log Message over the EventBroker Service.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testBrokerMessageOnBReaderWithContent() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader("Hello World")) {
			private int count = 0;

			@Override
			public boolean ready() throws IOException {
				count++;
				return count < 2;
			}

			@Override
			public String readLine() throws IOException {
				return "TestEditorLoggingInteraction Method : click ";
			}
		};
		testLogViewer.getUIUpdateRunnable(br, "MyTestCase").run();
		assertEquals(1, events.size());
		assertEquals("TestEditorLoggingInteraction Method : click ", events.get(0));
		br.close();
	}

	/**
	 * Test that no Message comes over the EventBroker, if the BufferedReader
	 * has no content.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testNoBrokerMessageOnBReaderWithOutContent() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader("Hello World")) {

			@Override
			public boolean ready() throws IOException {
				return false;
			}

			@Override
			public String readLine() throws IOException {
				return "TestEditorLoggingInteraction Method : click ";
			}
		};
		testLogViewer.getUIUpdateRunnable(br, "MyTestCase").run();
		assertEquals(0, events.size());
		br.close();
	}

	/**
	 * Test that no Message comes over the EventBroker, if the BufferedReader
	 * has only messages from other log entries.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testNoBrokerMessageOnBReaderWithContentThatIsNotFromTestEditorLoggingInteraction() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader("Hello World")) {
			private int count = 0;

			@Override
			public boolean ready() throws IOException {
				count++;
				return count < 2;
			}

			@Override
			public String readLine() throws IOException {
				return "Hello";
			}
		};
		testLogViewer.getUIUpdateRunnable(br, "MyTestCase").run();
		assertEquals(0, events.size());
		br.close();
	}

	/**
	 * Test that only the Lines from the TestEditorLoggingInteraction are
	 * catched.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testBrokerMessagesOnlyWithTestEditorLoggingInteraction() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader("Hello World")) {
			private int count = 0;

			@Override
			public boolean ready() throws IOException {
				count++;
				return count < 2;
			}

			@Override
			public String readLine() throws IOException {
				return "Hello\nTestEditorLoggingInteraction Method : click ";
			}
		};
		testLogViewer.getUIUpdateRunnable(br, "MyTestCase").run();
		assertEquals(1, events.size());
		assertEquals("TestEditorLoggingInteraction Method : click ", events.get(0));
		br.close();
	}

}
