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
package org.testeditor.ui.parts.testhistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.ui.constants.IconConstants;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Label provider to decorate Testhistory entries in a tableviewer.
 *
 */
public class TestHistoryLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Inject
	private TestEditorTranslationService translationService;

	@Override
	public Image getColumnImage(Object element, int index) {
		TestResult testResult = (TestResult) element;
		if (index == 0) {
			if (testResult.isSuccessfully()) {
				return IconConstants.ICON_TESTCASE_SUCCESSED;
			} else {
				return IconConstants.ICON_TESTCASE_FAILED;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {
		TestResult testResult = (TestResult) element;
		if (index == 1) {
			return format(testResult.getResultDate());
		}
		if (index == 2) {
			return getResultSummaryRowFrom(testResult);
		}
		return null;
	}

	/**
	 * formats the date to a string with county format.
	 * 
	 * @param date
	 *            Date
	 * @return a string representing the date
	 */
	private String format(Date date) {
		String dateFormat = translationService.translate("%DateFormatString");
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

	/**
	 * Extracts Summary String from TestResult to be used in the table.
	 * 
	 * @param testResult
	 *            as data for the extraction.
	 * @return string with the result summary.
	 */
	private String getResultSummaryRowFrom(TestResult testResult) {
		String right = translationService.translate("%test.history.right") + ":" + testResult.getRight();
		String wrong = translationService.translate("%test.history.wrong") + ":" + testResult.getWrong();
		String ignored = translationService.translate("%test.history.ignored") + ":" + testResult.getIgnored();
		String exception = translationService.translate("%test.history.exception") + ":" + testResult.getException();
		return right + "; \t" + wrong + "; \t" + ignored + "; \t" + exception;
	}

}
