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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * @author alebedev
 * 
 *         sorting for columns
 */
public class MyViewerComparator extends ViewerComparator {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(MyViewerComparator.class);
	/**
	 * Representing the descending order.
	 */
	private static final int DESCENDING = 1;
	/**
	 * Currently selected property.
	 */
	private int propertyIndex;
	/**
	 * Currently selected direction.
	 */
	private int direction;

	/**
	 * Constructor.
	 */
	public MyViewerComparator() {
		this.propertyIndex = 0;
		this.direction = DESCENDING;
	}

	/**
	 * returns sorting direction.
	 * 
	 * @return SWT.DOWN or UP
	 */
	public int getDirection() {
		if (direction == DESCENDING) {
			return SWT.DOWN;
		} else {
			return SWT.UP;
		}
	}

	/**
	 * selecting column for sorting.
	 * 
	 * @param column
	 *            table column number
	 */
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		TestResult testResult1 = (TestResult) e1;
		TestResult testResult2 = (TestResult) e2;

		int rc = 0;
		switch (propertyIndex) {
		case 0:
			Date date1 = stringToDate(testResult1.getDate());
			Date date2 = stringToDate(testResult2.getDate());

			rc = date1.compareTo(date2);
			break;
		case 1:
			rc = testResult1.getName().compareTo(testResult2.getName());
			break;
		case 2:
			rc = testResult1.getResult().compareTo(testResult2.getResult());
			break;
		case 3:
			rc = new Integer(testResult1.getQuantityRight()).compareTo(new Integer(testResult2.getQuantityRight()));
			break;
		case 4:
			rc = new Integer(testResult1.getQuantityWrong()).compareTo(new Integer(testResult2.getQuantityWrong()));
			break;
		case 5:
			rc = new Integer(testResult1.getDuration()).compareTo(new Integer(testResult2.getDuration()));
			break;
		case 6:
			rc = new Integer(testResult1.getRunCount()).compareTo(new Integer(testResult2.getRunCount()));
			break;

		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

	/**
	 * Formats date in specified string ("dd/MM/yyyy HH:mm:ss").
	 * 
	 * @param dateString
	 *            date to format
	 * @param date
	 *            formatted date
	 * @return formated date
	 * @throws ParseException
	 */
	private Date stringToDate(String dateString) {

		Date date = null;
		try {
			date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateString);
		} catch (ParseException e) {
			LOGGER.warn("Date parsing error ");
		}

		return date;
	}
}