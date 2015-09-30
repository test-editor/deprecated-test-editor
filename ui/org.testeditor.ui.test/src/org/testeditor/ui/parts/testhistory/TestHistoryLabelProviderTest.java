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

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;
import org.testeditor.core.model.testresult.TestResult;
import org.testeditor.ui.utilities.TestEditorTranslationService;

/**
 * Modul Tests of TestHistoryLabelProvider.
 *
 */
public class TestHistoryLabelProviderTest {

	/**
	 * Test the extraction of a String represantation of the TestResult in the
	 * History table.
	 * 
	 * @throws ParseException
	 *             on test error.
	 */
	@Test
	public void testToStringOfTestResult() throws ParseException {
		TestResult testResult = new TestResult();
		testResult.setException(0);
		testResult.setRight(3);
		testResult.setWrong(1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
		testResult.setResultDate(sdf.parse("12.10.1970"));
		IEclipseContext context = EclipseContextFactory.create();
		context.set(TestEditorTranslationService.class, new TestEditorTranslationService() {
			@Override
			public String translate(String key, Object... params) {
				if (key.equals("%DateFormatString")) {
					return "mm/dd/yyyy";
				}
				return "";
			}
		});
		TestHistoryLabelProvider labelProvider = ContextInjectionFactory.make(TestHistoryLabelProvider.class, context);
		String resultSummaryRowFrom = labelProvider.getColumnText(testResult, 2);
		assertEquals(":3; \t:1; \t:-1; \t:0", resultSummaryRowFrom);
		String timestamp = labelProvider.getColumnText(testResult, 1);
		assertEquals("10/12/1970", timestamp);
	}

}
