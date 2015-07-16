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
package org.testeditor.teamshare.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareStatusServiceNew;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNTeamShareStatusServiceNew implements TeamShareStatusServiceNew {

	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareStatusServiceNew.class);

	/**
	 * list of modificated teststructures.
	 */
	Map<TestProject, List<String>> projects = new HashMap<TestProject, List<String>>();

	@Override
	public List<String> getModified(TestProject testProject) {

		if (projects.containsKey(testProject)) {
			return projects.get(testProject);
		}

		LOGGER.info("No Project found !");

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(final TestProject testProject) {

		LOGGER.trace("testProject" + testProject);

		// clean project instance for update with newest data
		projects.remove(testProject);

		final ArrayList<String> testStructures = new ArrayList<String>();

		// init with new data
		File file = new File(testProject.getTestProjectConfig().getProjectPath());

		if (file.exists()) {
			final SVNTeamShareService teamShareService = new SVNTeamShareService();
			SVNClientManager clientManager = getSVNClientManager();
			try {

				clientManager.getStatusClient().doStatus(file, true, true, true, true,

				new ISVNStatusHandler() {

					@Override
					public void handleStatus(SVNStatus status) throws SVNException {

						LOGGER.info(status.getFile().getAbsolutePath());

						SVNStatusType statusType = status.getCombinedNodeAndContentsStatus();

						if (statusType != SVNStatusType.STATUS_NONE && statusType != SVNStatusType.STATUS_NORMAL
								&& statusType != SVNStatusType.STATUS_IGNORED) {

							String fullName = teamShareService.convertFileToFullname(status.getFile(), testProject);

							if (SVNStatusType.STATUS_DELETED == statusType) {
								// fileList.put(fullName.substring(0,
								// fullName.lastIndexOf(".")),
								// TeamChangeType.MODIFY);
								testStructures.add(fullName);

							} else {
								// fileList.put(fullName,
								// getTeamChangeTypeFromSVNStatusType(statusType));
								testStructures.add(fullName);

							}

						}
					}

				});
			} catch (Exception e) {
				LOGGER.error(
						"Could not read the SVNStatus from Project: " + testProject.getName() + "\n error: "
								+ e.getMessage(), e);
			}
		}

		if (testStructures.size() > 0) {
			projects.put(testProject, testStructures);
		}

	}

	/**
	 * create a new SVNClientManager without Credentials.
	 * 
	 * @return a new {@link SVNClientManager};
	 */
	private SVNClientManager getSVNClientManager() {

		LOGGER.trace("getSVNClientManager");

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		return SVNClientManager.newInstance(null, authManager);
	}

	@Override
	public boolean isModified(TestStructure testStructure) {

		List<String> listOfModifiedTestStructures = projects.get(testStructure.getRootElement());

		for (String modifiedTestStructure : listOfModifiedTestStructures) {

			if (modifiedTestStructure.equals(testStructure.getFullName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean remove(TestProject testProject) {

		if (projects.containsKey(testProject)) {
			projects.remove(testProject);
			return true;
		}

		LOGGER.debug("project " + testProject + " does not exists for removing");
		return false;

	}

}
