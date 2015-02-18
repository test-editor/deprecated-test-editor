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
package org.testeditor.ui.uiscanner.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.testeditor.ui.uiscanner.expressions.Expression;
import org.testeditor.ui.uiscanner.expressions.ExpressionException;
import org.testeditor.ui.uiscanner.expressions.ExpressionReader;

/**
 * Test the CheckReader class which read out the Expression from a txt file.
 * 
 * @author dkuhlmann
 * 
 */
public class ExpressionReaderTest {

	/**
	 * Check the Expression reader.
	 * 
	 * @throws ExpressionException
	 *             ExpressionException
	 * @throws IOException
	 *             IOException
	 */
	@Test
	public void readCheck() throws ExpressionException, IOException {
		ExpressionReader reader = new ExpressionReader();
		HashMap<String, Expression> exp = reader.readCheck(new File("resources/expressions/newCheck.txt").getPath()
				.toString());
		assertEquals(5, exp.size());
	}
}
