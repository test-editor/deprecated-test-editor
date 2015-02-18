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
package org.testeditor.dashboard;

import java.util.concurrent.TimeUnit;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.constants.ColorConstants;

/**
 * @author alebedev
 * 
 *         label provider for LastRunsTable
 */
class MyLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider, IColorProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			TestResult testResult = (TestResult) element;
			if (testResult.isSuite()) {
				return getImage("/testsuite.gif");
			}
			if (testResult.isTestcase()) {
				return getImage("/testcase.gif");
			}
		}
		if (columnIndex == 2) {
			String result = ((TestResult) element).getResult();
			if ("ok".equals(result)) {
				return getImage("/ok.png");
			}
			if ("warning".equals(result)) {
				return getImage("/warning.png");
			}
			if ("failed".equals(result)) {
				return getImage("/failed.png");
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return ((TestResult) element).getDate().toString();
		}
		if (columnIndex == 1) {
			return ((TestResult) element).getName();
		}
		if (columnIndex == 3) {
			return ((TestResult) element).getSubSetRightOf();
		}
		if (columnIndex == 4) {
			return ((TestResult) element).getSubSetWrongOf();
		}
		if (columnIndex == 5) {
			return formatDuration(((TestResult) element).getDuration());
		}
		if (columnIndex == 6) {
			return Integer.toString(((TestResult) element).getRunCount());
		}

		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null; // text color
	}

	@Override
	public Color getBackground(Object element) {
		return ColorConstants.COLOR_WHITE;
	}

	/**
	 * format int duration in H:M:S:MS string.
	 * 
	 * @param runTimeInMillisInt
	 *            duration as int to be formatted
	 * @return duration formatted
	 */
	private String formatDuration(int runTimeInMillisInt) {
		long hours = TimeUnit.MILLISECONDS.toHours(runTimeInMillisInt);
		runTimeInMillisInt -= TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(runTimeInMillisInt);
		runTimeInMillisInt -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(runTimeInMillisInt);
		runTimeInMillisInt -= TimeUnit.SECONDS.toMillis(seconds);

		String hoursString = hours > 0 ? String.format("%02d%n", hours) + "h : " : "";
		String minutesString = minutes > 0 ? String.format("%02d%n", minutes) + "m : " : "";
		String secondsString = seconds > 0 ? String.format("%02d%n", seconds) + "s : " : "";
		String millisString = runTimeInMillisInt > 0 ? String.format("%03d%n", runTimeInMillisInt) + "ms" : "";

		return hoursString + minutesString + secondsString + millisString;
	}

	/**
	 * gets images for results.
	 * 
	 * @param imagePath
	 *            the image path (for example /failed.png)
	 * @return image
	 */
	static Image getImage(String imagePath) {
		Bundle bundle = FrameworkUtil.getBundle(MyLabelProvider.class);
		return ImageDescriptor.createFromURL(bundle.getEntry(imagePath)).createImage();
	}
}