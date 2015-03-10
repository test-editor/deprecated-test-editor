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
package org.testeditor.ui.constants;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides a general access to all icons of the Test-Editor.
 * <p />
 * 
 * Please attend that the Application.e4xmi contains references to the images
 * directly. Java classes on the other hand should use this class for the
 * centralized access to the images.
 * <p />
 * 
 * The images are located at the current bundle at /icons. The most icons are
 * Eclipse icons with the given Eclipse license expect this files:
 * 
 * <ul>
 * <li>eraser.png</li>
 * <li>excel.png</li>
 * <li>glasses.png</li>
 * <li>open.png</li>
 * <li>remove.png</li>
 * <li>rename.png</li>
 * <li>test_editor.png</li>
 * <li>testexecution_failed.png</li>
 * <li>testexecution_ok.png</li>
 * <li>warning.png</li>
 * </ul>
 */
public final class IconConstants {
	public static final String ICON_URI_TESTCASE = "platform:/plugin/org.testeditor.ui/icons/testcase.gif";
	public static final String ICON_URI_SCENARIO = "platform:/plugin/org.testeditor.ui/icons/scenario.gif";
	public static final String ICON_URI_PROJECT = "platform:/plugin/org.testeditor.ui/icons/project.gif";

	public static final Image ICON_ACTION = getImage("/icons/action.gif");
	public static final Image ICON_ADD = getImage("/icons/add.gif");
	public static final Image ICON_ADD_LINE = getImage("/icons/add_line.gif");
	public static final Image ICON_COPY = getImage("/icons/copy.gif");
	public static final Image ICON_CUT = getImage("/icons/cut.gif");
	public static final Image ICON_DELETE = getImage("/icons/delete.gif");
	public static final Image ICON_DESCRIPTION = getImage("/icons/description.gif");
	public static final Image ICON_EDIT_LINE = getImage("/icons/edit_line.gif");
	public static final Image ICON_EXCEL_IMPORT = getImage("/icons/excel.png");
	public static final Image ICON_PASTE = getImage("/icons/paste.gif");
	public static final Image ICON_PROJECT = getImage("/icons/project.gif");
	public static final Image ICON_SHARED_PROJECT = getImage("/icons/shared_project.gif");
	public static final Image ICON_SHARED_PROJECT_MODIFIED = getImage("/icons/shared_project_modified.gif");
	public static final Image ICON_SCENARIOSUITE = getImage("/icons/scenariosuite.gif");
	public static final Image ICON_SCENARIOSUITE_MODIFIED = getImage("/icons/scenariosuite_modified.gif");
	public static final Image ICON_SCENARIO = getImage("/icons/scenario.gif");
	public static final Image ICON_SCENARIO_MODIFIED = getImage("/icons/scenario_modified.gif");
	public static final Image ICON_TESTEDITOR = getImage("/icons/test_editor.png");
	public static final Image ICON_TESTCASE = getImage("/icons/testcase.gif");
	public static final Image ICON_TESTCASE_MODIFIED = getImage("/icons/testcase_modified.gif");
	public static final Image ICON_TESTCASE_FAILED = getImage("/icons/testcase_failed.gif");
	public static final Image ICON_TESTCASE_FAILED_MODIFIED = getImage("/icons/testcase_failed_modified.gif");
	public static final Image ICON_TESTCASE_SUCCESSED = getImage("/icons/testcase_successed.gif");
	public static final Image ICON_TESTCASE_SUCCESSED_MODIFIED = getImage("/icons/testcase_successed_modified.gif");
	public static final Image ICON_TESTEXECUTION_OK = getImage("/icons/testexecution_ok.png");
	public static final Image ICON_TESTEXECUTION_FAILED = getImage("/icons/testexecution_failed.png");
	public static final Image ICON_TESTSUITE = getImage("/icons/testsuite.gif");
	public static final Image ICON_TESTSUITE_MODIFIED = getImage("/icons/testsuite_modified.gif");
	public static final Image ICON_UNPARSED_LINE = getImage("/icons/delete.gif");
	public static final Image ICON_WARNING = getImage("/icons/warning.png");
	public static final Image ICON_WARNING_SMALL = getImage("/icons/warning_small.png");
	public static final Image ICON_CHECKED = getImage("/icons/checked.png");
	public static final Image ICON_UNCHECKED = getImage("/icons/unchecked.png");
	public static final Image ICON_ADD_OBJECT = getImage("/icons/add_obj.gif");
	public static final Image ICON_IMPORT_PROJECT = getImage("/icons/import_projectset.gif");
	public static final Image ICON_SHARE_PROJECT = getImage("/icons/share_project.gif");
	public static final Image ICON_DISCONNECT_PROJECT = getImage("/icons/disconnect.gif");
	public static final Image ICON_REVERT_TESTSTRUCTURE = getImage("/icons/revert.gif");
	public static final Image ICON_SHARE_SHAREING = getImage("/icons/team_shareing.gif");
	public static final Image ICON_SHOW_CHANGES = getImage("/icons/show_changes.png");
	public static final Cursor CURSOR_ARROW = getCursorArrow();

	/**
	 * Creates an image.
	 * 
	 * @param imagePath
	 *            image path
	 * @return image
	 */
	private static Image getImage(String imagePath) {
		Bundle bundle = FrameworkUtil.getBundle(IconConstants.class).getBundleContext().getBundle();
		return ImageDescriptor.createFromURL(bundle.getEntry(imagePath)).createImage();
	}

	/**
	 * 
	 * @return the arrow-cursor
	 */
	private static Cursor getCursorArrow() {
		return new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW);
	}

	/**
	 * Don't create objects of this constants class.
	 */
	private IconConstants() {
	}

	/**
	 * this method disposes all images. only the Activator-class should call
	 * this method at the end!
	 */
	public static void disposeImages() {
		ICON_ACTION.dispose();
		ICON_ADD.dispose();
		ICON_ADD_LINE.dispose();
		ICON_COPY.dispose();
		ICON_CUT.dispose();
		ICON_DELETE.dispose();
		ICON_DESCRIPTION.dispose();
		ICON_EDIT_LINE.dispose();
		ICON_EXCEL_IMPORT.dispose();
		ICON_PASTE.dispose();
		ICON_PROJECT.dispose();
		ICON_SHARED_PROJECT.dispose();
		ICON_SHARED_PROJECT_MODIFIED.dispose();
		ICON_SCENARIO.dispose();
		ICON_SCENARIO_MODIFIED.dispose();
		ICON_TESTCASE.dispose();
		ICON_TESTCASE_MODIFIED.dispose();
		ICON_TESTCASE_FAILED.dispose();
		ICON_TESTCASE_SUCCESSED_MODIFIED.dispose();
		ICON_TESTCASE_FAILED_MODIFIED.dispose();
		ICON_TESTCASE_SUCCESSED.dispose();
		ICON_SCENARIOSUITE.dispose();
		ICON_SCENARIOSUITE_MODIFIED.dispose();
		ICON_TESTEXECUTION_OK.dispose();
		ICON_TESTEXECUTION_FAILED.dispose();
		ICON_TESTSUITE.dispose();
		ICON_TESTSUITE_MODIFIED.dispose();
		ICON_UNPARSED_LINE.dispose();
		ICON_WARNING.dispose();
		ICON_TESTEDITOR.dispose();
		ICON_IMPORT_PROJECT.dispose();
		ICON_ADD_OBJECT.dispose();
		ICON_SHARE_PROJECT.dispose();
		ICON_DISCONNECT_PROJECT.dispose();
		ICON_SHARE_SHAREING.dispose();
		ICON_REVERT_TESTSTRUCTURE.dispose();
		ICON_SHOW_CHANGES.dispose();
		CURSOR_ARROW.dispose();
	}
}
