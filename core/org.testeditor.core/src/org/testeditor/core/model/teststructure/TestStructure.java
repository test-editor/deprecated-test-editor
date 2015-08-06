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

import java.util.List;

/**
 * Abstract POJO for the test structure (e.g. test cases and suites).
 */
public abstract class TestStructure {

	private String name;
	private TestStructure parent;
	private String oldNameBeforeRename;

	/**
	 * Initialize the Object with an empty Name and a null Parent.
	 */
	public TestStructure() {
		setName("");
		parent = null;
	}

	/**
	 * Returns the plain text of this test structure as it is used at the third
	 * party system.
	 * 
	 * @return source code
	 */
	public abstract String getSourceCode();

	/**
	 * Sets the name (e.g. 'LoginSuite' or 'LoginWithValidUserTest')
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name (e.g. 'LoginSuite' or 'LoginWithValidUserTest')
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            parent
	 */
	protected void setParent(TestStructure parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent.
	 * 
	 * @return parent
	 */
	public TestStructure getParent() {
		return parent;
	}

	/**
	 * Returns the full name including all parent names separated by dots.
	 * 
	 * @return full name
	 */
	public String getFullName() {
		String fullName = "";

		if (getParent() != null) {
			fullName = fullName.concat(getParent().getFullName()).concat(".");
		}

		fullName = fullName.concat(getName());

		return fullName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result;
		if (name != null) {
			result += name.hashCode();
		}

		result = prime * result;
		if (parent != null) {
			result += parent.hashCode();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof TestStructure && obj.getClass() == this.getClass()) {
			TestStructure other = (TestStructure) obj;
			return this.getFullName().equals(other.getFullName());
		} else {
			return false;
		}
	}

	/**
	 * Searches in the Testsstrucuture Tree for the Root Element.
	 * 
	 * @return the Root Element of the Teststrucutre Tree.
	 */
	public TestProject getRootElement() {
		if (getParent() == null) {
			return (TestProject) this;
		} else {
			return getParent().getRootElement();
		}
	}

	/**
	 * returns the PageType e.g. TestSuite or TestCase or TestScenario.
	 * 
	 * @return PageType
	 */
	public abstract String getPageType();

	/**
	 * returns the TypeName.
	 * 
	 * @return TypeName
	 */
	public abstract String getTypeName();

	/**
	 * 
	 * Indicates that a TestStructure is executable. The default implementation
	 * is false. Subclasses with an executable Test should ovveride this method
	 * with true. Known Subclasses with an true implementation are:
	 * <code>TestCase</code> and <code>TestSuite</code>
	 * 
	 * @return true if this TestStructure can be executed.
	 */
	public boolean isExecutableTestStructure() {
		return false;
	}

	/**
	 * Stores the last oldName of the TestStructure before it was renamed.
	 * 
	 * @param oldName
	 *            before renaming the teststructure.
	 */
	public void setOldName(String oldName) {
		this.oldNameBeforeRename = oldName;
	}

	/**
	 * Checks if the TestStructure matches the given full name.
	 * 
	 * This allows transient rename function checks before persisting them.
	 * 
	 * @param fullName
	 *            to be used for matching this teststructure.
	 * 
	 * @return true, if the fullName or the oldName is euqal.
	 */
	public boolean matchesFullName(String fullName) {
		String myFullName = getFullName();
		boolean oldValueFound = false;
		if (oldNameBeforeRename != null) {
			String oldFullName = myFullName.substring(0, myFullName.lastIndexOf(".") + 1) + oldNameBeforeRename;
			oldValueFound = oldFullName.equals(fullName);
		}
		return myFullName.equals(fullName) | oldValueFound;
	}

	/**
	 * Checks if the given TestStructure is in the parent path of this
	 * TestStructure.
	 * 
	 * @param testStructure
	 *            that may is the parent the updated- {@link TestStructure}
	 * @return true, if a parent or grantParent of the childTestStrcture is
	 *         equal to the updated- {@link TestStructure}
	 */
	public boolean isInParentHirachieOfChildTestStructure(TestStructure testStructure) {
		if (this.equals(testStructure)) {
			return true;
		}
		TestProject root = getRootElement();
		TestStructure parent = this;
		while (!parent.equals(root)) {
			parent = parent.getParent();
			if (parent.equals(testStructure)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a ordered List of the Parents of this Teststructure. The first
	 * element is the direct parent.
	 * 
	 * @return a List containing all parents of this testStructure.
	 */
	public List<TestStructure> getAllParents() {
		List<TestStructure> parents = getParent().getAllParents();
		parents.add(0, getParent());
		return parents;
	}

}
