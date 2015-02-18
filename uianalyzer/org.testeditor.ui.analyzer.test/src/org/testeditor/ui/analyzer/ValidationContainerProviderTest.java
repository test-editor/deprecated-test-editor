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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.testeditor.core.model.teststructure.TestCase;
import org.testeditor.ui.analyzer.errormodel.Error;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * Modultest for ValidationContainerProvider.
 * 
 */
public class ValidationContainerProviderTest {

	/**
	 * Test the hasChildren operation of the contentprovider. only on
	 * errorcontainer children is expected.
	 */
	@Test
	public void testHasChildren() {
		ValidationContainerProvider containerProvider = new ValidationContainerProvider();
		assertFalse(containerProvider.hasChildren(new Object()));
		assertFalse(containerProvider.hasChildren(new Error(new TestCase())));
		assertTrue(containerProvider.hasChildren(new ErrorContainer(new TestCase())));
	}

	/**
	 * 
	 * Tests the get children from an errorcontainer.
	 * 
	 */
	@Test
	public void testGetChildren() {
		ValidationContainerProvider containerProvider = new ValidationContainerProvider();
		ErrorContainer parentElement = new ErrorContainer(new TestCase());
		Error er = new Error(new TestCase());
		parentElement.add(er);
		Object[] children = containerProvider.getChildren(parentElement);
		assertEquals(1, children.length);
		assertSame(er, children[0]);
	}

}
