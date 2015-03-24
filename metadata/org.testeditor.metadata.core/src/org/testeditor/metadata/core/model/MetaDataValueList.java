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
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;

public class MetaDataValueList {

	private String name;
	private String description;
	private List<MetaDataValue> metaDataValues = new ArrayList<>();
	private TestProject project;

	public MetaDataValueList() {
	}

	public MetaDataValueList(String name, TestProject project) {
		this.project = project;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// TODO: Muss eindeutig sein!!
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MetaDataValue> getMetaDataValues() {
		return metaDataValues;
	}

	public void setMetaDataValues(List<MetaDataValue> metaDataValues) {
		this.metaDataValues = metaDataValues;
	}

	public TestProject getProject() {
		return project;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((metaDataValues == null) ? 0 : metaDataValues.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		MetaDataValueList other = (MetaDataValueList) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (metaDataValues == null) {
			if (other.metaDataValues != null)
				return false;
		} else if (!metaDataValues.equals(other.metaDataValues))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MetaDataValueList [shortName=" + name + ", label=" + description + "]";
	}

}
