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
package org.testeditor.core.services.dispatcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;

/**
 * Dispatcher to lookup the right plugIn of the TeamShareService.
 *
 */
public class TeamShareServiceDispatcher implements TeamShareService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TeamShareServiceDispatcher.class);
	private Map<String, TeamShareServicePlugIn> teamShareServices = new HashMap<String, TeamShareServicePlugIn>();

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareServicePlugIn teamShareService) {
		teamShareServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareService Plug-In " + teamShareService.getClass().getName());
	}

	@Override
	public void disconnect(TestProject testProject, TranslationService translationService) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			teamShareService.disconnect(testProject, translationService);
		}
	}

	@Override
	public void share(TestProject testProject, TranslationService translationService, String comment)
			throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			teamShareService.share(testProject, translationService, comment);
		}
	}

	@Override
	public String approve(TestStructure testStructure, TranslationService translationService, String comment)
			throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			return teamShareService.approve(testStructure, translationService, comment);
		}
		return null;
	}

	@Override
	public String update(TestStructure testStructure, TranslationService translationService) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			return teamShareService.update(testStructure, translationService);
		}
		return null;
	}

	@Override
	public void checkout(TestProject testProject, TranslationService translationService)
			throws SystemException, TeamAuthentificationException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			teamShareService.checkout(testProject, translationService);
		}
	}

	@Override
	public void delete(TestStructure testStructure, TranslationService translationService) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.delete(testStructure, translationService);
		}
	}

	@Override
	public String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.getStatus(testStructure, translationService);
		}
		return null;
	}

	@Override
	public void addProgressListener(TestStructure testStructure, ProgressListener listener) {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.addProgressListener(testStructure, listener);
		}
	}

	@Override
	public void addChild(TestStructure testStructureChild, TranslationService translationService)
			throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructureChild.getRootElement());
		if (teamShareService != null) {
			teamShareService.addChild(testStructureChild, translationService);
		}
	}

	/**
	 * 
	 * @param testProject
	 *            used to look up the team share plugin.
	 * @return null if no team share config is present or there is no plugin
	 *         registered on that id otherwise it returns the team share plugin
	 *         for the project.
	 */
	private TeamShareServicePlugIn getTeamShare(TestProject testProject) {
		if (testProject.getTestProjectConfig().getTeamShareConfig() != null) {
			TeamShareServicePlugIn servicePlugIn = teamShareServices
					.get(testProject.getTestProjectConfig().getTeamShareConfig().getId());
			if (servicePlugIn == null) {
				LOGGER.error("No Service found for: " + testProject);
			}
			return servicePlugIn;
		}
		return null;
	}

	@Override
	public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
			throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			return teamShareService.validateConfiguration(testProject, translationService);
		}
		return false;
	}

	@Override
	public void revert(TestStructure testStructure, TranslationService translationService) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.revert(testStructure, translationService);
		}

	}

	@Override
	public void rename(TestStructure testStructure, String newName, TranslationService translationService)
			throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.rename(testStructure, newName, translationService);
		}
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		Collection<TeamShareServicePlugIn> plugins = teamShareServices.values();
		for (TeamShareServicePlugIn plugin : plugins) {
			if (plugin instanceof IContextFunction) {
				((IContextFunction) plugin).compute(context, contextKey);
			}
		}
		return this;
	}

	@Override
	public void addAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.addAdditonalFile(testStructure, fileName);
		}
	}

	@Override
	public int availableUpdatesCount(TestProject testProject) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			return teamShareService.availableUpdatesCount(testProject);
		}
		return 0;
	}

	@Override
	public void removeAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testStructure.getRootElement());
		if (teamShareService != null) {
			teamShareService.removeAdditonalFile(testStructure, fileName);
		}
	}

	@Override
	public boolean isCleanupNeeded(TestProject testProject) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			return teamShareService.isCleanupNeeded(testProject);
		}
		return false;
	}

	@Override
	public void cleanup(TestProject testProject) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			teamShareService.cleanup(testProject);
		}
	}

	@Override
	public Map<String, String> getAvailableReleases(TestProject testProject) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			return teamShareService.getAvailableReleases(testProject);
		}
		return null;
	}

	@Override
	public void switchToBranch(TestProject testProject, String url) throws SystemException {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			teamShareService.switchToBranch(testProject, url);
		}
	}

	@Override
	public String getCurrentBranch(TestProject testProject) {
		TeamShareServicePlugIn teamShareService = getTeamShare(testProject);
		if (teamShareService != null) {
			return teamShareService.getCurrentBranch(testProject);
		}
		return null;
	}

}
