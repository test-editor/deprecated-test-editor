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
package org.testeditor.metadata.core;

import java.util.ArrayList;
import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.metadata.core.model.MetaDataTag;

public class MetaDataServiceStandAlone extends MetaDataServiceFileImpl {

	public MetaDataServiceStandAlone() {
	}

	public List<MetaDataTag> getMetaDataTags(String projectName, String fullName) {
		TestProject project = new TestProject();
		project.setName(projectName);
		TestProjectConfig testProjectConfig = new TestProjectConfig();
		testProjectConfig.setProjectPath(fullName);
		project.setTestProjectConfig(testProjectConfig);

		List<MetaDataTag> value = new ArrayList<MetaDataTag>();
		if (getMetaDataStore(projectName).containsKey(fullName)) {
			value.addAll(getMetaDataStore(projectName).get(fullName));
		}
		return value;
	}

	public void storeMetaDataTags(List<MetaDataTag> metaDataTags, String testCase, String projectName,
			String projectPath) {
		if (!getMetaDataStore(projectName).containsKey(testCase)) {
			getMetaDataStore(projectName).put(testCase, new ArrayList<MetaDataTag>());
		}
		getMetaDataStore(projectName).get(testCase).clear();
		getMetaDataStore(projectName).get(testCase).addAll(metaDataTags);
		store(projectName, projectPath);

	}

}
