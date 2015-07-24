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
package org.testeditor.metadata.core.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;

/**
 * A MetaData value represents a key and a list of values that can be attached
 * to a testcase. A MetaData exists allways in a context of project.
 */
public class MetaData {

	private List<MetaDataValue> values;
	private TestProject testProject;
	private String key;

	/**
	 * Get the project of the metadata.
	 * 
	 * @return the testproject
	 */
	public TestProject getTestProject() {
		return testProject;
	}

	/**
	 * Creates a new metadate from a key, a label for a given testProject.
	 * 
	 * @param key
	 *            - the key of the metadata
	 * @param label
	 *            - the label of the metadata
	 * @param testProject
	 *            - the testProject
	 */
	public MetaData(String key, String label, TestProject testProject) {
		this.key = key;
		this.label = label;
		this.testProject = testProject;
		values = new ArrayList<MetaDataValue>();
	}

	/**
	 * The label of the metadata. The label will be displayed in the UI.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the metadata.
	 * 
	 * @param label
	 *            - the label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	private String label;

	/**
	 * Get the technical key of the metadata. The technical key is used to refer
	 * to the metadata.
	 * 
	 * @return the technical key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the key of the metadata.
	 * 
	 * @param key
	 *            - the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Defaultconstructor the metadata.
	 */
	public MetaData() {
		values = new ArrayList<MetaDataValue>();
	}

	/**
	 * Gets the list of metadatevalues of a metadata.
	 * 
	 * @return - the list of metadata- Values
	 */
	public List<MetaDataValue> getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MetaData other = (MetaData) obj;
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}

	public static class MetaDataTagListComparator implements Comparator<MetaData> {

		@Override
		public int compare(MetaData list1, MetaData list2) {
			if (list1 == null) {
				return -1;
			}
			if (list2 == null) {
				return 1;
			}
			return list1.getLabel().compareToIgnoreCase(list2.getLabel());
		}

	}

	@Override
	public String toString() {
		return label;
	}

}
