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

import java.util.Set;
import java.util.TreeSet;

/**
 * The MetaDataValue represents all tags that belong to a MetaData.
 *
 */
public class MetaDataValue implements Comparable<MetaDataValue> {

	private String label;
	private MetaData metaData;
	private String key;
	private Set<String> testCases = new TreeSet<String>();

	/**
	 * Creates a new MetaDataValue that belongs to a given MetaData an consists
	 * of key and a label.
	 * 
	 * @param metaData
	 *            - the metaData
	 * @param key
	 *            - the key to identify the MetaDataValue
	 * @param label
	 *            - the label to display the MetaDataValue in a UI
	 */
	public MetaDataValue(MetaData metaData, String key, String label) {
		this.metaData = metaData;
		this.key = key;
		this.label = label;
		metaData.getValues().add(this);
	}

	/**
	 * The MetaData-Object the MetaDataValue belongs to.
	 * 
	 * @return - the MetaData.
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * Get the technical key for the metaDataValue.
	 * 
	 * @return the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Get the label for the metadata value. the label is used in the UI.
	 * 
	 * @return - the label of the metadata
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the metadatavalue.
	 * 
	 * @param value
	 *            - the label
	 */
	public void setLabel(String value) {
		this.label = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (label != null) {
			result += label.hashCode();
		}
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
		MetaDataValue other = (MetaDataValue) obj;
		if (getMetaData() == null) {
			if (other.getMetaData() != null) {
				return false;
			}
		} else if (!getMetaData().getKey().equals(other.getMetaData().getKey())) {
			return false;
		}
		if (getKey() == null) {
			if (other.getKey() != null) {
				return false;
			}
		} else if (!getKey().equals(other.getKey())) {
			return false;
		}
		return true;
	}

	/**
	 * Geht global technical of the metadata. The global key consists of the
	 * technical key of the metadatavalue and the key of the metadata.
	 * 
	 * @return - the global key
	 */
	public String getGlobalKey() {
		return getMetaData().getKey() + "-" + getKey();
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * Returns a lost of all testcases that uses that metatag.
	 * 
	 * @return the list of testcases
	 */
	public Set<String> getTestCases() {
		return testCases;
	}

	@Override
	public int compareTo(MetaDataValue metaDataValue) {
		if (metaData.getLabel().equals(metaDataValue.getMetaData().getLabel())) {
			return getLabel().compareTo(metaDataValue.getLabel());
		}
		return metaData.getLabel().compareTo(metaDataValue.getMetaData().getLabel());
	}

}
