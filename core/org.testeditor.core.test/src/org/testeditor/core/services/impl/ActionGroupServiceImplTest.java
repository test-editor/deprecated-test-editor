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
package org.testeditor.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.AbstractAction;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.ProjectLibraryConfig;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.services.interfaces.LibraryConstructionException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorPlugInService;
import org.testeditor.core.services.interfaces.TestProjectService;

/**
 * Test the service implementation at {@link ActionGroupServiceImpl}.
 */
@Ignore
public class ActionGroupServiceImplTest {
	private static final String PATH_ACTION_GROUPS_XML = "testLibrary" + File.separatorChar + "AllActionGroups.xml";
	private static final String PATH_TECHNICAL_BINDINGS_XML = "testLibrary" + File.separatorChar
			+ "TechnicalBindingTypeCollection.xml";

	private static final String ACTION_GROUP_NAME_LOGIN = "Anmeldung";
	private static final String ACTION_GROUP_NAME_COMMON_BROWSER = "Allgemein Browser";
	private static final String ACTION_GROUP_NAME_INVALID = "InvalidActionGroupName";
	private static final String ACTION_LOGIN_LINE_INPUT_PASSWORD = "|gebe in das Feld|Passwort|den Wert|Ufhat869#+?|ein|";
	private static final String ACTION_LOGIN_LINE_INPUT_PASSWORD_INVALID = "|Gib in Feld|Invalid|den Wert|123|ein|";

	private static final String ACTION_LOGIN_LINE_BUTTON_LOGIN = "|klicke auf|Login|";
	private static final String ACTION_LOGIN_LINE_BUTTON_LOGIN_INVALID = "|klicke auf Button|Invalid|";
	private static final String TECHNICAL_BINDING_TYPE_INPULT_SHORT_NAME = "Wert eingeben";
	private static final String TECHNICAL_BINDING_TYPE_BUTTON_SHORT_NAME = "Button betaetigen";
	private ArrayList<Argument> arguments = new ArrayList<Argument>();

	private static ActionGroupServiceImpl actionGroupService;
	private TestProject testProject;
	private TestFlow testFlow;

	/**
	 * Sets up the internal service for all tests.
	 * 
	 * @throws Exception
	 *             is thrown if an internal error occurred
	 */

	@BeforeClass
	public static void initiliazeTest() throws Exception {
		actionGroupService = new ActionGroupServiceImpl();
		actionGroupService.bindLibraryReader(new LibraryReaderService() {

			@Override
			public ProjectActionGroups readBasisLibrary(ProjectLibraryConfig libraryConfig) throws SystemException,
					LibraryConstructionException {
				ProjectActionGroups actionGroups = new ProjectActionGroups();
				ActionGroup actionGroup = new ActionGroup();
				Action action = new Action();
				action.setArguments(new ArrayList<Argument>());
				actionGroup.addAction(action);
				actionGroup.setName("dummy");
				actionGroups.addActionGroup(actionGroup);
				return actionGroups;
			}

			@Override
			public String getId() {
				return "org.testeditor.dummylibrary";
			}
		});
	}

	/**
	 * Initializes for each test the XML config (see the testLibrary folder).
	 */
	@Before
	public void initializeTestProject() {
		testFlow = new TestCase();

		TestProjectConfig testProjectConfig = new TestProjectConfig();
		TestEditorPlugInService service = ServiceLookUpForTest.getService(TestEditorPlugInService.class);
		Properties properties = new Properties();
		properties.put(TestEditorPlugInService.LIBRARY_ID, "org.testeditor.xmllibrary");
		properties.put(
				"library.xmllibrary.actiongroup",
				new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
						.append(PATH_ACTION_GROUPS_XML).toString());
		properties.put(
				"library.xmllibrary.technicalbindings",
				new StringBuffer(new File("").getAbsolutePath()).append(File.separatorChar)
						.append(PATH_TECHNICAL_BINDINGS_XML).toString());
		properties.put(TestProjectService.VERSION_TAG, TestProjectService.VERSION1_2);
		ProjectLibraryConfig libraryConfig = service.createProjectLibraryConfigFrom(properties);
		testProjectConfig.setProjectLibraryConfig(libraryConfig);
		testProjectConfig.getLibraryLoadingStatus().setErrorLessLoaded(true);

		testProject = new TestProject();
		testProject.setName("firstTestProject");
		testProject.setTestProjectConfig(testProjectConfig);
		testProject.addChild(testFlow);
	}

	/**
	 * Tests the retrieval of an action group "Anmeldung" by his valid name.
	 */

	@Test
	public void testGetActionGroupValidNameLogin() {
		ActionGroup actionGroup = actionGroupService.getActionGroup(testProject, ACTION_GROUP_NAME_LOGIN);

		assertNotNull("action group " + ACTION_GROUP_NAME_LOGIN + " not found", actionGroup);
		assertFalse("actions of group is empty", actionGroup.getActions().isEmpty());
		assertTrue("invalid size of actions at the action group, size is: " + actionGroup.getActions().size(),
				actionGroup.getActions().size() == 8);
	}

	/**
	 * Tests the retrieval of an action group "Allgemein Browser" by his valid
	 * name.
	 */
	@Test
	public void testGetActionGroupValidNameCommonBrowser() {
		ActionGroup actionGroup = actionGroupService.getActionGroup(testProject, ACTION_GROUP_NAME_COMMON_BROWSER);

		assertNotNull("action group " + ACTION_GROUP_NAME_COMMON_BROWSER + " not found", actionGroup);
		assertFalse("actions of group is empty", actionGroup.getActions().isEmpty());
		assertTrue("invalid size of actions at the action group, size is: " + actionGroup.getActions().size(),
				actionGroup.getActions().size() == 7);
	}

	/**
	 * Tests the retrieval of an action group by an invalid name.
	 */
	@Test
	public void testGetActionGroupInvalidName() {
		ActionGroup actionGroup = actionGroupService.getActionGroup(testProject, ACTION_GROUP_NAME_INVALID);

		assertNull("action group " + ACTION_GROUP_NAME_INVALID + " was found", actionGroup);
	}

	/**
	 * Tests the retrieval of an action by a valid input line.
	 */
	@Test
	public void testGetActionByLineValidLineInputPassword() {
		IAction action = actionGroupService.getActionByLine(testProject, getActionGroupLogin(),
				ACTION_LOGIN_LINE_INPUT_PASSWORD, arguments, false);

		assertNotNull("action not found", action);
		assertTrue("action short name is invalid: " + action.getTechnicalBindingType().getShortName(), action
				.getTechnicalBindingType().getShortName().equals(TECHNICAL_BINDING_TYPE_INPULT_SHORT_NAME));
	}

	/**
	 * Tests the retrieval of an action by an invalid input line.
	 */
	@Test
	public void testGetActionByLineInvalidLineInputPassword() {
		IAction action = actionGroupService.getActionByLine(testProject, getActionGroupLogin(),
				ACTION_LOGIN_LINE_INPUT_PASSWORD_INVALID, arguments, false);

		assertTrue("invalid action", action instanceof UnparsedActionLine);
	}

	/**
	 * Tests the retrieval of an action by a valid input line.
	 */
	@Test
	public void testGetActionByLineValidLineButtonLogin() {
		IAction action = actionGroupService.getActionByLine(testProject, getActionGroupLogin(),
				ACTION_LOGIN_LINE_BUTTON_LOGIN, arguments, false);

		assertNotNull("action not found", action);
		assertTrue("action short name is invalid: " + action.getTechnicalBindingType().getShortName(), action
				.getTechnicalBindingType().getShortName().equals(TECHNICAL_BINDING_TYPE_BUTTON_SHORT_NAME));
	}

	/**
	 * Tests the retrieval of an action by an invalid input line.
	 */
	@Test
	public void testGetActionByLineInvalidLineButtonLogin() {
		IAction action = actionGroupService.getActionByLine(testProject, getActionGroupLogin(),
				ACTION_LOGIN_LINE_BUTTON_LOGIN_INVALID, arguments, false);

		assertTrue("invalid action", action instanceof UnparsedActionLine);
	}

	/**
	 * Tests the retrieval of an actionParts by an line.
	 */
	@Test
	public void testGetActionParts() {
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), "Anmeldung");

		IAction action = actionGroupService.getActionByLine(testFlow.getRootElement(), actionGroup,
				"|gebe in das Feld|Passwort|den Wert|hugendubel|ein|", arguments, false);

		assertTrue(action.getTechnicalBindingType().getActionParts().size() == 5);

		assertTrue(action.getTechnicalBindingType().getActionParts().iterator().next().getType() == ActionElementType.TEXT);

		assertTrue(action.getTechnicalBindingType().getActionParts().iterator().next().getValue()
				.equalsIgnoreCase("gebe in das Feld"));
	}

	/**
	 * Test the retrieval of the technicalBtechnicalBindingType by the shortName
	 * of a mask.
	 */
	@Test
	public void testGetTechnicalBindingTypes() {

		String shortName = "Anmeldung";
		List<TechnicalBindingType> technicalBindingTypeList = actionGroupService.getTechnicalBindingTypes(
				testFlow.getRootElement(), shortName);
		assertTrue(technicalBindingTypeList.get(0).getActionParts().get(0).getType().equals(ActionElementType.TEXT));

	}

	/**
	 * test the getChoiceList Method from the action.
	 */
	@Test
	public void getChoiceList() {
		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);
		String actionKey = "Land";
		List<AbstractAction> actions = actionGroup.getActions();

		for (AbstractAction action : actions) {
			if (action instanceof Action && ((Action) action).getActionName().equalsIgnoreCase(actionKey)) {
				ChoiceList choiceList = ((Action) action).getChoiceLists().get(0);
				assertEquals("argument1", choiceList.getArgumentId());
				assertEquals("USA", choiceList.getChoices().get(2));
			}
		}
	}

	/**
	 * test the getChoiceList Method from the action.
	 */
	@Test
	public void getChoiceListLand2() {
		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);
		String actionKey = "Land_2";
		List<AbstractAction> actions = actionGroup.getActions();

		for (AbstractAction action : actions) {
			if (action instanceof Action && ((Action) action).getActionName().equalsIgnoreCase(actionKey)) {
				ChoiceList choiceList = ((Action) action).getChoiceLists().get(0);
				assertEquals("argument2", choiceList.getArgumentId());
				assertEquals("USA", choiceList.getChoices().get(2));
			}
		}
	}

	/**
	 * Test a action with to values.
	 * 
	 */
	@Test
	public void testActionWithToValues() {

		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);

		IAction action = actionGroupService.getActionByLine(testFlow.getRootElement(), actionGroup,
				"|gebe in das Feld|Passwort|den Wert|Testpasswort|ein|", arguments, false);

		assertTrue(action.getTechnicalBindingType().getActionParts().size() == 5);

		assertTrue(action.getTechnicalBindingType().getActionParts().get(0).getType() == ActionElementType.TEXT);

		assertTrue(action.getTechnicalBindingType().getActionParts().get(0).getValue()
				.equalsIgnoreCase("gebe in das Feld"));
		assertEquals(action.getArguments().get(1).getValue(), "Testpasswort");
	}

	/**
	 * test the reading of the choicelist.
	 */
	@Test
	public void testActionWithArgumentFromChoiseList() {

		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);

		IAction action = actionGroupService.getActionByLine(testFlow.getRootElement(), actionGroup,
				"|wähle in Feld|land|den Wert|Deutschland|aus|", arguments, false);

		assertTrue(action.getTechnicalBindingType().getActionParts().size() == 5);

		assertTrue(action.getTechnicalBindingType().getActionParts().get(0).getType() == ActionElementType.TEXT);

		assertTrue(action.getTechnicalBindingType().getActionParts().get(0).getValue()
				.equalsIgnoreCase("wähle in Feld"));
		assertEquals(action.getArguments().get(1).getValue(), "Deutschland");
	}

	/**
	 * Test a action with to values.
	 * 
	 */
	@Test
	public void testActionparseChoiseLine() {

		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);

		IAction action = actionGroupService.getActionByLine(testFlow.getRootElement(), actionGroup,
				"|wähle den Wert|Euro|in dem Feld|Währung|aus|", arguments, false);
		assertTrue("size: " + action.getTechnicalBindingType().getActionParts().size(), action
				.getTechnicalBindingType().getActionParts().size() == 5);
	}

	/**
	 * Test a action with to values.
	 * 
	 */
	@Test
	public void testActionValueKeyParseLine() {

		String maskName = "Anmeldung";
		ActionGroup actionGroup = actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);

		IAction action = actionGroupService.getActionByLine(testFlow.getRootElement(), actionGroup,
				"|gebe in das Feld|Passwort|den Wert|Testpasswort|ein|", arguments, false);

		assertTrue("size: " + action.getTechnicalBindingType().getActionParts().size(), action
				.getTechnicalBindingType().getActionParts().size() == 5);
	}

	/**
	 * Returns the action group "login" inside the test project.
	 * 
	 * @return action group login
	 */
	private ActionGroup getActionGroupLogin() {
		ActionGroup actionGroup = actionGroupService.getActionGroup(testProject, ACTION_GROUP_NAME_LOGIN);
		assertNotNull("action group " + ACTION_GROUP_NAME_LOGIN + " not found", actionGroup);
		return actionGroup;
	}

	/**
	 * 
	 * @throws SystemException
	 *             for Test
	 */
	@Test(expected = SystemException.class)
	public void testSystemException() throws SystemException {
		SystemException systemException = new SystemException("save data failed");
		throw systemException;
	}

	/**
	 * test the getTechnicalBindingByName method.
	 */
	@Test
	public void getTechnicalBindingByNameTest() {

		String maskName = "Anmeldung";
		actionGroupService.getActionGroup(testFlow.getRootElement(), maskName);

		TechnicalBindingType technicalBindingType = actionGroupService.getTechnicalBindingByName(
				testFlow.getRootElement(), maskName, TECHNICAL_BINDING_TYPE_INPULT_SHORT_NAME);
		assertEquals(TECHNICAL_BINDING_TYPE_INPULT_SHORT_NAME, technicalBindingType.getShortName());

	}

	/**
	 * test the createTestActionGroupTestCase method.
	 */
	@Test
	public void getCreateActionGroupTest() {
		String maskName = "Anmeldung";
		List<String> inputLineParts = new ArrayList<String>();
		String partZero = "gebe in das Feld";
		inputLineParts.add(partZero);
		String argument = "Passwort";
		inputLineParts.add(argument);
		inputLineParts.add("den Wert");

		inputLineParts.add("TestPasswort");
		inputLineParts.add("ein");
		arguments.add(new Argument("1", argument));

		TestActionGroupTestCase testActionGroupTestCase = actionGroupService.createTestActionGroupTestCase(
				testFlow.getRootElement(), maskName, inputLineParts, arguments);
		assertEquals(maskName, testActionGroupTestCase.getActionGroupName());
		assertEquals(partZero + " ", testActionGroupTestCase.getActionLines().get(0).getTexts().get(0));
		assertEquals(arguments.get(0).getValue(), testActionGroupTestCase.getActionLines().get(0).getArguments().get(0)
				.getValue());
	}
}