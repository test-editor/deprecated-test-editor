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
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNTeamShareStatusServiceNew implements TeamShareStatusServicePlugIn, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareStatusServiceNew.class);

	private IEventBroker eventBroker;

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

							testStructures.add(fullName);

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

		if (listOfModifiedTestStructures == null) {
			update(testStructure.getRootElement());
			listOfModifiedTestStructures = projects.get(testStructure.getRootElement());
		}

		if (listOfModifiedTestStructures != null) {
			for (String modifiedTestStructure : listOfModifiedTestStructures) {

				if (modifiedTestStructure.contains(testStructure.getFullName())) {
					return true;
				}
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

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
		}
		return this;
	}

	@Override
	public String getId() {
		return SVNTeamShareConfig.SVN_TEAM_SHARE_PLUGIN_ID;
	}

}
