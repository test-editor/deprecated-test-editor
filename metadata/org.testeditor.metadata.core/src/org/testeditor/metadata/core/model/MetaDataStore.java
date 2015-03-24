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

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class MetaDataStore {

	private List<MetaDataValueList> list;
	private Integer id;
	@XStreamOmitField
	private TestProject project;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MetaDataStore() {
		list = new ArrayList<MetaDataValueList>();
		id = 1;
	}

	public List<MetaDataValueList> getList() {
		return list;
	}

	public TestProject getProject() {
		return project;
	}

	public void setProject(TestProject project) {
		this.project = project;
	}

	public Integer createNewId() {
		return id++;
	}

	public MetaDataValue createNewValue(String key, String value, MetaDataValueList parent) throws Exception {
		MetaDataValue metaDataValue = new MetaDataValue();
		metaDataValue.setKey(key);
		metaDataValue.setValue(value);
		metaDataValue.setParent(parent);
		metaDataValue.setId(createNewId());
		List<MetaDataValue> values = parent.getMetaDataValues();
		if (values.size() >= 1) {
			for (MetaDataValue metaDataValue2 : values) {
				if (metaDataValue2.getId().equals(metaDataValue.getId())) {
					throw new Exception("Es existiert bereits eine MetaDataValue mit derselben id: "
							+ metaDataValue.getId());
				}
				if (metaDataValue2.getValue().equals(metaDataValue.getValue())
						&& metaDataValue2.getKey().equals(metaDataValue.getKey())) {
					throw new Exception("Es existiert bereits eine MetaDataValue mit dem selben key-value Paar.");
				}
			}
		}

		return metaDataValue;

	}

	// private MetaDataValue findMetaDataValueByID(Integer id) {
	//
	// // TODO: Wertelisten suchen mit gleicher id und MetaDataValue
	// // wiedergeben.
	//
	// List<MetaDataValue> foundValues = new ArrayList<>();
	//
	// MetaDataService service = getService(); // TODO?
	// MetaDataTagList metaDataTagListFromXml = service.getTags(testCase);
	//
	// MetaDataStore store = service.getMetaDataStore(project);
	// List<MetaDataValueList> metaDataList = store.getList();
	// List<MetaDataValue> metaDataValues = new ArrayList<>();
	// for (MetaDataValueList metaDataValueList : metaDataList) {
	// for (MetaDataValue metaDataValue : metaDataValues) {
	// metaDataValues.add(metaDataValue);
	// }
	// }
	//
	// List<MetaDataTag> metaDataTags = metaDataTagListFromXml.getTags();
	// for (MetaDataTag metaDataTag : metaDataTags) {
	// if (metaDataTag.getMetaDataValue().getId().equals(obj)) {
	// foundValues.add(obj);
	// }
	// }
	// return foundValues;
	//
	// }
}
