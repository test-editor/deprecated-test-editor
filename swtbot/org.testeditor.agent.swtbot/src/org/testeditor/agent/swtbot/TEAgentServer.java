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
package org.testeditor.agent.swtbot;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.anyOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withRegex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuFinder;
import org.eclipse.swtbot.swt.finder.finders.MenuFinder;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.testing.ITestHarness;
import org.eclipse.ui.testing.TestableObject;
import org.hamcrest.Matcher;

/**
 * 
 * TestEditor Agent Server to accepts Commands from the Editor Fixture. Accepts
 * Fixture Commands and delegates it to the AUT.
 * 
 * 
 */
public class TEAgentServer extends Thread implements ITestHarness {

	private static final Logger LOGGER = Logger.getLogger(TEAgentServer.class);

	private final TestableObject testableObject;
	private boolean launched;
	private SWTBot bot;

	private String result;
	private PrintWriter out;

	private static final int SERVER_PORT = 9090;

	/**
	 * @param testableObject
	 *            of the Eclipse Workbench.
	 * 
	 */
	public TEAgentServer(TestableObject testableObject) {
		this.testableObject = testableObject;
		launched = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.testing.ITestHarness#runTests()
	 */
	@Override
	public void runTests() {
		testableObject.testingStarting();

		if (Display.getDefault() != null) {
			bot = new SWTBot();
		}
		// SWTBotPreferences.PLAYBACK_DELAY = 50;
		launched = true;
	}

	/**
	 * Stops the Application by telling the Workbench that the test is Finished.
	 * SWTBot tries to close the active window. If there is no active window,
	 * the JVM will be terminated.
	 */
	public void stopApplication() {
		if (Display.getCurrent() != null && !Display.getCurrent().isDisposed()) {
			testableObject.testingFinished();
			LOGGER.info(">>>> Stopping AUT");
			bot.activeShell().close();
		} else {
			LOGGER.info(">>>> Terminating AUT");
			System.exit(0);
		}
	}

	/**
	 * Clicks in the Context Menu of an menu Item.
	 * 
	 * @param menuText
	 *            identifies the menuItem.
	 * @return SWTBot Message
	 */
	@SuppressWarnings("unchecked")
	public String clickContextMenu(final String menuText) {

		try {

			LOGGER.trace("clickContextMenu menuText: " + menuText);

			Matcher<MenuItem> withMnemonic = withRegex(menuText);
			final Matcher<MenuItem> matcher = allOf(widgetOfType(MenuItem.class), withMnemonic);

			final ContextMenuFinder menuFinder = new ContextMenuFinder(bot.tree().widget);

			try {

				bot.waitUntil(new DefaultCondition() {
					@Override
					public String getFailureMessage() {
						return "Could not find context menu with text: " + menuText; //$NON-NLS-1$
					}

					@Override
					public boolean test() throws Exception {
						return !menuFinder.findMenus(matcher).isEmpty();
					}
				});
			} catch (TimeoutException e) {
				LOGGER.error("ERROR timeout: " + e.getMessage());
				return "ERROR timeout: " + e.getMessage();
			}

			List<MenuItem> findMenus = menuFinder.findMenus(matcher);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("findMenus.size(): " + findMenus.size());
				for (MenuItem menuItem : findMenus) {
					LOGGER.trace("menuItem: " + new SWTBotMenu(menuItem).getText());
				}
			}

			if (findMenus.isEmpty()) {
				return "ERROR Coundn't find menu item for: " + menuText;
			}
			MenuItem menuItem = findMenus.get(0);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("found menuItem: " + new SWTBotMenu(menuItem).getText());
			}

			new SWTBotMenu(menuItem, matcher).click();

		} catch (Exception e) {
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Presses the shortcut specified by the given keys.
	 * 
	 * @param id
	 *            identifier of the StyledText
	 * @param modificationKeys
	 *            the combination of SWT.ALT | SWT.CTRL | SWT.SHIFT |
	 *            SWT.COMMAND.
	 * @param key
	 *            the character
	 * @return true, after sending the keys
	 */
	public String pressShortcutOfStyledText(final String id, final String modificationKeys, final char key) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("modificationKeys: " + modificationKeys);
			LOGGER.trace("key: " + key);
		}
		SWTBotStyledText styledTextWithId = bot.styledTextWithId(id);
		styledTextWithId.pressShortcut(Integer.valueOf(modificationKeys).intValue(), key);

		return Boolean.toString(true);
	}

	/**
	 * reads all projects and trace them to the log.
	 * 
	 * @return "true"
	 */
	public String readAllProjectsInTree() {
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		if (LOGGER.isTraceEnabled()) {
			for (SWTBotTreeItem item : allItems) {
				LOGGER.trace("Project: " + item.getText());
			}
		}

		return Boolean.toString(true);
	}

	/**
	 * deletes all projects.
	 * 
	 * @return "true"
	 */
	public String deleteAllProjects() {
		while (bot.tree().getAllItems().length > 0) {
			SWTBotTreeItem[] allItems = bot.tree().getAllItems();
			String[] itemsToExpand = new String[1];
			itemsToExpand[0] = allItems[0].getText();
			expandTreeItems(itemsToExpand);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("item to delete: " + allItems[0].getText());
			}
			clickContextMenu(".*Eintrag.*l.*schen.*");
			clickButton("TEXT::OK");
		}
		return Boolean.toString(true);
	}

	/**
	 * compares the count of project with the expectedCount.
	 * 
	 * @param expectedCount
	 *            the expected count of projects.
	 * @return "true", if the count of projects equals the expectedCount, else
	 *         an ERROR
	 */
	public String countProjectsEquals(String expectedCount) {
		SWTBotTreeItem[] allItems = bot.tree().getAllItems();
		if (allItems.length != Integer.valueOf(expectedCount).intValue()) {
			String message = "Inspected count of projects was: " + expectedCount + " but there are " + allItems.length
					+ " projects";
			LOGGER.error(message);
			return message;
		}
		return Boolean.toString(true);
	}

	/**
	 * compares the count of the children of the parent with the expectedCount.
	 * 
	 * @param nodes
	 *            the path to the parent
	 * @param expectedCount
	 *            the expected count of children
	 * @return "true", if the count equals the expectedCount, else an ERROR
	 */
	public String countChildrenEquals(String[] nodes, String expectedCount) {
		SWTBotTreeItem expandNode = bot.tree().expandNode(nodes);
		if (expandNode.getItems().length != Integer.valueOf(expectedCount)) {
			String message = "Inspected count of projects was: " + expectedCount + " but there are "
					+ expandNode.getItems().length + " projects";
			LOGGER.error(message);
			return message;
		}
		return Boolean.toString(true);
	}

	/**
	 * Presses the shortcut specified by the given keys.
	 * 
	 * @param id
	 *            identifier of the StyledText
	 * @param keyStrokeAsString
	 *            the string-representing of the KeyStroke
	 * @return true, after sending the keys
	 * @see org.eclipse.swtbot.swt.finder.keyboard.Keyboard#pressShortcut(KeyStroke...)
	 * @see org.eclipse.swtbot.swt.finder.keyboard.Keystrokes
	 */
	public String pressShortcutOfStyledText(final String id, String keyStrokeAsString) {
		try {
			SWTBotStyledText styledTextWithId = bot.styledTextWithId(id);
			styledTextWithId.pressShortcut(KeyStroke.getInstance(keyStrokeAsString));
		} catch (ParseException e) {
			LOGGER.error("Key " + keyStrokeAsString + " couldn't be paresd!", e);
			return e.getMessage();
		} catch (WidgetNotFoundException e) {
			LOGGER.error("Widget not found " + id, e);
			analyzeWidgets();
			return e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Expands the Three to the given items. e.g [DemoWebTests, LoginSuite]
	 * 
	 * @param nodes
	 *            of the Tree to be expanded.
	 * @return SWTBot Message
	 */
	public String expandTreeItems(String[] nodes) {
		try {
			if (LOGGER.isTraceEnabled()) {
				for (String node : nodes) {
					LOGGER.trace("expandTreeItems node: " + node);
				}
			}
			if (nodes[0].startsWith("ID")) {
				String treeId = nodes[0].split(":")[1];
				LOGGER.trace("selecting tree with id: " + treeId);
				SWTBotTreeItem expandNode = bot.treeWithId(treeId).expandNode(
						Arrays.copyOfRange(nodes, 1, nodes.length));
				expandNode.select();
			} else {
				SWTBotTreeItem expandNode = bot.tree().expandNode(nodes);
				expandNode.select();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage(), e);
			LOGGER.trace(analyzeWidgets());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * Selects the line of a table.
	 * 
	 * @param index
	 *            to be selected.
	 * @return SWTBot message of the Action. True on success and an Error
	 *         message with the Exception Message of the SWTBot on error.
	 */
	public String selectTableAtIndex(String index) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("selectTableItems items: " + index);
			}
			bot.table().select(Integer.parseInt(index));
		} catch (Exception e) {
			LOGGER.error("ERROR Selecting Table items: " + e.getMessage(), e);
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * 
	 * @param row
	 *            of the cell
	 * @param column
	 *            of the cell
	 * @param value
	 *            to be compared to
	 * @return true if the value is equals to the cell content.
	 */
	public String compareTableCellContentAt(String row, String column, String value) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("compareTableCellContentAt  " + row + "x" + column + " with " + value);
			}
			if (bot.table().cell(0, 0).equals(value)) {
				return Boolean.toString(true);
			} else {
				return Boolean.toString(false);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR Selecting Table items: " + e.getMessage(), e);
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * Enters some text in a textfield. e.g
	 * bot.textWithId("new.test.page.name").setText("TEST_NAME");
	 * 
	 * @param id
	 *            locator
	 * @param text
	 *            text for input in textfield
	 * @return SWTBot Message
	 */
	public String setTextById(String id, String text) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("setTextById: id=" + id + " text=" + text);
			}
			SWTBotText textWithId = null;
			try {
				textWithId = bot.textWithId(id);
			} catch (Exception e) {
				LOGGER.error("Widget by id: " + id + " not found: ", e);
			}
			if (textWithId != null) {
				bot.textWithId(id).setFocus();
				bot.textWithId(id).setText(text);
			} else {
				LOGGER.trace("Trying styled text Widget by id: " + id + " not found: ");
				SWTBotStyledText styledText = null;
				try {
					styledText = bot.styledTextWithId(id);
					styledText.setFocus();
					styledText.setText(text);
				} catch (Exception e) {
					LOGGER.error("Widget by id: " + id + " not found: ");
				}
				if (styledText == null) {
					LOGGER.trace("Trying comboBox Widget by id: " + id);
					SWTBotCombo combo = bot.comboBoxWithId(id);
					combo.setFocus();
					combo.typeText(text);
				}
			}
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("setTextById: text was set");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage(), e);
			LOGGER.error(analyzeWidgets());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * compares the text in the textfield identified by the id with the text in
	 * the parameter comptext.
	 * 
	 * @param id
	 *            locator
	 * @param comptext
	 *            to compare with the text in the textfield.
	 * @return SWTBot Message
	 */
	public String compareTextById(String id, String comptext) {

		try {
			LOGGER.trace("compareTextById: id=" + id + " comptext=" + comptext);
			SWTBotText textWithId = null;
			try {
				textWithId = bot.textWithId(id);
			} catch (Exception e) {
				LOGGER.error("Widget by id: " + id + " not found: ", e);
			}
			if (textWithId != null) {
				LOGGER.trace("text " + bot.textWithId(id).getText() + " in widget with id " + id);
				return Boolean.valueOf(bot.textWithId(id).getText().equalsIgnoreCase(comptext)).toString();
			} else {
				return Boolean.valueOf(bot.styledTextWithId(id).getText().equalsIgnoreCase(comptext)).toString();
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

	}

	/**
	 * compares the text in the labelfield identified by the id with the text in
	 * the parameter comptext.
	 * 
	 * @param id
	 *            locator
	 * @param comptext
	 *            to compare with the text in the textfield.
	 * @return SWTBot Message
	 */
	private String compareLabelById(String id, String comptext) {

		try {
			LOGGER.trace("compareLableById: id=" + id + " comptext=" + comptext);
			SWTBotLabel labelWithId = null;
			try {
				labelWithId = bot.labelWithId(id);
				if (labelWithId != null) {
					LOGGER.trace("text " + bot.labelWithId(id).getText() + " in widget with id " + id);
					return Boolean.valueOf(bot.labelWithId(id).getText().equalsIgnoreCase(comptext)).toString();
				}
			} catch (Exception e) {
				LOGGER.error("Widget by id: " + id + " not found: ", e);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(false);

	}

	/**
	 * select a line in SWTStyledText with the given lineNumber.
	 * 
	 * @param id
	 *            identifier of the widget
	 * @param lineNumber
	 *            number of the line
	 * @return "true", if the line is selected, else an error
	 */
	public String selectLineInText(String id, String lineNumber) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("selectLineInText: id=" + id + " lineNumber=" + lineNumber);
			}
			bot.styledTextWithId(id).selectLine(new Integer(lineNumber).intValue());
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("selectLine: line with number: " + lineNumber + " is selected.");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * sets the cursor at the position in the line identified with the parameter
	 * content in the styled-text with the id and marks the line from the
	 * position to the end of the line.
	 * 
	 * @param id
	 *            id to identify the styled-text
	 * @param content
	 *            the part of the text search for
	 * @param position
	 *            of the cursor in the line
	 * @return true, if the text was found, else false
	 */
	private String setCursorInTextWithContentsAtPosition(String id, String content, String position) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("selectLineInText: id=" + id + " content=" + content + " pos " + position);
			}
			SWTBotStyledText styledTextWithId = bot.styledTextWithId(id);
			int lineNumber = 0;
			for (String line : styledTextWithId.getLines()) {
				if (line.contains(content)) {
					styledTextWithId.selectRange(lineNumber, Integer.valueOf(position),
							line.length() - Integer.valueOf(position));
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("selectLine: " + line + " with number: " + lineNumber + " and selectedText "
								+ styledTextWithId.getSelection() + " is selected.");
					}
					break;
				}
				lineNumber++;
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * checks the existence of a text in the styled-text identified by the
	 * identifier.
	 * 
	 * @param id
	 *            id to identify the styled-text
	 * @param content
	 *            the searched text
	 * @return true, if the text is on the widget, else false
	 */
	public String checkTextExistInWidgets(String id, String content) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("checkTextExistInWidgets: id=" + id + " content=" + content);
			}
			SWTBotStyledText styledTextWithId = bot.styledTextWithId(id);

			return Boolean.valueOf(styledTextWithId.getText().contains(content)).toString();

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * Clicks a menu. the menu is identified by the name. e.g
	 * bot.menu("Datei").click();
	 * 
	 * @param menuName
	 *            to be clicked.
	 * @return state of the ui action.
	 */
	public String clickMenuByName(String menuName) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickMenuByName ....");
			}
			clickMenuByRegEx(menuName);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickMenuByName " + menuName + " clicked");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage() + " menuName: " + menuName);
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * clicks the menu by searching with a regular-expression.
	 * 
	 * @param regex
	 *            the regular-expression.
	 * @return "true", if the menu was found, else an Error
	 */
	@SuppressWarnings("unchecked")
	private String clickMenuByRegEx(final String regex) {
		try {

			LOGGER.trace("clickMenuByRegEx regex: " + regex);

			Matcher<MenuItem> withMnemonic = withRegex(regex);
			final Matcher<MenuItem> matcher = allOf(widgetOfType(MenuItem.class), withMnemonic);

			final MenuFinder menuFinder = new MenuFinder();

			try {
				bot.waitUntil(new DefaultCondition() {
					@Override
					public String getFailureMessage() {
						return "Could not find menu with text: " + regex; //$NON-NLS-1$
					}

					@Override
					public boolean test() throws Exception {
						return !menuFinder.findMenus(matcher).isEmpty();
					}
				});
			} catch (TimeoutException e) {
				LOGGER.error("ERROR timeout: " + e.getMessage());
				return "ERROR timeout: " + e.getMessage();
			}

			List<MenuItem> findMenus = menuFinder.findMenus(matcher);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("findMenus.size(): " + findMenus.size());
				for (MenuItem menuItem : findMenus) {
					LOGGER.trace("menuItem: " + new SWTBotMenu(menuItem).getText());
				}
			}

			if (findMenus.isEmpty()) {
				return "ERROR Coundn't find menu item for: " + regex;
			}

			MenuItem menuItem = findMenus.get(0);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("I am clicking on menuItem " + new SWTBotMenu(menuItem).getText());
			}

			try {
				new SWTBotMenu(menuItem).click();
			} catch (Exception e) {
				LOGGER.error("foo", e);
			}

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("menuItem clicked");
			}

		} catch (Exception e) {
			LOGGER.error("foo", e);
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);

	}

	/**
	 * Clicks a menu. the menu is identified by the name. e.g
	 * bot.menu("Datei").click();
	 * 
	 * @param menuId
	 *            of the menu to be clicked.
	 * @return state of the ui action.
	 */
	public String clickMenuById(String menuId) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickMenuById ....");
			}
			bot.menuWithId(menuId).click();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickMenuById " + menuId + " clicked");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage() + " menuId: " + menuId);
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * Generic method for click button by different locator types.
	 * 
	 * TYPE {ID, TEXT, REGEX, INDEX}
	 * 
	 * @param locatorWithType
	 *            Example for locator with ID: "ID@dialog.text.4711" Example for
	 *            locator with TEXT: "TEXT@OK" Example for locator with REGEX:
	 *            "REGEX@.*Hin.*zu.*gen.* Example for locator with INDEX:
	 *            "INDEX@0"
	 * 
	 * @return message of the SWT Bot
	 */
	public String clickButton(String locatorWithType) {

		String[] split = locatorWithType.split("::");
		String locatorType = split[0];
		String locator = split[1];

		if (locatorType.equals("ID")) {
			return clickButtonById(locator);
		} else if (locatorType.equals("TEXT")) {
			return clickButtonByText(locator);
		} else if (locatorType.equals("REGEX")) {
			return clickButtonByRegEx(locator);
		} else if (locatorType.equals("INDEX")) {
			return clickButtonByIndex(locator);
		}

		return Boolean.toString(true);

	}

	/**
	 * e.g. bot.button(0).click();
	 * 
	 * @param locator
	 *            of the button
	 * 
	 * @return true, if the button is clicked.
	 */
	private String clickButtonByIndex(String locator) {

		String[] split = locator.split("@");
		String text = split[0];
		int index = Integer.parseInt(split[1]);

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonByIndex text: " + text + " index: " + index);
			}
			bot.button(text, index).click();

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);

	}

	/**
	 * Clicks the button by text.
	 * 
	 * @param text
	 *            on the button
	 * @return true, if the button is clicked.
	 */
	private String clickButtonByText(String text) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonByText " + text + " ...");
			}
			SWTBotButton button = bot.button(text);
			LOGGER.trace("Found button " + button);
			try {
				// There is a Try catch for index out bound exeption, to ignore
				// the event notification error in swtbot.
				button.click();
			} catch (IndexOutOfBoundsException e) {
				LOGGER.trace("SWTBot event notification error catched. Operation was successfull.");
			}
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonByText " + text + " clicked");
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			StringBuffer sb = new StringBuffer();
			sb.append("ERROR ").append(e.getMessage());
			StackTraceElement[] trace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : trace) {
				sb.append("\n").append(stackTraceElement.toString());
			}
			return sb.toString();
		}
		return Boolean.toString(true);

	}

	/**
	 * The speed of playback in milliseconds. Defaults to 0.
	 * 
	 * @param milliseconds
	 *            for waiting
	 * 
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	public String setPlayBackTime(String milliseconds) {

		try {
			SWTBotPreferences.PLAYBACK_DELAY = Long.parseLong(milliseconds);
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Clicks the button by text.
	 * 
	 * @param locator
	 *            of the button.
	 * 
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	private String clickButtonById(String locator) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonById locator:" + locator);
			}
			bot.buttonWithId(locator).click();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);

	}

	/**
	 * Click the check box.
	 * 
	 * @param locator
	 *            of the check box (prefix is ID:: or TEXT:: or nothing).
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	public String clickCheckBox(String locator) {
		String[] split = locator.split("::");

		if (split.length == 1) {
			return clickCheckBoxById(locator);
		} else if (split.length == 2) {
			String locatorType = split[0];
			locator = split[1];

			if (locatorType.equals("ID")) {
				return clickCheckBoxById(locator);
			} else if (locatorType.equals("TEXT")) {
				return clickCheckBoxByText(locator);
			}
		} else {
			return "ERROR wrong locater type (use ID:: or TEXT:: or nothing as prefix)";
		}

		return Boolean.toString(true);

	}

	/**
	 * Click the check box by id.
	 * 
	 * @param locator
	 *            of the check box.
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	private String clickCheckBoxById(String locator) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickCheckBoxById locator:" + locator);
			}
			bot.checkBoxWithId(locator).click();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * Click the check box by label (text).
	 * 
	 * @param label
	 *            text or label of the check box
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	private String clickCheckBoxByText(String label) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickCheckBoxById locator:" + label);
			}
			bot.checkBoxWithLabel(label).click();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * Looks if check box is present and checked.
	 * 
	 * @param locator
	 *            of the check box.
	 * @return true, if the check box is found and checked, false otherwise
	 */
	public String isCheckBoxChecked(String locator) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isCheckBoxChecked locator:" + locator);
			}
			return Boolean.valueOf(bot.checkBoxWithId(locator).isChecked()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * Looks if check box is present and enabled.
	 * 
	 * @param locator
	 *            of the check box.
	 * @return true, if the check box is found and enabled, false otherwise
	 */
	public String isCheckBoxEnabled(String locator) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isCheckBoxEnabled locator:" + locator);
			}
			return Boolean.valueOf(bot.checkBoxWithId(locator).isEnabled()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * Clicks the button by text.
	 * 
	 * @param regEx
	 *            to find the ui element.
	 * 
	 * @return done on success otherwise an error with the swtbot exception.
	 */
	@SuppressWarnings("unchecked")
	private String clickButtonByRegEx(String regEx) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonByRegEx regEx: " + regEx);
			}
			Widget widget = bot.widget(allOf(widgetOfType(Button.class), anyOf(withRegex(regEx))));
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickButtonByRegEx widget: " + widget);
			}
			new SWTBotButton((Button) widget).click();

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);

	}

	/**
	 * Set given text in styledtext widget found by id.
	 * 
	 * @param locator
	 *            id of styledtext widget
	 * @param text
	 *            text to be typed in widget
	 * @return "done" or exception message text
	 */
	private String setStyledTextWithId(String locator, String text) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("setStyledTextWithId locator: " + locator + " text: " + text);
			}
			bot.styledTextWithId(locator).setText(text);
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Selects item in combobox with given text. combobox widget will be found
	 * by id.
	 * 
	 * @param locator
	 *            id of widget
	 * @param text
	 *            text to be selected in widget
	 * @return "done" or exception message text
	 */
	private String selectComboBoxWithId(String locator, String text) {

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("selectComboBoxWithId locator: " + locator + " text: " + text);
			}
			String replacedText = replaceUmlaute(text);

			String[] items = bot.comboBoxWithId(locator).items();

			for (int i = 0; i < items.length; i++) {
				String item = items[i];
				LOGGER.trace("Items: " + item);

				String replacedItem = replaceUmlaute(item);
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("replacedText: " + replacedText + " replacedItem: " + replacedItem);
				}
				if (replacedText.equals(replacedItem)) {

					bot.comboBoxWithId(locator).setSelection(i);
				}
			}

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Replace all mutated vowel in given text.
	 * 
	 * @param text
	 *            text for replace mutated vowel
	 * @return new text without mutated vowel
	 */
	private String replaceUmlaute(String text) {
		String pattern = "[^0-9a-zA-Z]";
		return text.replaceAll(pattern, "");
	}

	/**
	 * Returns true if given node is selected.
	 * 
	 * @param treeNodeText
	 *            node to be checked if selected
	 * @return "true" if selected, otherwise "false"
	 */
	private String checkSelectedTreeNode(final String treeNodeText) {

		result = Boolean.toString(false);

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("treeNodeText: " + treeNodeText);
			}

			// out.println("checkSelectedTreeNode treeNodeText: " +
			// treeNodeText);

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Tree tree = bot.tree().widget;

					TreeItem[] selection = tree.getSelection();
					for (TreeItem treeItem : selection) {
						if (treeItem.getText().equals(treeNodeText)) {
							result = Boolean.toString(true);
							break;
						}
					}
				}
			};

			Display.getDefault().syncExec(runnable);

			new Thread(runnable).join();
			waitSeconds("1");

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			// return "error " + e.getMessage();
		}

		return result;
	}

	/**
	 * Interrupts the Thread and waits for given time in seconds.
	 * 
	 * @param seconds
	 *            wait time in seconds
	 * @return exception message text or "done"
	 */
	private String waitSeconds(String seconds) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("waitSeconds: " + seconds);
			}
			Thread.sleep(Long.parseLong(seconds) * 1000);
		} catch (NumberFormatException e) {
			LOGGER.error("Can not parse second value as number " + e.getMessage());
			return "ERROR " + e.getMessage();
		} catch (InterruptedException e) {
			LOGGER.error("Wait step was interrupted " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
		return Boolean.toString(true);
	}

	/**
	 * Clicks toolbarbutton for given locator. Locator is an id and must be
	 * unique.
	 * 
	 * @param locator
	 *            id of widget
	 * @return exception message text or "done"
	 */
	private String clickToolbarButtonWithId(String locator) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickToolbarButtonWithId locator: " + locator);
			}
			bot.toolbarButtonWithId(locator);

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Clicks Toolbarbutton by tooltip.
	 * 
	 * @param tooltip
	 *            given tooltip text
	 * @return @return exception message text or "done"
	 */
	private String clickToolbarButtonWithTooltip(String tooltip) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("clickToolbarButtonWithTooltip tooltip: " + tooltip);
			}
			bot.toolbarButtonWithTooltip(tooltip);

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(true);
	}

	/**
	 * Method for debugging the AUT. A first implementation of an widget
	 * scanner. all widget s on current SWT-App will be printed with their id's
	 * in the swtbot.log with the trace-level. This Method is in work and can be
	 * extended and modified step by step.
	 * 
	 * @return message
	 */
	private String analyzeWidgets() {

		Level oldLevel = LOGGER.getLevel();
		LOGGER.setLevel(Level.TRACE);
		LOGGER.trace("analyzeWidgets start");
		LOGGER.trace("---------------------------------------------");

		Display.getDefault().syncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {

				try {
					List<? extends Widget> widgets = bot.widgets(allOf(widgetOfType(Widget.class)));

					StringBuilder sb = new StringBuilder();

					for (Widget widget : widgets) {

						sb.append("widgetId: " + widget.getData("org.eclipse.swtbot.widget.key"));
						sb.append("widgetClass: " + widget.getClass().getSimpleName());
						try {
							Method[] methods = widget.getClass().getMethods();
							boolean foundGetText = false;
							for (Method method : methods) {
								if (method.getName().equals("getText")) {
									foundGetText = true;
								}
							}
							if (foundGetText) {

								Method method = widget.getClass().getMethod("getText", new Class[] {});
								sb.append("\n >>> text value: " + method.invoke(widget, new Object[] {}));
							}
						} catch (Exception e) {
							LOGGER.error(">>>>>>> no tesxxt", e);
						}
						sb.append(" widget: " + widget).append("\n");
					}

					Level oldLevel = LOGGER.getLevel();
					LOGGER.setLevel(Level.TRACE);
					LOGGER.trace(sb.toString());
					LOGGER.setLevel(oldLevel);

				} catch (Exception e) {
					LOGGER.error("ERROR " + e.getMessage());
				}

			}
		});
		LOGGER.trace("analyzeWidgets end");
		LOGGER.trace("---------------------------------------------");
		LOGGER.setLevel(oldLevel);

		return Boolean.toString(true);

	}

	/**
	 * Checks for widget text and returns true if text was found.
	 * 
	 * @param text
	 *            the searched text
	 * @return result
	 */
	public String checkTextForAllWidgets(final String text) {
		final Boolean[] returnValue = new Boolean[] { false };
		Display.getDefault().syncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {

				try {
					List<? extends Widget> widgets = bot.widgets(allOf(widgetOfType(Widget.class)));

					for (Widget widget : widgets) {
						try {
							Method[] methods = widget.getClass().getMethods();
							boolean foundGetText = false;
							for (Method method : methods) {
								if (method.getName().equals("getText")) {
									foundGetText = true;
								}
							}
							if (foundGetText) {
								Method method = widget.getClass().getMethod("getText", new Class[] {});
								String textValue = (String) method.invoke(widget, new Object[] {});
								if (text.trim().equals(textValue.trim())) {
									returnValue[0] = true;
								}
							} else {
								LOGGER.trace("Widget: " + widget + " has no text attribute");
							}

						} catch (Exception e) {
							LOGGER.error("No text for the widget " + widget + "found.", e);
						}
					}
				} catch (Exception e) {
					LOGGER.error("ERROR " + e.getMessage());
				}
			}
		});

		return returnValue[0].toString();
	}

	/**
	 * closes a tab with the given name.
	 * 
	 * @param name
	 *            name of the tab
	 * @return true, if the tab is found and closed, else an Error
	 */
	public String closeTabItemWithName(String name) {
		SWTBotCTabItem cTabItem = bot.cTabItem(name);
		if (cTabItem == null) {
			return "ERROR the tab with the name " + name + " isn't found!";
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Found the tab with the name " + name + "!");
		}
		if (cTabItem.close() != null) {
			return Boolean.toString(true);
		}
		return Boolean.toString(false);
	}

	/**
	 * 
	 * @return true if the application is launched.
	 */
	public boolean isLaunched() {
		return launched;
	}

	/**
	 * Starts the Serversocket an listen for testclient. Commands will be
	 * recieved and SWT-Bot calls will be invoked.
	 * 
	 */
	@Override
	public void run() {
		ServerSocket listener;
		try {
			listener = new ServerSocket(SERVER_PORT);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Server startet on Port: " + SERVER_PORT);
			}

			try {
				while (!isInterrupted()) {
					Socket socket = listener.accept();
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

						String command = in.readLine();

						LOGGER.trace("Dispatching command: " + command);

						if (command != null) {

							out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

							if (command.equals("isLaunched")) {
								out.println("" + isLaunched());
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace("" + isLaunched());
								}
							} else if (command.equals("stop")) {
								LOGGER.info("STOP Command retrived.");
								stopApplication();
							}

							String methodName = command.split(";")[0];

							if (methodName.equals("expandTreeItems")) {
								String[] splitCommand = command.split(";");
								String[] nodes = splitCommand[1].split(",");
								out.println(expandTreeItems(nodes));
							}
							if (methodName.equals("selectTableAtIndex")) {
								String[] splitCommand = command.split(";");
								String index = splitCommand[1];
								out.println(selectTableAtIndex(index));
							}
							if (methodName.equals("clickContextMenu")) {
								String[] splitCommand = command.split(";");
								out.println(clickContextMenu(splitCommand[1]));
							} else if (methodName.equals("clickMenuByName")) {
								String[] splitCommand = command.split(";");
								String result = clickMenuByName(splitCommand[1]);
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace("result of clickMenuByName: " + result);
								}
								out.println(Boolean.valueOf(result));
							} else if (methodName.equals("clickMenuById")) {
								String[] splitCommand = command.split(";");
								String result = clickMenuById(splitCommand[1]);
								if (LOGGER.isTraceEnabled()) {
									LOGGER.trace("result of clickMenuById: " + result);
								}
								out.println(Boolean.valueOf(result));
							} else if (methodName.equals("clickButton")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								out.println(clickButton(locator));

							} else if (methodName.equals("clickCheckBox")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								out.println(clickCheckBox(locator));
							} else if (methodName.equals("isCheckBoxEnabled")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								out.println(isCheckBoxEnabled(locator));
							} else if (methodName.equals("isCheckBoxChecked")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								out.println(isCheckBoxChecked(locator));
							} else if (methodName.equals("checkTextForAllWidgets")) {
								String[] splitCommand = command.split(";");
								String text = splitCommand[1];
								out.println(checkTextForAllWidgets(text));

							} else if (methodName.equals("clickToolbarButtonWithId")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								out.println(clickToolbarButtonWithId(locator));
							} else if (methodName.equals("clickToolbarButtonWithTooltip")) {
								String[] splitCommand = command.split(";");
								String tooltip = splitCommand[1];
								out.println(clickToolbarButtonWithTooltip(tooltip));
							} else if (methodName.equals("checkSelectedTreeNode")) {
								String[] splitCommand = command.split(";");
								String treeNodeText = splitCommand[1];
								out.println(checkSelectedTreeNode(treeNodeText));
							} else if (methodName.equals("setTextById")) {
								String[] splitCommand = command.split(";");
								out.println(setTextById(splitCommand[1], splitCommand[2]));
							} else if (methodName.equals("setStyledTextWithId")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String text = splitCommand[2];
								out.println(setStyledTextWithId(locator, text));
							} else if (methodName.equals("setPlayBackTime")) {
								String[] splitCommand = command.split(";");
								String playBackTime = splitCommand[1];
								out.println(setPlayBackTime(playBackTime));
							} else if (methodName.equals("waitSeconds")) {
								String[] splitCommand = command.split(";");
								String seconds = splitCommand[1];
								out.println(waitSeconds(seconds));
							} else if (methodName.equals("selectComboBoxWithId")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String text = splitCommand[2];
								out.println(selectComboBoxWithId(locator, text));
							} else if (methodName.equals("analyzeWidgets")) {
								out.println(analyzeWidgets());
							} else if (methodName.equals("selectLineInText")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String lineNumber = splitCommand[2];
								out.println(selectLineInText(locator, lineNumber));
							} else if (methodName.equals("setCursorInTextWithContentsAtPosition")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String content = splitCommand[2];
								String position = splitCommand[3];
								out.println(setCursorInTextWithContentsAtPosition(locator, content, position));
							} else if (methodName.equals("checkTextExistInWidgets")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String text = splitCommand[2];
								out.println(checkTextExistInWidgets(locator, text));
							} else if (methodName.equals("compareTextById")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String comptext = splitCommand[2];
								out.println(compareTextById(locator, comptext));
							} else if (methodName.equals("closeTabItemWithName")) {
								String[] splitCommand = command.split(";");
								String name = splitCommand[1];
								out.println(closeTabItemWithName(name));
							} else if (methodName.equals("pressShortcutWithModificationKeyOfStyledText")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String modificationKeys = splitCommand[2];
								char key = splitCommand[3].toCharArray()[0];
								out.println(pressShortcutOfStyledText(locator, modificationKeys, key));
							} else if (methodName.equals("pressShortcutOfStyledText")) {
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String keyStrokeAsString = splitCommand[2];
								out.println(pressShortcutOfStyledText(locator, keyStrokeAsString));
							} else if (methodName.equals("readAllProjectsInTree")) {
								out.println(readAllProjectsInTree());
							} else if (methodName.equals("deleteAllProjects")) {
								out.println(deleteAllProjects());
							} else if (methodName.equals("countProjectsEquals")) {
								String[] splitCommand = command.split(";");
								String expectedCount = splitCommand[1];
								out.println(countProjectsEquals(expectedCount));
							} else if (methodName.equals("countChildrenEquals")) {
								String[] splitCommand = command.split(";");
								String expectedCount = splitCommand[2];
								String[] nodes = splitCommand[1].split(",");
								out.println(countChildrenEquals(nodes, expectedCount));
							} else if (methodName.equals("isButtonEnabled")) {
								String[] splitCommand = command.split(";");
								String locatorWithType = splitCommand[1];
								out.println(isButtonEnabled(locatorWithType));
							} else if (methodName.equals("textIsVisible")) {
								String[] splitCommand = command.split(";");
								String text = splitCommand[1];
								out.println(textIsVisible(text));
							} else if (methodName.equals("compareTextInStyledById")) {
								LOGGER.info("compareTextInStyledById");
								LOGGER.info("command");
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String comptext = splitCommand[2];
								out.println(compareTextInStyledById(locator, comptext));
							} else if (methodName.equals("compareLabelById")) {
								LOGGER.info("compareLabelById");
								LOGGER.info(command);
								String[] splitCommand = command.split(";");
								String locator = splitCommand[1];
								String comptext = splitCommand[2];
								out.println(compareLabelById(locator, comptext));
							} else {
								out.println("command: [" + command + "] unknown !");
							}
						}
					} finally {
						socket.close();
					}
				}
			} finally {
				listener.close();
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}

	/**
	 * checks, if the text is visible or not.
	 * 
	 * @param text
	 *            the text as a String.
	 * @return true, if the text is visible
	 */
	@SuppressWarnings("unchecked")
	private String textIsVisible(String text) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("textIsVisible: " + text);
		}
		List<? extends Widget> widgets = bot.widgets(allOf(widgetOfType(Widget.class)));

		StringBuilder sb = new StringBuilder();

		for (Widget widget : widgets) {
			sb.append("widgetId: " + widget.getData("org.eclipse.swtbot.widget.key"));
			sb.append(" widgetClass: " + widget.getClass().getSimpleName());
			sb.append(" widget: " + widget).append("\n");
			if (widget instanceof StyledText) {
				sb.append(" content: " + ((StyledText) widget).getText());
			}
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("textIsVisible: content " + sb.toString());
		}
		return Boolean.valueOf(sb.toString().contains(text)).toString();
	}

	/**
	 * 
	 * @param locator
	 *            the locator of the widget.
	 * @param searched
	 *            the internal text or a part
	 * @return ture, if the searched text is in the styledText.
	 */
	private String compareTextInStyledById(String locator, String searched) {
		boolean compResult = false;
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("compareTextInStyledById locator: " + locator + " text: " + searched);
			}
			compResult = bot.styledTextWithId(locator).getText().contains(searched);
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(compResult);

	}

	/**
	 * Generic method for isButtonEnabled by different locator types.
	 * 
	 * TYPE {ID, TEXT, REGEX, INDEX}
	 * 
	 * @param locatorWithType
	 *            Example for locator with ID: "ID@dialog.text.4711" Example for
	 *            locator with TEXT: "TEXT@OK" Example for locator with REGEX:
	 *            "REGEX@.*Hin.*zu.*gen.* Example for locator with INDEX:
	 *            "INDEX@0"
	 * 
	 * @return message of the SWT Bot
	 */
	public String isButtonEnabled(String locatorWithType) {

		String[] split = locatorWithType.split("::");
		String locatorType = split[0];
		String locator = split[1];

		if (locatorType.equals("ID")) {
			return isButtonEnabledById(locator);
		} else if (locatorType.equals("TEXT")) {
			return isButtonEnabledByText(locator);
		} else if (locatorType.equals("REGEX")) {
			return isButtonEnabledByRegEx(locator);
		} else if (locatorType.equals("INDEX")) {
			return isButtonEnabledByIndex(locator);
		}
		return Boolean.toString(true);
	}

	/**
	 * looks for a button and checks enabled or disabled.
	 * 
	 * @param locator
	 *            the locator
	 * @return true, if the button is found and enabled, else false
	 */
	private String isButtonEnabledByIndex(String locator) {
		String[] split = locator.split("@");
		String text = split[0];
		int index = Integer.parseInt(split[1]);

		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isButtonEnabledByIndex text: " + text + " index: " + index);
				LOGGER.trace("Result of isButtonEnabledByIndex: " + bot.button(text, index).isEnabled());

			}
			return Boolean.valueOf(bot.button(text, index).isEnabled()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			analyzeWidgets();
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * 
	 * looks for a button and checks enabled or disabled.
	 * 
	 * @param regEx
	 *            to find the ui element.
	 * @return true, if the button is found and enabled, else false
	 */
	@SuppressWarnings("unchecked")
	private String isButtonEnabledByRegEx(String regEx) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isButtonEnabledByRegEx regEx: " + regEx);
			}
			Widget widget = bot.widget(allOf(widgetOfType(Button.class), anyOf(withRegex(regEx))));
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isButtonEnabledByRegEx widget: " + widget);
				LOGGER.trace("Result of isButtonEnabledByRegEx: " + new SWTBotButton((Button) widget).isEnabled());

			}
			return Boolean.valueOf(new SWTBotButton((Button) widget).isEnabled()).toString();

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * looks for a button and checks enabled or disabled.
	 * 
	 * @param text
	 *            on the button to find the ui-element
	 * @return true, if the button is found and enabled, else false
	 */
	private String isButtonEnabledByText(String text) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isButtonEnabledByText " + text + " ...");
				LOGGER.trace("Result of isButtonEnabledByText: " + bot.button(text).isEnabled());
			}
			return Boolean.valueOf(bot.button(text).isEnabled()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * looks for a button and checks enabled or disabled.
	 * 
	 * @param locator
	 *            to find the ui-element
	 * @return true, if the button is found and enabled, else false
	 */
	private String isButtonEnabledById(String locator) {
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("isButtonEnabledById locator:" + locator);
				LOGGER.trace("Result of isButtonEnabledById: " + bot.buttonWithId(locator).isEnabled());
			}
			return Boolean.valueOf(bot.buttonWithId(locator).isEnabled()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			analyzeWidgets();
			return "ERROR " + e.getMessage();
		}
	}

}
