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

import java.util.List;

import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.metadata.core.model.MetaDataStore;
import org.testeditor.metadata.core.model.MetaDataTagList;

public interface MetaDataService {

	MetaDataStore getAllMetaDataLists(TestProject testProject);

	public void saveLists(MetaDataStore store) throws Exception;

	List<TestStructure> findDirtyTestcases(TestProject testProject);

	MetaDataTagList getTags(TestStructure testcase);

	public void saveTags(MetaDataTagList metaDataTagList) throws Exception;

}
