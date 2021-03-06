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
package org.testeditor.ui.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestScenario;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.model.teststructure.TestSuite;
import org.testeditor.core.services.interfaces.TestExecutionEnvironmentService;
import org.testeditor.core.services.interfaces.TestScenarioService;
import org.testeditor.ui.adapter.StructuredSelectionAdapter;
import org.testeditor.ui.adapter.TestExceutionEnvironmentServiceAdapter;
import org.testeditor.ui.mocks.TestScenarioServiceMock;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Integrationtest the Rules for Handlers of the TestExplorer.
 * 
 */
public class CanExecuteTestExplorerHandlerRulesTest {

	private Shell shell;

	/**
	 * Test the Rule that the Handler works only with one Element.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnlyOneElementRule() throws Exception {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertFalse(
				rules.canExecuteOnlyOneElementRule((IStructuredSelection) getTreeViewerMockEmptyMock().getSelection()));
		assertTrue(rules.canExecuteOnlyOneElementRule(
				(IStructuredSelection) getTreeViewerMockOneSelectionMock().getSelection()));
		assertFalse(rules.canExecuteOnlyOneElementRule(
				(IStructuredSelection) getTreeViewerMockManySelectionMock().getSelection()));
	}

	/**
	 * Test the Rule that the Handler works one one ore more Elements.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnOneOrManyElementRule() throws Exception {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertFalse(rules
				.canExecuteOnOneOrManyElementRule((IStructuredSelection) getTreeViewerMockEmptyMock().getSelection()));
		assertTrue(rules.canExecuteOnOneOrManyElementRule(
				(IStructuredSelection) getTreeViewerMockOneSelectionMock().getSelection()));
		assertTrue(rules.canExecuteOnOneOrManyElementRule(
				(IStructuredSelection) getTreeViewerMockManySelectionMock().getSelection()));
	}

	/**
	 * Test Identification of Root in a Rule.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnNoneRootRule() throws Exception {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertFalse(rules.canExecuteOnNoneRootRule((IStructuredSelection) getTreeViewerMockRoot().getSelection()));
		assertTrue(rules
				.canExecuteOnTestSuiteRule((IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
	}

	/**
	 * Test Identification of TestSuite in a Rule.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnTestSuiteRule() throws Exception {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertTrue(rules
				.canExecuteOnTestSuiteRule((IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
	}

	/**
	 * Test Identification of TestSuite in a Rule.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testCanExecuteOnTestProjectRule() throws Exception {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertFalse(rules
				.canExecuteOnTestProjectRule((IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
		assertTrue(rules.canExecuteOnTestProjectRule((IStructuredSelection) getTreeViewerMockRoot().getSelection()));
	}

	/**
	 * Test Handling for delete a <code>TestScenario</code>. The rules allows
	 * delete on all other Objects. THe rules checks for
	 * <code>TestScenario</code> that there is no <code>TestFlow</code> using
	 * it.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Ignore
	@Test
	public void testCanExecuteOnUnusedScenario() throws Exception {
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestScenarioService.class, getTestScenarioServiceMock());
		context.set(TestExecutionEnvironmentService.class, null);
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory
				.make(CanExecuteTestExplorerHandlerRules.class, context);
		assertTrue("Expecting True for Non TestScenario Objects.", rules
				.canExecuteOnUnusedScenario((IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
		assertTrue("Expecting true for unused TestScenario Objects.", rules.canExecuteOnUnusedScenario(
				(IStructuredSelection) getTreeViewerMockWithTestScenrarioMock(new TestScenario() {
					@Override
					public String getName() {

						return "notInUses";
					}
				}).getSelection()));
		assertFalse("Expecting False for used TestScenario Objects.", rules.canExecuteOnUnusedScenario(
				(IStructuredSelection) getTreeViewerMockWithTestScenrarioMock(new TestScenario() {
					@Override
					public String getName() {
						return "used";
					}
				}).getSelection()));
	}

	/**
	 * tests the canExecuteTeamShareApproveOrUpdate method.
	 * 
	 */
	@Test
	public void testCanExecuteTeamShareApproveOrUpdate() {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertFalse(rules.canExecuteOnOneOrManyElementRule(
				(IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection())
				&& rules.canExecuteOnTeamShareProject(
						(IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
		assertTrue(rules.canExecuteOnOneOrManyElementRule(
				(IStructuredSelection) getTreeViewerMockWithFilledIterator().getSelection())
				&& rules.canExecuteOnTeamShareProject(
						(IStructuredSelection) getTreeViewerMockWithFilledIterator().getSelection()));

	}

	/**
	 * tests the canExecuteTeamShareApproveOrUpdate method.
	 * 
	 */
	@Test
	public void testCanExecuteTeamShareApproveOrUpdateSelectionNotUnderSVN() {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		IStructuredSelection selectionMock = (IStructuredSelection) getTreeViewerMockWithFilledIteratorTestProjectNotUnderSVN()
				.getSelection();
		assertTrue(rules.canExecuteOnOneOrManyElementRule(selectionMock));
		assertFalse(rules.canExecuteOnOneOrManyElementRule(selectionMock)
				&& rules.canExecuteOnTeamShareProject(selectionMock));
	}

	/**
	 * tests the canExecuteTeamShareApproveOrUpdate method.
	 * 
	 */
	@Test
	public void testCanExecuteTeamShareApproveOrUpdateSelectionUnderAndNotUnderSVN() {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		IStructuredSelection selectionMock = (IStructuredSelection) getTreeViewerMockWithFilledIteratorTestProjectUnderAndNotUnderSVN()
				.getSelection();
		assertTrue(rules.canExecuteOnOneOrManyElementRule(selectionMock));
		assertFalse(rules.canExecuteOnOneOrManyElementRule(selectionMock)
				&& rules.canExecuteOnTeamShareProject(selectionMock));
	}

	/**
	 * Tests the can execute on testflow.
	 */
	@Test
	public void testCanExecuteOnTestFlowRule() {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		assertTrue(rules.canExecuteOnTestFlowRule((IStructuredSelection) getTreeViewerMockEmptyMock().getSelection()));
		assertFalse(rules.canExecuteOnTestFlowRule((IStructuredSelection) getTreeViewerMockRoot().getSelection()));
		assertFalse(
				rules.canExecuteOnTestFlowRule((IStructuredSelection) getTreeViewerMockWithTestSuite().getSelection()));
		assertTrue(rules.canExecuteOnTestFlowRule((IStructuredSelection) getTreeViewerMockScenario().getSelection()));
	}

	/**
	 * tests the canExecuteOnTestScenarioRule method.
	 * 
	 */
	@Test
	public void testCanExecuteOnTestScenarioRule() {
		CanExecuteTestExplorerHandlerRules rules = new CanExecuteTestExplorerHandlerRules();
		TestExplorer explorerMockSC = getTestExplorerMock(getTreeViewerMockScenario());
		assertTrue(rules.canExecuteOnTestScenarioRule(explorerMockSC.getSelection()));
		TestExplorer explorerMockNoSC = getTestExplorerMock(
				getTreeViewerMockWithFilledIteratorTestProjectUnderAndNotUnderSVN());
		assertFalse(rules.canExecuteOnTestScenarioRule(explorerMockNoSC.getSelection()));

	}

	/**
	 * Tests the CanExecuteOnTestStructureWithLaunchedTestExecutionEnvironment.
	 */
	@Test
	public void testCanExecuteOnTestStructureWithLaunchedTestExecutionEnvironment() {
		final TestProject tpWithEnv = new TestProject();
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestScenarioService.class, getTestScenarioServiceMock());
		context.set(TestExecutionEnvironmentService.class, new TestExceutionEnvironmentServiceAdapter() {

			@Override
			public boolean isTestEnvironmentLaunchedFor(TestProject testProject) {
				return tpWithEnv == testProject;
			}
		});
		CanExecuteTestExplorerHandlerRules rules = ContextInjectionFactory
				.make(CanExecuteTestExplorerHandlerRules.class, context);
		assertFalse(rules.canExecuteOnTestStructureWithLaunchedTestExecutionEnvironment(
				getStructureSelectionMock(new TestProject())));
		assertTrue(rules
				.canExecuteOnTestStructureWithLaunchedTestExecutionEnvironment(getStructureSelectionMock(tpWithEnv)));
	}

	/**
	 * Creates a mock.
	 * 
	 * @param ts
	 *            used in the mock
	 * @return selection mock object
	 */
	private IStructuredSelection getStructureSelectionMock(final TestStructure ts) {
		return new StructuredSelectionAdapter() {

			@Override
			public Object getFirstElement() {
				return ts;
			}
		};
	}

	/**
	 * 
	 * @param testScenario
	 *            to be passed to the TestScenarioServiceMock.
	 * @return TreeViewer Mock with the TestscenarioMock in the selection.
	 */
	private TreeViewer getTreeViewerMockWithTestScenrarioMock(final TestScenario testScenario) {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {

					@Override
					public Object getFirstElement() {
						return testScenario;

					}

					@Override
					public Iterator<?> iterator() {
						return new Iterator<Object>() {
							private int countHasNext = 0;

							@Override
							public boolean hasNext() {
								boolean returnValue = true;
								if (countHasNext != 0) {
									returnValue = false;
								}

								return returnValue;
							}

							@Override
							public Object next() {
								if (!hasNext()) {
									throw new NoSuchElementException();
								}
								countHasNext++;
								return testScenario;
							}

							@Override
							public void remove() {

							}
						};
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TestScenarioServiceMock to check the TestScenarioMocks
	 */
	private TestScenarioService getTestScenarioServiceMock() {
		return new TestScenarioServiceMock();
	}

	/**
	 * Init UI Classes.
	 */
	@Before
	public void setup() {
		shell = new Shell(Display.getDefault());
	}

	/**
	 * Dispose UI Elements.
	 */
	@After
	public void tearDown() {
		shell.dispose();
	}

	/**
	 * 
	 * @param treeviewer
	 *            to be used in the TestExplorerMock
	 * @return TestExplorerMock
	 */
	private TestExplorer getTestExplorerMock(final TreeViewer treeviewer) {
		return new TestExplorer(null) {
			@Override
			public TreeViewer getTreeViewer() {
				return treeviewer;
			}
		};
	}

	/**
	 * 
	 * @return a TreeViewer with no selection.
	 */
	private TreeViewer getTreeViewerMockEmptyMock() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 0;
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return a TreeViewer with many selected elements.
	 */
	private TreeViewer getTreeViewerMockManySelectionMock() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 2;
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return a TreeViewer with one selection.
	 */
	private TreeViewer getTreeViewerMockOneSelectionMock() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 1;
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with a Root Node
	 */
	private TreeViewer getTreeViewerMockRoot() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Iterator<TestStructure> iterator() {
						ArrayList<TestStructure> arrayList = new ArrayList<TestStructure>();
						TestProject testProject = new TestProject();
						testProject.setTestProjectConfig(new TestProjectConfig());
						arrayList.add(testProject);
						return arrayList.iterator();
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with a Scenario Node
	 */
	private TreeViewer getTreeViewerMockScenario() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 1;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Iterator<TestStructure> iterator() {
						ArrayList<TestStructure> arrayList = new ArrayList<TestStructure>();
						arrayList.add(new TestScenario());
						return arrayList.iterator();
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with a TestCase as a child of a TestProject under
	 *         SVN
	 */
	private TreeViewer getTreeViewerMockWithFilledIterator() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 1;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Iterator<TestStructure> iterator() {
						TestProject testProject = new TestProject();
						TestProjectConfig testProjectConfig = new TestProjectConfig();
						testProjectConfig.setTeamShareConfig(new TeamShareConfig() {

							@Override
							public String getId() {
								return "svn-team-share-mock";
							}
						});
						testProject.setTestProjectConfig(testProjectConfig);
						TestCase testCase = new TestCase();
						testProject.addChild(testCase);
						ArrayList<TestStructure> arrayList = new ArrayList<TestStructure>();
						arrayList.add(testCase);
						return arrayList.iterator();
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with a TestCase as a child of a TestProject not
	 *         under SVN
	 */
	private TreeViewer getTreeViewerMockWithFilledIteratorTestProjectNotUnderSVN() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 1;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Iterator<TestStructure> iterator() {
						TestProject testProject = new TestProject();
						TestProjectConfig testProjectConfig = new TestProjectConfig();
						testProject.setTestProjectConfig(testProjectConfig);
						TestCase testCase = new TestCase();
						testProject.addChild(testCase);
						ArrayList<TestStructure> arrayList = new ArrayList<TestStructure>();
						arrayList.add(testCase);
						return arrayList.iterator();
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with two TestCases as a children of two
	 *         TestProjects one under SVN and one not under SVN
	 * 
	 */
	private TreeViewer getTreeViewerMockWithFilledIteratorTestProjectUnderAndNotUnderSVN() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public int size() {
						return 2;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public Iterator<TestStructure> iterator() {
						TestProject testProject = new TestProject();
						TestProjectConfig testProjectConfig = new TestProjectConfig();
						testProjectConfig.setTeamShareConfig(new TeamShareConfig() {

							@Override
							public String getId() {
								return "svn-team-share-mock";
							}
						});
						testProject.setTestProjectConfig(testProjectConfig);
						TestCase testCase = new TestCase();
						testProject.addChild(testCase);
						ArrayList<TestStructure> arrayList = new ArrayList<TestStructure>();
						arrayList.add(testCase);
						TestProject testProjectNoSVN = new TestProject();
						TestProjectConfig testProjectConfigNoSVN = new TestProjectConfig();
						testProjectNoSVN.setTestProjectConfig(testProjectConfigNoSVN);
						TestCase testCaseNoSVN = new TestCase();
						testProjectNoSVN.addChild(testCaseNoSVN);
						arrayList.add(testCaseNoSVN);
						return arrayList.iterator();
					}
				};
			}
		};
	}

	/**
	 * 
	 * @return TreeViewer Mock with a TestSuite
	 */
	private TreeViewer getTreeViewerMockWithTestSuite() {
		return new TreeViewer(shell) {
			@Override
			public ISelection getSelection() {
				return new TreeSelection() {
					@Override
					public Object getFirstElement() {
						TestSuite result = new TestSuite();
						new TestSuite().addChild(result);
						return result;
					}

					@Override
					public Iterator<?> iterator() {
						ArrayList<Object> list = new ArrayList<Object>();
						list.add(getFirstElement());
						return list.iterator();
					}
				};
			}

		};
	}

}
