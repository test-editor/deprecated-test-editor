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
package org.testeditor.core.model.teststructure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * 
 * Modultest for the TestStructure.
 * 
 */
public class TestStructureTest {

	/**
	 * Tests the equals method with the same object.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testEqualsWithSame() throws Exception {
		TestStructure ts = new TestStructure() {

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}

			@Override
			public String getTypeName() {
				return null;
			}
		};
		assertTrue(ts.equals(ts));
	}

	/**
	 * Tests the equals method with a null reference.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testEqualsWithNull() throws Exception {
		TestStructure ts = new TestStructure() {

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}

			@Override
			public String getTypeName() {
				return null;
			}
		};
		assertFalse(ts.equals(null));
	}

	/**
	 * Tests the equals method with another teststructuret.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testEqualsWithOther() throws Exception {
		TestStructure ts = createTestStruckure();
		ts.setName("Test");
		TestStructure otherTs = createTestStruckure();
		otherTs.setName("Other");
		assertFalse(ts.equals(otherTs));
		otherTs.setName("Test");
		assertTrue(ts.equals(otherTs));
	}

	/**
	 * Tests the equals method with another teststructuret.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testNotEqualsWithOtherClass() throws Exception {
		TestStructure ts = createTestStruckure();
		ts.setName("Test");
		TestStructure otherTs = new TestStructure() {

			@Override
			public String getTypeName() {
				return null;
			}

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}
		};
		otherTs.setName("Test");
		assertFalse(ts.equals(otherTs));
	}

	/**
	 * Tests the equals method with object of a other class.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testEqualsWithOtherObjectType() throws Exception {
		TestStructure ts = new TestStructure() {

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}

			@Override
			public String getTypeName() {
				return null;
			}
		};
		ts.setName("Test");
		assertFalse(ts.equals("a String"));
	}

	/**
	 * Tests the Init Behaviour.
	 * 
	 * @throws Exception
	 *             for Test
	 */
	@Test
	public void testInitialization() throws Exception {
		TestStructure ts = new TestStructure() {

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}

			@Override
			public String getTypeName() {
				return null;
			}
		};
		assertNotNull("Test Structure without name is not allowed", ts.getName());
		assertNull("Test Structure without parent is null", ts.getParent());
	}

	/**
	 * Test that a Teststructure element in the tree find it root element.
	 * 
	 * @throws Exception
	 *             for test
	 */
	@Test
	public void testGetRootElement() throws Exception {
		TestProject tp = new TestProject();
		TestSuite ts = new TestSuite();
		tp.addChild(ts);
		assertSame(tp, ts.getRootElement());
		TestSuite ts1 = new TestSuite();
		ts.addChild(ts1);
		assertSame(tp, ts1.getRootElement());
		TestCase tc = new TestCase();
		ts1.addChild(tc);
		TestScenario tsc = new TestScenario();
		ts1.addChild(tsc);
		assertSame(tp, tc.getRootElement());
	}

	/**
	 * Test matching fullName with an equal full name.
	 */
	@Test
	public void testMatchesFullName() {
		TestProject tp = new TestProject();
		tp.setName("Foo");
		TestStructure testStruckure = createTestStruckure();
		testStruckure.setName("MyTS");
		tp.addChild(testStruckure);
		assertTrue(testStruckure.matchesFullName("Foo.MyTS"));
	}

	/**
	 * Test the matching with fullName and oldName.
	 */
	@Test
	public void testMatchesFullNameWithOldName() {
		TestProject tp = new TestProject();
		tp.setName("Foo");
		TestStructure testStruckure = createTestStruckure();
		testStruckure.setName("MyTS");
		testStruckure.setOldName("TheTS");
		tp.addChild(testStruckure);
		assertTrue(testStruckure.matchesFullName("Foo.MyTS"));
		assertTrue(testStruckure.matchesFullName("Foo.TheTS"));
		assertFalse(testStruckure.matchesFullName("Foo.TheOtherTS"));
	}

	/**
	 * Test the check if a given TestStructure is in the parent path.
	 */
	@Test
	public void testIsTestStructureInPratentHirachieOfChildTestStructure() {
		TestProject tp = new TestProject();
		tp.setName("Foo");
		TestSuite testStruckure = new TestSuite();
		TestSuite otherTS = new TestSuite();
		otherTS.setName("fooTs");
		tp.addChild(otherTS);
		testStruckure.setName("MyTestSuite");
		tp.addChild(testStruckure);
		TestSuite testStruckure2 = new TestSuite();
		testStruckure.addChild(testStruckure2);
		testStruckure2.setName("MyTestSuite2");
		TestStructure leaf = createTestStruckure();
		testStruckure2.addChild(leaf);
		assertTrue(leaf.isInParentHirachieOfChildTestStructure(tp));
		assertTrue(leaf.isInParentHirachieOfChildTestStructure(testStruckure));
		assertTrue(leaf.isInParentHirachieOfChildTestStructure(testStruckure2));
		assertFalse(leaf.isInParentHirachieOfChildTestStructure(otherTS));
	}

	/**
	 * private inner class to create a TestStrukture.
	 * 
	 * @return a new TestStructure
	 */
	private TestStructure createTestStruckure() {
		return new TestStructure() {

			@Override
			public String getTypeName() {
				return null;
			}

			@Override
			public String getSourceCode() {
				return null;
			}

			@Override
			public String getPageType() {
				return null;
			}
		};
	}

	/**
	 * Tests the building of the Parent Path of the TestStructure as List.
	 */
	@Test
	public void testGetAllParents() {
		TestCase testCase = new TestCase();
		TestProject testProject = new TestProject();
		TestSuite testSuite1 = new TestSuite();
		TestSuite testSuite2 = new TestSuite();
		testProject.addChild(testSuite1);
		testSuite1.addChild(testSuite2);
		testSuite2.addChild(testCase);
		List<TestStructure> parents = testCase.getAllParents();
		assertSame(parents.get(0), testSuite2);
		assertSame(parents.get(1), testSuite1);
		assertSame(parents.get(2), testProject);
		assertTrue(parents.size() == 3);
	}

}
