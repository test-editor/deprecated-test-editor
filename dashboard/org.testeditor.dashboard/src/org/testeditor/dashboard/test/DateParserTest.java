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
package org.testeditor.dashboard.test;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author alebedev
 * 
 */
final class DateParserTest {
	/**
	 * @param args
	 */
	private DateParserTest() {
	}

	private static final Logger LOGGER = Logger.getLogger(DateParserTest.class);

	/**
	 */
	public static void formatString() {

		String dateS = "28.02.2014";
		String k = DateParserTest.formatStringInDUration("7801010"); // 2h 10min
																		// 1s
		LOGGER.info("duration " + k); // 10ms
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		try {
			date = sdf.parse(dateS);
			LOGGER.info("xx" + date.toString());

		} catch (ParseException e) {
			LOGGER.error("ParseException", e);
		}

	}

	/**
	 * formates date string in other date string.
	 * 
	 * @param inputDuration
	 *            string to be formatted
	 * @return date as string
	 */
	public static String formatStringInDUration(String inputDuration) {
		long inputDurationLong = Long.parseLong(inputDuration);
		Time time = new Time(inputDurationLong);
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		String s = cal.get(Calendar.HOUR) - 1 + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + ":"
				+ cal.get(Calendar.MILLISECOND);
		cal.add(Calendar.HOUR, -1);
		// System.out.printf("%tT%n", cal);
		return s;
	}
}
