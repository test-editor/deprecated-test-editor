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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNTeamShareStatusService implements TeamShareStatusServicePlugIn, IContextFunction {

	private static final Logger logger = Logger.getLogger(SVNTeamShareStatusService.class);

	private IEventBroker eventBroker;

	/**
	 * list of modificated teststructures.
	 */
	private Map<TestProject, List<String>> projects = new HashMap<TestProject, List<String>>();
	private Map<String, Set<String>> changedTestStructresPerProject = new HashMap<String, Set<String>>();

	private List<String> whiteListForNonTestStructures = Arrays.asList("AllActionGroups.xml", "config.tpr",
			"ElementList.conf", "TechnicalBindingTypeCollection.xml", "MetaData.properties");

	@Override
	public List<String> getModified(TestProject testProject) {

		if (projects.containsKey(testProject)) {
			return projects.get(testProject);
		}

		logger.info("No Project found !");

		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(final TestProject testProject) throws FileNotFoundException {

		// clean project instance for update with newest data
		projects.put(testProject, new ArrayList<String>());

		final ArrayList<String> testStructures = new ArrayList<String>();
		final Set<String> changedTestStructres = new HashSet<String>();

		// init with new data
		final File file = new File(testProject.getTestProjectConfig().getProjectPath());

		if (file.exists()) {
			final SVNClientManager clientManager = getSVNClientManager();

			new Thread(new Runnable() {

				@Override
				public void run() {
					int changedTestStructresSize = 0;
					if (projects.containsKey(testProject.getName())) {
						changedTestStructresSize = projects.get(testProject.getName()).size();
					}
					try {

						clientManager.getStatusClient().doStatus(file, true, false, false, false,

								new ISVNStatusHandler() {
									@Override
									public void handleStatus(SVNStatus status) throws SVNException {

										String fullName = status.getFile().getAbsolutePath();

										if (!testStructures.contains(fullName) && !isInIgnoreList(fullName)) {
											testStructures.add(fullName);
											List<String> changedStructures = lookUpTestStructuresByPath(
													testProject.getRootElement(), fullName);
											if (changedStructures != null) {
												changedTestStructres.addAll(changedStructures);
											}
										}
									}

									/**
									 * 
									 * @param fullName
									 * @return Returns true if given string is
									 *         in ignore list.
									 */
									private boolean isInIgnoreList(String fullName) {

										boolean inIgnoreList = false;

										for (int i = 0; i < SVNTeamShareService.IGNORE_LIST.length; i++) {
											if (fullName.matches(".*" + SVNTeamShareService.IGNORE_LIST[i])) {
												inIgnoreList = true;
												break;
											}
										}

										return inIgnoreList;
									}

								});

						if (testStructures.size() > 0) {
							if (eventBroker != null) {
								for (String testStructure : testStructures) {
									eventBroker.post(
											TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED_BY_TESTSTRUCTURE,
											testStructure);
								}
							}
							projects.put(testProject, testStructures);
						} else {
							projects.put(testProject, new ArrayList<String>());
						}
						changedTestStructresPerProject.put(testProject.getName(), changedTestStructres);

					} catch (Exception e) {
						logger.error("Could not read the SVNStatus from Project: " + testProject.getName()
								+ "\n error: " + e.getMessage(), e);
					}
					if (changedTestStructresSize != changedTestStructres.size()) {
						eventBroker.send(
								TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_TEAMSHARESTATUS,
								testProject.getName());
					}
				}

			}, "threadStatusService").start();

		} else {
			logger.warn("Given project " + file + " does not exist");
			throw new FileNotFoundException(file.getAbsolutePath());
		}
	}

	/**
	 * create a new SVNClientManager without Credentials.
	 * 
	 * @return a new {@link SVNClientManager};
	 */
	private SVNClientManager getSVNClientManager() {

		logger.trace("getSVNClientManager");

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		return SVNClientManager.newInstance(null, authManager);
	}

	@Override
	public boolean isModified(TestStructure testStructure) {

		if (changedTestStructresPerProject.containsKey(testStructure.getRootElement().getName())) {
			return changedTestStructresPerProject.get(testStructure.getRootElement().getName())
					.contains(testStructure.getFullName());
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(TestProject testProject) {

		if (projects.containsKey(testProject)) {
			projects.remove(testProject);
			return true;
		}

		logger.debug("project " + testProject + " does not exists for removing");
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

	private List<String> lookUpTestStructuresByPath(TestProject testProject, String path) {
		String pointSeparatedFile = path.replace(File.separatorChar, '.');

		List<String> result = new ArrayList<String>();
		String testStructure = "";
		if (pointSeparatedFile.indexOf(".content.txt") > 0) {
			testStructure = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".content.txt"));
		} else if (pointSeparatedFile.indexOf(".metadata.xml") > 0) {
			testStructure = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".metadata.xml"));
		} else if (pointSeparatedFile.indexOf(".properties.xml") > 0) {
			testStructure = pointSeparatedFile.substring(pointSeparatedFile.indexOf("FitNesseRoot") + 13,
					pointSeparatedFile.indexOf(".properties.xml"));
		} else {
			pointSeparatedFile = pointSeparatedFile.substring(0, pointSeparatedFile.lastIndexOf('.'));
			String filename = path.substring(path.lastIndexOf(File.separator) + 1);
			if (whiteListForNonTestStructures.contains(filename)) {
				result.add(testProject.getName());
				return result;
			}
			return null;
		}
		result.add(testStructure);
		while (testStructure.indexOf('.') != -1) {
			testStructure = testStructure.substring(0, testStructure.lastIndexOf('.'));
			result.add(testStructure);
		}

		return result;
	}

}
