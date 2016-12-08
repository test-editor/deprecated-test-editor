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

import org.testeditor.core.exceptions.SystemException;
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
 * <li>a <b>MetaDataValue</b> is a value that could be assigned to a
 * TestStrucure
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
	 * Returns a list of all MetaData that are handled by the service.
	 * 
	 * @param project
	 *            TestProject
	 * 
	 * @return - the list of MetaData
	 */
	List<MetaData> getAllMetaData(TestProject project);

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
	 *            - the list of metatags that should be stored
	 * @param orgMetaDataTags
	 *            - the list of metatags before the changes
	 * @param testStructure
	 *            - the teststructure of the metatags
	 * @throws SystemException
	 *             - SystemException
	 */
	void storeMetaDataTags(List<MetaDataTag> metaDataTags, List<MetaDataTag> orgMetaDataTags,
			TestStructure testStructure) throws SystemException;

	/**
	 * Gets the MetaDataValue for a metaDataTag. In the metaDataTag are only the
	 * keys of the the MetaDataValue stored. If the MetaDataValue is not managed
	 * by the service, the method will throw an exception.
	 * 
	 * @param metaDataTag
	 *            - the metadatatag
	 * @param testProject
	 *            - the teststructure of the metatags
	 * @return - the corresponding MetaDataTag
	 */
	MetaDataValue getMetaDataValue(MetaDataTag metaDataTag, TestProject testProject);

	/**
	 * Handles the renaming of a given testStructure.
	 * 
	 * @param selectedTestStructure
	 *            - the teststrcuture with the old name
	 * @param newName
	 *            - the new name without the path information (not the
	 *            fullName).
	 * @throws SystemException
	 *             - SystemException
	 */
	void rename(TestStructure selectedTestStructure, String newName) throws SystemException;

	/**
	 * Deletes the metaData for a teststructure. If no metadata are available
	 * for the teststructure, nothing will be done
	 * 
	 * @param testStructure
	 *            - the teststrcuture
	 * @throws SystemException
	 *             - SystemException
	 */
	void delete(TestStructure testStructure) throws SystemException;

	/**
	 * Clears the local cache and reads the metadatainformation from the local
	 * disk. This method is used on the refresh button in the TestEditor
	 * explorer.
	 * 
	 * @param testProject
	 *            - the project to be refreshed
	 */
	void refresh(TestProject testProject);

	/**
	 * Gets all TestCases that are tagged for a given MetaDataValue. The method
	 * will return a list of Strings that refer to the testcases.
	 * 
	 * @param project
	 *            - the project that has to be searched.
	 * @param metaDataValue
	 *            - the metadatavalue to be searched
	 * @return - the list of ID of the matching testcases
	 */
	List<String> getTestCases(TestProject project, MetaDataValue metaDataValue);

	/**
	 * Gets all TestCases that are tagged for a list of given MetaDataValues.
	 * Only testcases that are tagged for all metadatavalues are returned
	 * (AND-Logic). The method will return a list of Strings that refer to the
	 * testcases.
	 * 
	 * @param project
	 *            - the project that has to be searched.
	 * @param metaDataValueList
	 *            - the list of metadatavalues to be searched
	 * @return - the list of ID of the matching testcases
	 */
	List<String> getTestCases(TestProject project, List<MetaDataValue> metaDataValueList);

	void move(String orgName, TestStructure movedTestStructure) throws SystemException;

}
