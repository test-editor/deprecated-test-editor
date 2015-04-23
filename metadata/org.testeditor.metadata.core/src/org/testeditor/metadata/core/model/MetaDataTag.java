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

}
