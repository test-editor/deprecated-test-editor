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

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class MetaDataTag {

	private Integer metaDataValueId;

	@XStreamOmitField
	private MetaDataValue metaDataValue;

	public MetaDataTag() {
	}

	public MetaDataValue getMetaDataValue() {
		return metaDataValue;
	}

	public void setMetaDataValue(MetaDataValue metaDataValue) throws Exception {
		this.metaDataValue = metaDataValue;
		if (metaDataValue.getId() <= 0 || metaDataValue.getId() == null) {
			throw new Exception("Die Id ist nicht vorhanden oder <= 0 .");
		}
		metaDataValueId = metaDataValue.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metaDataValue == null) ? 0 : metaDataValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDataTag other = (MetaDataTag) obj;
		if (metaDataValue == null) {
			if (other.metaDataValue != null)
				return false;
		} else if (!metaDataValue.equals(other.metaDataValue))
			return false;
		return true;
	}

}
