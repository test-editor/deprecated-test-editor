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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.action.Action;
import org.testeditor.core.model.action.ActionElement;
import org.testeditor.core.model.action.ActionElementType;
import org.testeditor.core.model.action.ActionGroup;
import org.testeditor.core.model.action.Argument;
import org.testeditor.core.model.action.ChoiceList;
import org.testeditor.core.model.action.IAction;
import org.testeditor.core.model.action.ProjectActionGroups;
import org.testeditor.core.model.action.TechnicalBindingType;
import org.testeditor.core.model.action.UnparsedActionLine;
import org.testeditor.core.model.teststructure.TestActionGroup;
import org.testeditor.core.model.teststructure.TestActionGroupTestCase;
import org.testeditor.core.model.teststructure.TestActionGroupTestScenario;
import org.testeditor.core.model.teststructure.TestFlow;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.ActionGroupService;
import org.testeditor.core.services.interfaces.LibraryDataStoreService;
import org.testeditor.core.services.interfaces.LibraryReadException;
import org.testeditor.core.services.interfaces.LibraryReaderService;
import org.testeditor.core.util.ActionLineSplitter;

/**
 * Service implementation for the interface {@link ActionGroupService}.
 */
public class ActionGroupServiceImpl implements ActionGroupService {
	private static final Logger LOGGER = Logger.getLogger(ActionGroupServiceImpl.class);

	private LibraryDataStoreService libraryDataStoreService;
	private LibraryReaderService libraryReader;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ActionGroup> getActionGroups(TestProject testProject) {
		if (!testProject.getTestProjectConfig().getLibraryLoadingStatus().isErrorLessLoaded()) {
			return null;
		}
		readProjectLibraryIfNeeded(testProject);
		return libraryDataStoreService.getProjectsActionGroups().get(testProject.getFullName()).getActionGroupList();
	}

	/**
	 * Reads the library for the project.
	 * 
	 * @param testProject
	 *            the project
	 */
	private void readProjectLibrary(TestProject testProject) {
		try {
			ProjectActionGroups projectActionGroups = libraryReader.readBasisLibrary(testProject.getTestProjectConfig()
					.getProjectLibraryConfig());
			projectActionGroups.setProjectName(testProject.getName());
			libraryDataStoreService.addProjectActionGroups(projectActionGroups);
		} catch (LibraryReadException e) {
			LOGGER.error("Error reading library :: FAILED", e);
		} catch (SystemException e) {
			LOGGER.error("Error constructing object-tree :: FAILED", e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionGroup getActionGroup(TestProject testProject, String name) {
		readProjectLibraryIfNeeded(testProject);
		List<ActionGroup> actionGroups = getActionGroups(testProject);
		if (actionGroups != null) {
			for (ActionGroup actionGroup : actionGroups) {
				if (actionGroup.getName().equalsIgnoreCase(name)) {
					return actionGroup;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TechnicalBindingType> getTechnicalBindingTypes(TestProject testProject, String name) {

		readProjectLibraryIfNeeded(testProject);
		List<TechnicalBindingType> acTypeL = new ArrayList<TechnicalBindingType>();

		ActionGroup acGroup = getActionGroup(testProject, name);

		if (acGroup == null) {
			return acTypeL;
		}

		acGroup.sortActions();

		for (IAction action : acGroup.getActions()) {
			acTypeL.add(action.getTechnicalBindingType());
		}

		// Comparator<TechnicalBindingType> comp = new
		// TechnicalBindingTypeComperator();
		// Collections.sort(acTypeL, comp);

		TechnicalBindingType aType = null;
		ArrayList<TechnicalBindingType> toRemoved = new ArrayList<TechnicalBindingType>();
		for (TechnicalBindingType techType : acTypeL) {
			if (aType != null && techType.getId().equalsIgnoreCase(aType.getId())) {
				toRemoved.add(techType);
			} else {
				aType = techType;
			}
		}
		for (TechnicalBindingType techType : toRemoved) {
			acTypeL.remove(techType);
		}

		return acTypeL;
	}

	/**
	 * this method unbinds the LibraryDataStoreService and is needed for the
	 * OSGI-Service architecture.
	 * 
	 * @param libraryDataStoreService
	 *            LibraryDataStoreService
	 */
	public void unbindLibraryDataStoreService(LibraryDataStoreService libraryDataStoreService) {
		setLibraryDataStoreService(null);
	}

	/**
	 * this method binds the LibraryDataStoreService and is needed for the
	 * OSGI-Service architecture.
	 * 
	 * @param libraryDataStoreService
	 *            LibraryDataStoreService
	 */
	public void bindLibraryDataStoreService(LibraryDataStoreService libraryDataStoreService) {
		setLibraryDataStoreService(libraryDataStoreService);
	}

	/**
	 * primitive setter for the variable libraryDataStoreService.
	 * 
	 * @param libraryDataStoreService
	 *            LibraryDataStoreService
	 */
	public void setLibraryDataStoreService(LibraryDataStoreService libraryDataStoreService) {
		this.libraryDataStoreService = libraryDataStoreService;
	}

	/**
	 * this method binds the LibraryReaderService and is needed for the
	 * OSGI-Service architecture.
	 * 
	 * @param libraryReader
	 *            LibraryReaderService
	 */
	public void bindLibraryReader(LibraryReaderService libraryReader) {
		setLibraryReader(libraryReader);
	}

	/**
	 * this method unbinds the LibraryReaderService and is needed for the
	 * OSGI-Service architecture.
	 * 
	 * @param libraryReader
	 *            LibraryReaderService
	 */
	public void unbindLibraryReader(LibraryReaderService libraryReader) {
		setLibraryReader(null);
	}

	/**
	 * primitive setter for the variable libraryReader.
	 * 
	 * @param libraryReader
	 *            LibraryReaderService
	 */
	public void setLibraryReader(LibraryReaderService libraryReader) {
		this.libraryReader = libraryReader;
	}

	/**
	 * Returns the action by a given uncoded String line (e.g. a FitNesse table
	 * column separated by pipes) and project.
	 * 
	 * @param testProject
	 *            the current project (e.g. object for "DemoWebTests")
	 * @param actionGroup
	 *            related group (e.g. "Login" mask)
	 * @param line
	 *            line in clear code (e.g. |insert|123|into|user name|field|)
	 * @param arguments
	 *            List<Arguments> arguments
	 * @param isTestActionGroupTestScenario
	 *            true, if it's a scenario, else false
	 * @return action (e.g. action for the insert into field)
	 */
	public IAction getActionByLine(TestProject testProject, ActionGroup actionGroup, String line,
			List<Argument> arguments, boolean isTestActionGroupTestScenario) {
		String saveLine = line;
		readProjectLibraryIfNeeded(testProject);

		if (line != null && actionGroup != null) {

			IAction actionImpl = getAnalyzableActionByLine(actionGroup, line, arguments, isTestActionGroupTestScenario);
			if (actionImpl != null) {
				return actionImpl;
			}
		}
		UnparsedActionLine unparesdActionLine = new UnparsedActionLine(saveLine);
		return unparesdActionLine;
	}

	/**
	 * gets the {@link Action}, if the line is analyzable.
	 * 
	 * @param actionGroup
	 *            the {@link ActionGroup}
	 * @param line
	 *            the line of the TestFlow as a String
	 * @param arguments
	 *            List<Argument>
	 * @param isTestActionGroupTestScenario
	 *            boolean
	 * @return {@link Action}, if the line is analyzable, else null
	 */
	private IAction getAnalyzableActionByLine(ActionGroup actionGroup, String line, List<Argument> arguments,
			boolean isTestActionGroupTestScenario) {
		String saveLine = line;
		Action actionClone = null;
		StringBuilder actionLine = new StringBuilder();
		int valuePos = 0;
		for (IAction action : actionGroup.getActions()) {
			int argPos = 0;
			if (action instanceof Action) {
				actionClone = new Action((Action) action);
				actionClone.setArguments(new ArrayList<Argument>());
				for (ActionElement ae : actionClone.getTechnicalBindingType().getActionParts()) {
					String part = ae.getValue();
					valuePos++;
					if (ae.getType().compareTo(ActionElementType.ACTION_NAME) == 0) {
						part = action.getArguments().get(argPos).getLocator();
						argPos++;
						actionClone.getArguments().add(action.getArguments().get(actionClone.getArguments().size()));
					} else if (ae.getType().compareTo(ActionElementType.ARGUMENT) == 0) {
						// at this place there is a variable value in the line
						// stored
						// so this can't compared with the action of the
						// actionGroup
						// but we can compare the beginning of the line until
						// now

						String comString = ActionLineSplitter.splitLineBeforePatternNo(line, "|", valuePos);
						if (!comString.equalsIgnoreCase(actionLine.toString() + "|")) {
							continue;
						}
						Argument argument = getArgument(arguments, argPos, line, valuePos);

						if (ae.getId() != null
								&& !checkTheArgumentAgainstTheChoiceList((Action) action, ae, argument,
										isTestActionGroupTestScenario)) {
							List<String> params = new ArrayList<String>();
							params.add(argument.getValue());
							continue;
						}
						actionClone.getArguments().add(argument);
						//
						line = ActionLineSplitter.removePatternNofromLine(line, "|", valuePos);
						valuePos--;
						argPos++;
						continue;
					}
					actionLine = actionLine.append("|").append(part);
				}
				actionLine = actionLine.append("|");
				if (actionLine.toString().equalsIgnoreCase(line)) {
					return actionClone;
				} else {
					actionLine = new StringBuilder();
					valuePos = 0;
					line = saveLine;
				}
			}
		}
		return new UnparsedActionLine(saveLine, UnparsedActionLine.ACTION_NOT_FOUND, new ArrayList<String>());
	}

	/**
	 * checks the argument against the list of choices.
	 * 
	 * @param action
	 *            {@link Action}
	 * @param ae
	 *            the {@link ActionElement}
	 * @param argument
	 *            {@link Argument}
	 * @param isTestActionGroupTestScenario
	 *            boolean
	 * @return true, if the argument is in the list of choices.
	 */
	private boolean checkTheArgumentAgainstTheChoiceList(Action action, ActionElement ae, Argument argument,
			boolean isTestActionGroupTestScenario) {
		if (isTestActionGroupTestScenario && argument.getValue().startsWith("@")) {
			return true;
		}
		List<ChoiceList> choiceLists = action.getChoiceLists();
		for (ChoiceList choice : choiceLists) {
			if (choice.getArgumentId().equalsIgnoreCase(ae.getId()) && !containsArgument(choice.getChoices(), argument)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * returns the argument from the list of arguments, or if the it not
	 * contains, than take it from the line.
	 * 
	 * @param arguments
	 *            List<Argument>
	 * @param argumentNo
	 *            int position of the argument
	 * @param line
	 *            String
	 * @param valuePos
	 *            int
	 * @return the argument from the list of arguments, or if the it not
	 *         contains, than take it from the line.
	 */
	private Argument getArgument(List<Argument> arguments, int argumentNo, String line, int valuePos) {

		if (arguments.size() > argumentNo) {
			return arguments.get(argumentNo);
		}
		Argument argument = new Argument();
		String value = ActionLineSplitter.getValueAtPos(line, "|", valuePos);
		if (value.startsWith("@{")) {
			argument.setValue("@" + value.substring(2, value.length() - 1));
		} else {
			argument.setValue(value);
		}

		return argument;
	}

	/**
	 * 
	 * @param choices
	 *            List<String> of choices
	 * @param argument
	 *            chosen the argument
	 * @return true, if the argument is in the list of choices
	 */
	private boolean containsArgument(List<String> choices, Argument argument) {
		for (String choice : choices) {
			if (argument.getValue().equalsIgnoreCase(choice)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * reads the projectLibrary if needed.
	 * 
	 * @param testProject
	 *            the project
	 */
	private void readProjectLibraryIfNeeded(TestProject testProject) {
		if (!libraryDataStoreService.getProjectsActionGroups().containsKey(testProject.getFullName())) {
			readProjectLibrary(testProject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TestActionGroup createTestActionGroup(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments) {
		TestActionGroup testActionGr = new TestActionGroup();
		return createTestActionGroupInternal(testProject, actionGroupName, inputLineParts, arguments, testActionGr);
	}

	/**
	 * this method creates the contents of the actionGroup.
	 * 
	 * @param testProject
	 *            TestProject
	 * @param actionGroupName
	 *            String
	 * @param inputLineParts
	 *            List<String>
	 * @param arguments
	 *            List<Argument>
	 * @param testActionGr
	 *            TestActionGroup
	 * @return TestActionGroup
	 */
	private TestActionGroup createTestActionGroupInternal(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments, TestActionGroup testActionGr) {
		testActionGr.setActionGroupName(actionGroupName);
		ActionGroup actionGroup = getActionGroup(testProject.getRootElement(), actionGroupName);
		testActionGr.setActionGroupName(actionGroupName);
		StringBuilder inputLine = new StringBuilder("|");
		for (String input : inputLineParts) {
			if (input.startsWith("|") && input.endsWith("|")) {
				inputLine = inputLine.append(input.substring(1));
			} else {
				inputLine = inputLine.append(input).append("|");
			}
		}
		boolean isScenarioTestActionGroup = false;
		if (testActionGr instanceof TestActionGroupTestScenario) {
			isScenarioTestActionGroup = true;
		}

		IAction foundedAction = getActionByLine(testProject.getRootElement(), actionGroup, inputLine.toString(),
				arguments, isScenarioTestActionGroup);
		IAction action = null;
		if (foundedAction instanceof Action) {
			action = new Action((Action) foundedAction);
			action.setArguments(foundedAction.getArguments());
		} else {
			action = new UnparsedActionLine((UnparsedActionLine) foundedAction);
		}
		testActionGr.addActionLine(action);
		return testActionGr;
	}

	@Override
	public TestActionGroupTestCase createTestActionGroupTestCase(TestProject testProject, String actionGroupName,
			List<String> inputLineParts, List<Argument> arguments) {
		TestActionGroupTestCase testActionGr = new TestActionGroupTestCase();
		return (TestActionGroupTestCase) createTestActionGroupInternal(testProject, actionGroupName, inputLineParts,
				arguments, testActionGr);
	}

	@Override
	public TestActionGroupTestScenario createTestActionGroupTestScenario(TestProject testProject,
			String actionGroupName, List<String> inputLineParts, List<Argument> arguments) {
		TestActionGroupTestScenario testActionGr = new TestActionGroupTestScenario();
		return (TestActionGroupTestScenario) createTestActionGroupInternal(testProject, actionGroupName,
				inputLineParts, arguments, testActionGr);
	}

	@Override
	public TechnicalBindingType getTechnicalBindingByName(TestProject testProject, String actionGroupName,
			String technicalBindingType) {
		List<TechnicalBindingType> technicalBindingTypes = getTechnicalBindingTypes(testProject, actionGroupName);
		for (TechnicalBindingType techtype : technicalBindingTypes) {
			if (techtype.getShortName().equalsIgnoreCase(technicalBindingType)) {
				return techtype;
			}
		}
		return null;
	}

	@Override
	public void addActionLine(TestActionGroup testActionGroup, TestFlow testFlow, String line) {
		String actionGroupName = testActionGroup.getActionGroupName();
		ActionGroup actionGroup = getActionGroup(testFlow.getRootElement(), actionGroupName);
		if (actionGroup == null) {
			testActionGroup.setParsedActionGroup(false);
		}
		IAction action = getActionByLine(testFlow.getRootElement(), actionGroup, line, new ArrayList<Argument>(),
				testActionGroup.isScenarioTestActionGroup());
		testActionGroup.getActionLines().add(action);
	}
}
