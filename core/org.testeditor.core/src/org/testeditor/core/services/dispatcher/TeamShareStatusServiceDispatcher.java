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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareStatusService;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;

/**
 * Dispatcher to lookup the right plugIn of the TeamShareStatusService.
 *
 */
public class TeamShareStatusServiceDispatcher implements TeamShareStatusService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(TeamShareServiceDispatcher.class);
	private Map<String, TeamShareStatusServicePlugIn> teamShareStatusServices = new HashMap<String, TeamShareStatusServicePlugIn>();

	/**
	 * 
	 * @param teamShareService
	 *            to be bind to this service.
	 */
	public void bind(TeamShareStatusServicePlugIn teamShareService) {
		teamShareStatusServices.put(teamShareService.getId(), teamShareService);
		LOGGER.info("Binding TeamShareStatusServicePlugIn Plug-In " + teamShareService.getClass().getName());
	}

	/**
	 * 
	 * @param teamShareService
	 *            to be removed.
	 */
	public void unBind(TeamShareStatusServicePlugIn teamShareService) {
		teamShareStatusServices.remove(teamShareService.getId());
		LOGGER.info("Removing TeamShareStatusServicePlugIn Plug-In " + teamShareService.getClass().getName());
	}

	@Override
	public void setTeamStatusForProject(TestProject testProject) {
		TeamShareStatusServicePlugIn teamShareStatus = getTeamShareStatusPlugIn(testProject);
		if (teamShareStatusServices != null) {
			teamShareStatus.setTeamStatusForProject(testProject);
		}
	}

	@Override
	public List<String> getModifiedFilesFromTestStructure(TestStructure testStructure) {
		TeamShareStatusServicePlugIn teamShareStatus = getTeamShareStatusPlugIn(testStructure.getRootElement());
		if (teamShareStatusServices != null) {
			return teamShareStatus.getModifiedFilesFromTestStructure(testStructure);
		}
		return null;
	}

	/**
	 * 
	 * @param testProject
	 *            used to look up the team share plugin.
	 * @return null if no team share config is present or there is no plugin
	 *         registered on that id otherwise it returns the team share plugin
	 *         for the project.
	 */
	private TeamShareStatusServicePlugIn getTeamShareStatusPlugIn(TestProject testProject) {
		if (testProject.getTestProjectConfig().getTeamShareConfig() != null) {
			return teamShareStatusServices.get(testProject.getTestProjectConfig().getTeamShareConfig().getId());
		}
		return null;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		Collection<TeamShareStatusServicePlugIn> plugins = teamShareStatusServices.values();
		for (TeamShareStatusServicePlugIn plugin : plugins) {
			if (plugin instanceof IContextFunction) {
				((IContextFunction) plugin).compute(context, contextKey);
			}
		}
		return this;
	}

}
