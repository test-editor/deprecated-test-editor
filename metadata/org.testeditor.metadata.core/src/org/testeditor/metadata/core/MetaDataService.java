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
import org.testeditor.metadata.core.model.MetaData;
import org.testeditor.metadata.core.model.MetaDataTag;
import org.testeditor.metadata.core.model.MetaDataValue;

/**
 * The MetadataService manages information about the TestStructures that are not
 * part of the TestSturctures itself but containing information what is tested
 * in the TestStructures. The services defines the following structure for the
 * metadata:
 * <ul>
 * <li>a <b>MetaDataValue<b> is a value that could be assigned to a TestStrucure
 * <li>The <b>MetaData</b> is a group of MetaDataValues
 * <li>A <b> MetaDataTag</b> is an association between a TestStructure and a
 * MetaDataValue.
 * </ul>
 * 
 * 
 * @author Georg Portwich
 *
 */
public interface MetaDataService {

	/**
	 * Returns a list of all MetaData that are handled by the service
	 * 
	 * @return - the list of MetaData
	 */
	List<MetaData> getAllMetaData(TestProject project);

	/**
	 * return the MetaData for specific key. The method will throw a
	 * runtimeException if there is no metadata for the key.
	 * 
	 * @param label
	 *            - the key of the metadata
	 * @return - the metadata-.
	 */
	// MetaData getMetaData(String key);

	/**
	 * Return the metatags for a testscture. If there is no metadate for the
	 * teststructure an empty list is returned.
	 * 
	 * @param testStructure
	 *            - the teststructure
	 * @return the list of metatags
	 */
	List<MetaDataTag> getMetaDataTags(TestStructure testStructure);

	/**
	 * Stores the metatags for a testsctures.
	 * 
	 * @param metaDataTags
	 *            - the list of metatags
	 * @param testStructure
	 *            - the teststructure of the metatags
	 */
	void storeMetaDataTags(List<MetaDataTag> metaDataTags, TestStructure testStructure);

	/**
	 * Gets the MetaDataValue for a metaDataTag. In the metaDataTag are only the
	 * keys of the the MetaDataValue stored. If the MetaDataValue is not managed
	 * by the service, the method will throw an exception.
	 * 
	 * @param metaDataTag
	 *            - the metadatatag
	 * @return - the corresponding MetaDataTag
	 */
	MetaDataValue getMetaDataValue(MetaDataTag metaDataTag, TestProject project);

	/**
	 * Handles the renaming of a given testStructure.
	 * 
	 * @param selectedTestStructure
	 *            - the teststrcuture with the old name
	 * @param sbname
	 *            - the new name without the path information (not the
	 *            fullName).
	 */
	void rename(TestStructure selectedTestStructure, String sbname);

	/**
	 * Deletes the metaData for a teststructure. If no metadata are available
	 * for the teststructure, nothing will be done
	 * 
	 * @param selectedTestStructure
	 *            - the teststrcuture
	 */
	void delete(TestStructure testStructure);

	List<String> getTestCases(String projectName, MetaDataValue metaDataValue);
}
