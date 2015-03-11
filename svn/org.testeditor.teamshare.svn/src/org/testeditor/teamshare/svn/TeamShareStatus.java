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
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Opens new Thread and search for SVN changes to save them in the Project. This
 * will e used by Projects under version control to show the user which files
 * has been changed and till yet not approved.
 * 
 * @author dkuhlmann
 * 
 */
public class TeamShareStatus {

	private static final Logger LOGGER = Logger.getLogger(TeamShareStatus.class);
	private IEventBroker eventBroker;
	private Thread svnStateRunner;

	/**
	 * Constructor.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 * 
	 */
	public TeamShareStatus() {
	}

	/**
	 * Constructor.
	 * 
	 * @param eventBroker
	 *            IEventBroker
	 * 
	 */
	public TeamShareStatus(final IEventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	/**
	 * Convert the SVNStatusType to the matching TeamChangeType
	 */
	private TeamChangeType getTeamChangeTypeFromSVNStatusType(SVNStatusType statusType) {
		if (statusType == SVNStatusType.STATUS_ADDED) {
			return TeamChangeType.ADD;
		} else if (statusType == SVNStatusType.STATUS_MODIFIED) {
			return TeamChangeType.MODIFY;
		} else if (statusType == SVNStatusType.STATUS_DELETED) {
			return TeamChangeType.DELETE;
		} else {
			return TeamChangeType.NONE;
		}
	}

	/**
	 * Starts the thread to read the SVN status from the given TestProject the
	 * event ({@link TestEditorCoreEventConstants#TEAM_STATE_LOADED}).
	 * 
	 * @param testProject
	 *            TestProject
	 */
	public void setSVNStatusForProject(final TestProject testProject) {
		if (testProject.getTestProjectConfig().isTeamSharedProject()) {
			svnStateRunner = new Thread(new Runnable() {

				@Override
				public void run() {
					testProject.setTeamChangeFileList(getSvnStatusFromProjectFiles(testProject));
					testProject.setSVNStatusInChilds();
					LOGGER.info("Loaded SVN State for files in: " + testProject.getName());
					if (eventBroker != null) {
						eventBroker.post(TestEditorCoreEventConstants.TEAM_STATE_LOADED, testProject);
					}
				}

				/**
				 * Looks in the given Project for the SVNStatus and returns
				 * every File change in the TestProject in a Map<Fullname,
				 * SVNStatusType>. (None Normal and Ingnore states will not be
				 * in the List.
				 * 
				 * @param testProject
				 *            TestProject
				 * @return Map<String, SVNStatusType> String is the fullname of
				 *         the modified TestStructure, SVNStatusType SVNStatus
				 */
				@SuppressWarnings("deprecation")
				private Map<String, TeamChangeType> getSvnStatusFromProjectFiles(final TestProject testProject) {
					File file = new File(testProject.getTestProjectConfig().getProjectPath());
					final Map<String, TeamChangeType> fileList = new HashMap<String, TeamChangeType>();
					if (file.exists()) {
						final SVNTeamShareService teamShareService = new SVNTeamShareService();
						SVNClientManager clientManager = getSVNClientManager();
						try {

							clientManager.getStatusClient().doStatus(file, true, true, true, true,

							new ISVNStatusHandler() {

								@Override
								public void handleStatus(SVNStatus status) throws SVNException {
									SVNStatusType statusType = status.getCombinedNodeAndContentsStatus();

									if (statusType != SVNStatusType.STATUS_NONE
											&& statusType != SVNStatusType.STATUS_NORMAL
											&& statusType != SVNStatusType.STATUS_IGNORED) {
										String fullName = teamShareService.convertFileToFullname(status.getFile(),
												testProject);
										if (SVNStatusType.STATUS_DELETED == statusType) {
											fileList.put(fullName.substring(0, fullName.lastIndexOf(".")),
													TeamChangeType.MODIFY);
										}
										fileList.put(fullName, getTeamChangeTypeFromSVNStatusType(statusType));
									}
								}

							});
						} catch (SVNException e) {
							LOGGER.error("Could not read the SVNStatus from Project: " + testProject.getName()
									+ "\n error: " + e.getMessage());
						}
					}
					return fileList;
				}

			});
			svnStateRunner.start();
		}
	}

	/**
	 * 
	 * 
	 * @param testStructure
	 *            TestStructure
	 */
	@SuppressWarnings("deprecation")
	public List<String> getModifiedFilesFromTestStructure(final TestStructure testStructure) {
		SVNTeamShareService teamShareService = new SVNTeamShareService();
		final List<String> result = new ArrayList<String>();
		final File file = teamShareService.getFile(testStructure);
		if (file.exists()) {
			SVNClientManager clientManager = getSVNClientManager();
			try {
				clientManager.getStatusClient().doStatus(file, true, false, true, false, new ISVNStatusHandler() {

					@Override
					public void handleStatus(SVNStatus status) throws SVNException {
						SVNStatusType statusType = status.getContentsStatus();

						if (statusType != SVNStatusType.STATUS_NONE && statusType != SVNStatusType.STATUS_NORMAL
								&& statusType != SVNStatusType.STATUS_IGNORED) {
							// result.add(status.getFile().getPath().substring(file.getPath().length()));

							result.add(status.getFile().getPath());
						}
					}

				});
			} catch (SVNException e) {
				LOGGER.error("Could not read the SVNStatus from TestStructure: " + testStructure.getFullName()
						+ "\n error: " + e.getMessage());
			}

		}
		return result;
	}

	/**
	 * checks if the thread is not anymore alive.
	 * 
	 * @return true when the thread is not Alive.
	 */
	public boolean isFinish() {
		return !svnStateRunner.isAlive();
	}

	/**
	 * create a new SVNClientManager without Credentials
	 * 
	 * @return a new {@link SVNClientManager};
	 */
	private SVNClientManager getSVNClientManager() {
		String userName = "";
		String password = "";
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
		return SVNClientManager.newInstance(null, authManager);
	}

}
