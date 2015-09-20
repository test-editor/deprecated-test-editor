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
package org.testeditor.ui.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.graphics.Image;
import org.junit.Assert;
import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.ui.analyzer.errormodel.Error;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * Modultest for ValidationLabelProvider.
 * 
 */
public class ValidationLabelProviderIntTest {

	/**
	 * Tests the GetText Method for the Labelprovider.
	 */
	@Test
	public void testGetText() {
		ValidationLabelProvider labelProvider = new ValidationLabelProvider();
		TestCase testCase = new TestCase();
		testCase.setName("MyTest");
		ErrorContainer errorContainer = new ErrorContainer(testCase);
		assertEquals("MyTest (0)", labelProvider.getText(errorContainer));
	}

	/**
	 * Test the GetLabel Method for the Labelprovider. Error Cotainer and Error
	 * should have different images.
	 */
	@Test
	public void testGetImage() {
		ValidationLabelProvider labelProvider = new ValidationLabelProvider();
		Image imageCon = labelProvider.getImage(new ErrorContainer(new TestCase()));
		assertNotNull(imageCon);
		assertNotNull(labelProvider.getImage(new Error(new TestCase())));
		Assert.assertNotEquals(imageCon, labelProvider.getImage(new Error(new TestCase())));
	}

	/**
	 * Tests that the images are disposed.
	 */
	@Test
	public void testDispose() {
		ValidationLabelProvider labelProvider = new ValidationLabelProvider();
		labelProvider.dispose();
		assertTrue(labelProvider.getImage(new ErrorContainer(new TestCase())).isDisposed());
		assertTrue(labelProvider.getImage(new Error(new TestCase())).isDisposed());
	}

}
