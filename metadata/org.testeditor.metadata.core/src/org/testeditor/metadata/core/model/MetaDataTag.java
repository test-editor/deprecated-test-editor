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

public class MetaDataTag {

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	private String key;
	private String parentKey;

	public MetaDataTag() {
	}

	public MetaDataTag(MetaDataValue metaDataValue) {
		key = metaDataValue.getKey();
		parentKey = metaDataValue.getMetaData().getKey();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
		result = prime * result + ((getParentKey() == null) ? 0 : getParentKey().hashCode());
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

	static public class MetaDataTagComparator implements Comparator<MetaDataTag> {

		private MetaDataService metaDataService;
		private TestProject testProject;

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
