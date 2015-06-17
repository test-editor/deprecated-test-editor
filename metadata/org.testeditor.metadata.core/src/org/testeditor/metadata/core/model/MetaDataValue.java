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

public class MetaDataValue implements Comparable<MetaDataValue> {

	private String label;
	private MetaData metaData;
	private String key;

	public MetaDataValue(MetaData metaData, String key, String label) {
		this.metaData = metaData;
		this.key = key;
		this.label = label;
		metaData.getValues().add(this);
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String value) {
		this.label = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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

	public String getGlobalKey() {
		return getMetaData().getKey() + "-" + getKey();
	}

	public String toString() {
		return label;
	}

	@Override
	public int compareTo(MetaDataValue metaDataValue) {
		if (metaData.getLabel().equals(metaDataValue.getMetaData().getLabel())) {
			return getLabel().compareTo(metaDataValue.getLabel());
		}
		return metaData.getLabel().compareTo(metaDataValue.getMetaData().getLabel());
	}

}
