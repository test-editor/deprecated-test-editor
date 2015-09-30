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

import java.util.Comparator;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.metadata.core.MetaDataService;

/**
 * A MetaDataTag is the connection between a testCase and a MetaDataValue. The
 * MetaDataValues of a testcase are stored as a list in the MetaDataServerice
 * together with the corresponding testCase.
 */
public class MetaDataTag {

	/**
	 * The key for the tag.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key for the tag.
	 * 
	 * @param key
	 *            - the key.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * The parentKey is the key of the MetaData. *
	 * 
	 * @return - the key of the metaData.
	 */
	public String getParentKey() {
		return parentKey;
	}

	/**
	 * sets the parent key. The key identifies the MetaDataValue of a
	 * MetaData-object
	 * 
	 * @param parentKey
	 *            - the parent key.
	 */
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	private String key;
	private String parentKey;

	/**
	 * Creates an empty new metaData object.
	 */
	public MetaDataTag() {
	}

	/**
	 * Creates a MetaDataTag that points to a given MetaDataValue.
	 * 
	 * @param metaDataValue
	 *            - the metaDataValue
	 */
	public MetaDataTag(MetaDataValue metaDataValue) {
		key = metaDataValue.getKey();
		parentKey = metaDataValue.getMetaData().getKey();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (getKey() != null) {
			result += getKey().hashCode();
		}
		result = prime * result;
		if (getParentKey() != null) {
			result = prime * getParentKey().hashCode();
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
		MetaDataTag other = (MetaDataTag) obj;
		if (getParentKey() == null) {
			if (other.getParentKey() != null) {
				return false;
			}
		} else if (!getParentKey().equals(other.getParentKey())) {
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

	public String getGlobalKey() {
		return getParentKey() + "-" + getKey();
	}

	/**
	 * Class to compare two metaDataTags. The compare is based on the alphabetic
	 * order of the MetaData / MetaDataValue.
	 * 
	 */
	public static class MetaDataTagComparator implements Comparator<MetaDataTag> {

		private MetaDataService metaDataService;
		private TestProject testProject;

		/**
		 * Constructor for the MetaDataTagComparator. The MetaDataService and
		 * the project is needed to find the MetaData / MetaDataValue for a
		 * corresponding MetaDataTag.
		 * 
		 * @param metaDataService
		 *            - the MetaDataService to get MetaData and MetaDataValue
		 *            for a MetaDataTag
		 * @param testProject
		 *            - parameter ist needed to find the correct MetaData
		 */
		public MetaDataTagComparator(MetaDataService metaDataService, TestProject testProject) {
			this.metaDataService = metaDataService;
			this.testProject = testProject;
		}

		@Override
		public int compare(MetaDataTag metaDataTag1, MetaDataTag metaDataTag2) {
			MetaDataValue metaDataValue1 = metaDataService.getMetaDataValue(metaDataTag1, testProject);
			MetaDataValue metaDataValue2 = metaDataService.getMetaDataValue(metaDataTag2, testProject);
			if (!metaDataValue1.getMetaData().getLabel().equals(metaDataValue2.getMetaData().getLabel())) {
				return metaDataValue1.getMetaData().getLabel().compareTo(metaDataValue2.getMetaData().getLabel());
			}
			return metaDataValue1.getLabel().compareTo(metaDataValue2.getLabel());
		}
	}

}
