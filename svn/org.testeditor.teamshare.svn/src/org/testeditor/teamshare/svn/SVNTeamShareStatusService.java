/*******************************************************************************
 * Copyright (c) 2012 - 2014 Signal Iduna Corporation and others.
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

import java.util.Map;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.model.team.TeamChangeType;

/**
 * Service to retrieve the state of a team shared test structures. SVN based
 * implementation of the service.
 *
 */
public class SVNTeamShareStatusService implements  IContextFunction {

	private Thread svnStateRunner;

	private IEventBroker eventBroker;
	protected Map<String, TeamChangeType> lastSVNState;

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.testeditor.teamshare.svn.xxx#setSVNStatusForProject(org.testeditor
	 * .core.model.teststructure.TestProject)
	 */
//	@Override
//	public void setTeamStatusForProject(final TestProject testProject) {
//		if (testProject.getTestProjectConfig().isTeamSharedProject()) {
//			svnStateRunner = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					testProject.setTeamChangeType(TeamChangeType.NONE);
//					if (lastSVNState != null) {
//						for (String fullname : lastSVNState.keySet()) {
//							TestStructure child = testProject.getTestChildByFullName(fullname);
//							if (child != null) {
//								child.setTeamChangeType(TeamChangeType.NONE);
//							}
//						}
//					}
//					Map<String, TeamChangeType> statusFromProjectFiles = getSvnStatusFromProjectFiles(testProject);
//					lastSVNState = statusFromProjectFiles;
//					updateTeamStatusInChilds(testProject, statusFromProjectFiles);
//					LOGGER.info("Loaded SVN State for files in: " + testProject.getName());
//					if (eventBroker != null) {
//						eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED, testProject);
//					}
//				}
//
//				/**
//				 * set the TeamChangeType from the child`s of the project. (sets
//				 * only modified ones).
//				 * 
//				 * @param changeFilelist
//				 *            Map<String, TeamChangeType> list of childs with
//				 *            their TeamChangeTypes
//				 */
//				public void updateTeamStatusInChilds(TestProject testProject, Map<String, TeamChangeType> changeFilelist) {
//					if (changeFilelist != null) {
//						for (String fullname : changeFilelist.keySet()) {
//							if (!fullname.equals(testProject.getName())) {
//								TestStructure child = testProject.getTestChildByFullName(fullname);
//								if (child != null) {
//									child.setTeamChangeType(changeFilelist.get(fullname));
//								}
//							} else {
//								testProject.setTeamChangeType(changeFilelist.get(fullname));
//							}
//						}
//					}
//				}
//
//				/**
//				 * Looks in the given Project for the SVNStatus and returns
//				 * every File change in the TestProject in a Map<Fullname,
//				 * SVNStatusType>. (None Normal and Ingnore states will not be
//				 * in the List.
//				 * 
//				 * @param testProject
//				 *            TestProject
//				 * @return Map<String, SVNStatusType> String is the fullname of
//				 *         the modified TestStructure, SVNStatusType SVNStatus
//				 */
//				@SuppressWarnings("deprecation")
//				private Map<String, TeamChangeType> getSvnStatusFromProjectFiles(final TestProject testProject) {
//					File file = new File(testProject.getTestProjectConfig().getProjectPath());
//					final Map<String, TeamChangeType> fileList = new HashMap<String, TeamChangeType>();
//					if (file.exists()) {
//						final SVNTeamShareService teamShareService = new SVNTeamShareService();
//						SVNClientManager clientManager = getSVNClientManager();
//						try {
//
//							clientManager.getStatusClient().doStatus(file, true, true, true, true,
//
//							new ISVNStatusHandler() {
//
//								@Override
//								public void handleStatus(SVNStatus status) throws SVNException {
//									SVNStatusType statusType = status.getCombinedNodeAndContentsStatus();
//
//									if (statusType != SVNStatusType.STATUS_NONE
//											&& statusType != SVNStatusType.STATUS_NORMAL
//											&& statusType != SVNStatusType.STATUS_IGNORED) {
//										String fullName = teamShareService.convertFileToFullname(status.getFile(),
//												testProject);
//										if (SVNStatusType.STATUS_DELETED == statusType) {
//											fileList.put(fullName.substring(0, fullName.lastIndexOf(".")),
//													TeamChangeType.MODIFY);
//										} else {
//											fileList.put(fullName, getTeamChangeTypeFromSVNStatusType(statusType));
//										}
//
//									}
//								}
//
//							});
//						} catch (Exception e) {
//							LOGGER.error("Could not read the SVNStatus from Project: " + testProject.getName()
//									+ "\n error: " + e.getMessage(), e);
//						}
//					}
//					return fileList;
//				}
//
//			});
//			svnStateRunner.start();
//		}
//	}


	/**
	 * Checks if the modification information retrieve process is finished.
	 * 
	 * @return true if the operation is completed otherwise false.
	 */
	public boolean isFinished() {
		return !svnStateRunner.isAlive();
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
		}
		return this;
	}




}
