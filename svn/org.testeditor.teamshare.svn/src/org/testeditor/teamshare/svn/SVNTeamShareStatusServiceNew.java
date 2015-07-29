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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;
import org.testeditor.fitnesse.util.FitNesseUtil;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNTeamShareStatusServiceNew implements TeamShareStatusServicePlugIn, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareStatusServiceNew.class);

	private IEventBroker eventBroker;

	/**
	 * list of modificated teststructures.
	 */
	Map<TestProject, List<String>> projects = new HashMap<TestProject, List<String>>();

	List<String> whiteListForNonTestStructures = Arrays.asList("AllActionGroups.xml", "config.tpr", "ElementList.conf",
			"TechnicalBindingTypeCollection.xml", "MetaData.properties");

	private TeamShareService teamShareService;

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
	public void update(final TestProject testProject) throws FileNotFoundException {

		LOGGER.trace("testProject" + testProject);

		// clean project instance for update with newest data
		projects.put(testProject, new ArrayList<String>());

		final ArrayList<String> testStructures = new ArrayList<String>();

		// init with new data
		final File file = new File(testProject.getTestProjectConfig().getProjectPath());

		if (file.exists()) {
			final SVNClientManager clientManager = getSVNClientManager();

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						clientManager.getStatusClient().doStatus(file, true, false, false, false,

						new ISVNStatusHandler() {
							@Override
							public void handleStatus(SVNStatus status) throws SVNException {

								String fullName = status.getFile().getAbsolutePath();

								if (!testStructures.contains(fullName) && !isInIgnoreList(fullName)) {
									LOGGER.info(fullName);
									testStructures.add(fullName);
								}
							}

							/**
							 * 
							 * @param fullName
							 * @return Returns true if given string is in ignore
							 *         list.
							 */
							private boolean isInIgnoreList(String fullName) {

								boolean inIgnoreList = false;

								for (int i = 0; i < SVNTeamShareService.IGNORE_LIST.length; i++) {
									if (fullName.matches(".*FitNesseRoot.*" + SVNTeamShareService.IGNORE_LIST[i])) {
										inIgnoreList = true;
										break;
									}
								}

								return inIgnoreList;
							}

						});

						if (testStructures.size() > 0) {
							projects.put(testProject, testStructures);
						} else {
							projects.put(testProject, new ArrayList<String>());
						}

						if (eventBroker != null) {
							eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED,
									testProject.getName());
						}

					} catch (Exception e) {
						LOGGER.error("Could not read the SVNStatus from Project: " + testProject.getName()
								+ "\n error: " + e.getMessage(), e);
					}
				}

			}, "threadStatusService").start();

		} else {
			LOGGER.warn("Given project " + file + " does not exist");
			throw new FileNotFoundException(file.getAbsolutePath());
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

		if (listOfModifiedTestStructures != null) {
			for (String modifiedTestStructure : listOfModifiedTestStructures) {

				String modifiedTestStructureAsFitNessePath = FitNesseUtil.convertToFitNessePath(modifiedTestStructure);

				if (modifiedTestStructureAsFitNessePath.equals(testStructure.getFullName())) {
					return true;
				} else if (FitNesseUtil.contains(testStructure.getFullName(), modifiedTestStructureAsFitNessePath)) {
					return true;
				} else if (whiteListForNonTestStructures.contains(modifiedTestStructureAsFitNessePath)
						&& (testStructure instanceof TestProject)) {
					// only if given teststructure is not a project
					return true;
				}
			}

		}

		return false;
	}

	public void bind(TeamShareServicePlugIn teamShareService) {
		if (teamShareService.getId().equals(getId())) {
			this.teamShareService = teamShareService;
		} else {
			LOGGER.error("No SVN Plugin available");
		}
	}

	public void unBind(TeamShareServicePlugIn teamShareService) {
		if (teamShareService.getId().equals(getId())) {
			this.teamShareService = null;
		}
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
		if (teamShareService == null) {
			teamShareService = context.get(TeamShareService.class);
		}
		return this;
	}

	@Override
	public String getId() {
		return SVNTeamShareConfig.SVN_TEAM_SHARE_PLUGIN_ID;
	}

}
