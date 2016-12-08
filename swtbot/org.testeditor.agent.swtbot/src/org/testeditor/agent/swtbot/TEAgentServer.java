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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuFinder;
import org.eclipse.swtbot.swt.finder.finders.MenuFinder;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
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

	private String testname;

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
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Display.getDefault().getShells()[0].forceActive();
				}
			});
		}
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
		styledTextWithId.pressShortcut(Integer.parseInt(modificationKeys), key);

		return Boolean.toString(true);
	}

	/**
	 * Presses the shortcut specified by the given keys on the active window.
	 * 
	 * @param modificationKeys
	 *            the combination of SWT.ALT | SWT.CTRL | SWT.SHIFT |
	 *            SWT.COMMAND.
	 * @param key
	 *            the character
	 * @return true, after sending the keys
	 */
	public String pressGlobalShortcut(final String modificationKeys, final char key) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("modificationKeys: " + modificationKeys);
			LOGGER.trace("key: " + key);
		}
		bot.activeShell().pressShortcut(Integer.parseInt(modificationKeys), key);
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
		SWTBotTree tree = getTestExplorer();
		if (tree == null) {
			return Boolean.toString(false);
		}
		while (tree.getAllItems().length > 0) {
			SWTBotTreeItem[] allItems = tree.getAllItems();
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
	 * Searches for the TEstexplorer tree by ID with one retry. After the retry,
	 * it will report all visible widgets to the log file.
	 * 
	 * @return testExplorer tree object.
	 */
	public SWTBotTree getTestExplorer() {
		SWTBotTree tree = null;
		try {
			try {
				tree = bot.treeWithId("testexplorer.tree");
			} catch (Exception e) {
				// Try again.
				Thread.sleep(100);
				tree = bot.treeWithId("testexplorer.tree");
			}
		} catch (Exception e) {
			LOGGER.error("can't count widgets ", e);
			analyzeWidgets();
		}
		return tree;
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
		SWTBotTree tree = getTestExplorer();
		if (tree == null) {
			return Boolean.toString(false);
		}
		SWTBotTreeItem[] allItems = tree.getAllItems();
		if (allItems.length != Integer.parseInt(expectedCount)) {
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
		if (expandNode.getItems().length != Integer.parseInt(expectedCount)) {
			String message = "Inspected count of projects was: " + expectedCount + " but there are "
					+ expandNode.getItems().length + " projects";
			LOGGER.error(message);
			return message;
		}
		return Boolean.toString(true);
	}

	/**
	 * Compare the count of a list with expected count.
	 * 
	 * @param locator
	 *            locator id of the widget with items.
	 * @param expectedCount
	 *            count of items in the widget.
	 * @return true if the amount of items equals the expectedCount.
	 */
	public String countItemsEquals(String locator, String expectedCount) {
		SWTBotTable table = bot.tableWithId(locator);
		if (table.rowCount() != Integer.parseInt(expectedCount)) {
			String message = "Inspected count of items was: " + expectedCount + " but there are " + table.rowCount()
					+ " items";
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
			UIThreadRunnable.syncExec(new VoidResult() {

				@Override
				public void run() {
					Shell[] shells = Display.getDefault().getShells();
					LOGGER.trace(">>>>>> Shell count: " + shells.length);
					shells[shells.length - 1].forceActive();
				}
			});
			if (nodes[0].startsWith("ID")) {
				String treeId = nodes[0].split(":")[1];
				LOGGER.trace("selecting tree with id: " + treeId);
				SWTBotTreeItem expandNode = bot.treeWithId(treeId)
						.expandNode(Arrays.copyOfRange(nodes, 1, nodes.length));
				expandNode.select();
			} else {
				SWTBotTree tree = getTestExplorer();
				if (tree == null) {
					return Boolean.toString(false);
				}
				SWTBotTreeItem expandNode = tree.expandNode(nodes);
				expandNode.select();
			}
			bot.tree().setFocus();
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
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("setTextById: id=" + id + " text=" + text);
		}
		Widget widget = bot.widget(WidgetMatcherFactory.withId(id));
		if (widget != null) {
			boolean found = false;
			if (widget instanceof Combo) {
				SWTBotCombo combo = bot.comboBoxWithId(id);
				combo.setFocus();
				combo.typeText(text);
				found = true;
			}
			if (widget instanceof StyledText) {
				SWTBotStyledText styledText = null;
				styledText = bot.styledTextWithId(id);
				styledText.setFocus();
				styledText.setText(text);
				found = true;
			}
			if (!found) {
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
					LOGGER.error(analyzeWidgets());
				}
			}
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
	public String compareLabelById(String id, String comptext) {

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
					styledTextWithId.selectRange(lineNumber, Integer.parseInt(position),
							line.length() - Integer.parseInt(position));
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
				SWTBotMenu menu = new SWTBotMenu(menuItem);
				if (menu.widget.isDisposed()) {
					LOGGER.warn("Menu is allready disposed. Check the Application state.");
				} else {
					menu.click();
				}
			} catch (Exception e) {
				LOGGER.error("Can't click on menu item: " + menuItem, e);
			}

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("menuItem clicked");
			}

		} catch (Exception e) {
			LOGGER.error("Unable to find and click on menu", e);
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
	 * 
	 * @param testname
	 *            of current running test.
	 * @return true
	 */
	public String setTestName(String testname) {
		this.testname = testname;
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
			SWTBotButton button = null;
			try {
				button = bot.button(text);
			} catch (Exception e) {
				LOGGER.warn("Button not found try again");
				UIThreadRunnable.syncExec(new VoidResult() {

					@Override
					public void run() {
						Shell[] shells = Display.getDefault().getShells();
						shells[shells.length - 1].forceActive();
					}
				});

				button = bot.button(text);
			}
			LOGGER.trace("Found button " + button);
			try {
				// There is a Try catch for index out bound exeption, to ignore
				// the event notification error in swtbot.
				button.click();
			} catch (IndexOutOfBoundsException e) {
				LOGGER.trace("SWTBot event notification error catched. Operation was successfull.");
			} catch (IllegalStateException e) {
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
			analyzeWidgets();
			return sb.toString();
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
			try {
				bot.buttonWithId(locator).click();
			} catch (WidgetNotFoundException e) {

				SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithId(locator);
				toolbarButton.click();
			}
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
	public String setStyledTextWithId(String locator, String text) {

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
	public String selectComboBoxWithId(String locator, String text) {

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
	 * Checks the expected row number of given Table.
	 * 
	 * @param locator
	 *            String
	 * @param expectedRowNumber
	 *            String
	 * @return true if expected value equals current value
	 */
	public String checkRowNumberOfTable(String locator, String expectedRowNumber) {

		try {

			LOGGER.trace("checkRowNumberOfTable locator: " + locator + " expectedRowNumber: " + expectedRowNumber);

			SWTBotTable table = bot.tableWithId(locator);

			if (table != null) {

				if (table.rowCount() == Integer.parseInt(expectedRowNumber)) {
					return Boolean.toString(true);
				} else {
					LOGGER.info("expectedRowNumber " + expectedRowNumber + " currentRowNumber " + table.rowCount());
				}

			} else {
				String message = "ERROR  Table with key " + locator + " not found !";
				LOGGER.error(message);
				return message;
			}

		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage());
			return "ERROR " + e.getMessage();
		}

		return Boolean.toString(false);
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
	public String checkSelectedTreeNode(final String treeNodeText) {

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
	public String clickToolbarButtonWithId(String locator) {
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
	public String clickToolbarButtonWithTooltip(String tooltip) {
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
		LOGGER.trace("analyzeWidgets start");
		LOGGER.trace("---------------------------------------------");

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {

				try {
					List<? extends Widget> widgets = bot.widgets(widgetOfType(Widget.class));

					StringBuilder sb = new StringBuilder();

					for (Widget widget : widgets) {

						if (widget instanceof Table) {
							sb.append("\n >>> Table gefunden mit " + ((Table) widget).getItems().length + " Zeilen !");
						}

						sb.append("widgetId: " + widget.getData("org.eclipse.swtbot.widget.key"));
						sb.append(" widgetClass: " + widget.getClass().getSimpleName());
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
							LOGGER.error(">>>>>>> no text", e);
						}
						sb.append(" widget: " + widget).append("\n");
					}

					LOGGER.trace(sb.toString());

				} catch (Exception e) {
					LOGGER.error("ERROR " + e.getMessage());
				}

			}
		});
		LOGGER.trace("analyzeWidgets end");
		LOGGER.trace("---------------------------------------------");

		return Boolean.toString(true);

	}

	/**
	 * Select a string in a auto complete widget.
	 * 
	 * @param item
	 *            that is selected
	 * @return true on success.
	 */
	public String selectElementInAtuocompleteWidget(final String item) {
		try {
			SWTBotShell shell = bot.shell("", bot.activeShell().widget);
			shell.activate();
			final Table table = (Table) bot.widget(WidgetMatcherFactory.widgetOfType(Table.class), shell.widget);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					TableItem[] items = table.getItems();
					TableItem workOn = null;
					for (int i = 0; i < items.length; i++) {
						TableItem tableItem = table.getItem(i);
						if (tableItem.getText().equals(item)) {
							workOn = tableItem;
							bot.table().click(i, 0);
						}
					}
					if (workOn != null) {
						Event event = new Event();
						event.type = SWT.Selection;
						event.widget = table;
						event.item = workOn;
						table.notifyListeners(SWT.Selection, event);
						table.notifyListeners(SWT.DefaultSelection, event);
					}
				}
			});
			return Boolean.toString(true);
		} catch (Exception e) {
			LOGGER.error("ERROR selecting " + item + " in autocomplete widget. " + e.getMessage(), e);
			return "ERROR selcting " + item + " in autocomplete widget. " + e.getMessage();
		}
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
	 * Starts the Serversocket and listen for testclient. Commands will be
	 * recieved and SWT-Bot calls will be invoked.
	 * 
	 */
	@Override
	public void run() {
		ServerSocket listener;
		boolean first = true;
		try {
			listener = new ServerSocket(SERVER_PORT);

			LOGGER.info("Server startet on Port: " + SERVER_PORT);

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

							if (first && Display.getDefault() != null && testname != null) {
								UIThreadRunnable.syncExec(new VoidResult() {

									@Override
									public void run() {
										Shell[] shells = Display.getDefault().getShells();
										if (shells.length > 0) {
											shells[shells.length - 1].forceActive();
											shells[shells.length - 1].setText("***** AUT running with TE-Agent for: "
													+ testname + "*****" + shells[shells.length - 1].getText());
										}
									}
								});
								first = false;
							}

							String[] splitCommand = command.split(";");
							String methodName = splitCommand[0];
							if (methodName.equals("expandTreeItems")) {
								String[] nodes = splitCommand[1].split(",");
								out.println(expandTreeItems(nodes));
							} else if (methodName.equals("analyzeWidgets")) {
								out.println(analyzeWidgets());
							} else if (methodName.equals("setCursorInTextWithContentsAtPosition")) {
								String locator = splitCommand[1];
								String content = splitCommand[2];
								String position = splitCommand[3];
								out.println(setCursorInTextWithContentsAtPosition(locator, content, position));
							} else if (methodName.equals("checkTextExistInWidgets")) {
								String locator = splitCommand[1];
								String text = splitCommand[2];
								out.println(checkTextExistInWidgets(locator, text));
							} else if (methodName.equals("pressShortcutWithModificationKeyOfStyledText")) {
								String locator = splitCommand[1];
								String modificationKeys = splitCommand[2];
								char key = splitCommand[3].toCharArray()[0];
								out.println(pressShortcutOfStyledText(locator, modificationKeys, key));
							} else if (methodName.equals("pressGlobalShortcut")) {
								String modificationKeys = splitCommand[1];
								char key = splitCommand[2].toCharArray()[0];
								out.println(pressGlobalShortcut(modificationKeys, key));
							} else if (methodName.equals("readAllProjectsInTree")) {
								out.println(readAllProjectsInTree());
							} else if (methodName.equals("deleteAllProjects")) {
								out.println(deleteAllProjects());
							} else if (methodName.equals("countChildrenEquals")) {
								String expectedCount = splitCommand[2];
								String[] nodes = splitCommand[1].split(",");
								out.println(countChildrenEquals(nodes, expectedCount));
							} else {
								try {
									if (splitCommand.length == 2) {
										Method method = getClass().getMethod(methodName, String.class);
										out.println(method.invoke(this, splitCommand[1]));
									}
									if (splitCommand.length == 3) {
										Method method = getClass().getMethod(methodName, String.class, String.class);
										out.println(method.invoke(this, splitCommand[1], splitCommand[2]));
									}
								} catch (NoSuchMethodException e) {
									LOGGER.error("Method not found in fixture", e);
								} catch (SecurityException e) {
									LOGGER.error("Error Executing Teststep", e);
								} catch (IllegalAccessException e) {
									LOGGER.error("Error Executing Teststep", e);
								} catch (IllegalArgumentException e) {
									LOGGER.error("Error Executing Teststep", e);
								} catch (InvocationTargetException e) {
									LOGGER.error("Error Executing Teststep", e);
								}
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
	public String textIsVisible(String text) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("textIsVisible: " + text);
		}
		List<? extends Widget> widgets = bot.widgets(widgetOfType(Widget.class));

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
	 * @return true, if the searched text is in the styledText.
	 */
	public String compareTextInStyledById(String locator, String searched) {
		boolean compResult = false;
		try {
			String text = bot.styledTextWithId(locator).getText();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("compareTextInStyledById locator: " + locator + " text: " + searched + " in:\n " + text);
			}
			compResult = text.contains(searched);
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
			LOGGER.error("ERROR " + e.getMessage(), e);
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
			LOGGER.error("ERROR " + e.getMessage(), e);
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
			LOGGER.error("ERROR " + e.getMessage(), e);
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
				try {
					LOGGER.trace("Result of isButtonEnabledById: " + bot.buttonWithId(locator).isEnabled());
				} catch (WidgetNotFoundException e) {

					SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithId(locator);
					return Boolean.valueOf(toolbarButton.isEnabled()).toString();
				}
			}
			return Boolean.valueOf(bot.buttonWithId(locator).isEnabled()).toString();
		} catch (Exception e) {
			LOGGER.error("ERROR " + e.getMessage(), e);
			analyzeWidgets();
			return "ERROR " + e.getMessage();
		}
	}

	/**
	 * retrieve the items of a drop down box and check if one equals the given
	 * value.
	 * 
	 * @param locator
	 *            locator for a drop down
	 * @param checkString
	 *            string to look for
	 * @return true, if drop down contains the given value
	 */
	public boolean checkDropDownContains(String locator, String checkString) {
		SWTBotCombo combo = bot.comboBoxWithId(locator);
		if (combo != null) {
			String[] items = combo.items();
			for (String item : items) {
				if (item.equals(checkString)) {
					return true;
				}
			}
			return false;
		} else {
			throw new IllegalArgumentException("no drop down found for locator '" + locator + "'");
		}
	}

}
