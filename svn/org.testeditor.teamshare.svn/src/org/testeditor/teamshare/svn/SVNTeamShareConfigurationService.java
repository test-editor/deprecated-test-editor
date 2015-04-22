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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;

/**
 * Implementation of the Configuration Service for Team Sharing using
 * Subversion.
 * 
 */
@SuppressWarnings("restriction")
public class SVNTeamShareConfigurationService implements TeamShareConfigurationService {

	public static final String URL_PROPERTY = "teamshare.svn.url";
	public static final String USERNAME_PROPERTY = "teamshare.svn.username";
	public static final String PASSWORD_PROPERTY = "teamshare.svn.password";

	@Override
	public String getId() {
		return SVNTeamShareConfig.SVN_TEAM_SHARE_PLUGIN_ID;
	}

	@Override
	public String getTranslatedHumanReadablePlugInName(TranslationService translationService) {
		return "Subversion";
	}

	@Override
	public TeamShareConfig createAnEmptyTeamShareConfig() {
		return new SVNTeamShareConfig();
	}

	@Override
	public List<FieldMappingExtension> getFieldMappingDeclarations() {
		List<FieldMappingExtension> result = new ArrayList<FieldMappingExtension>();
		result.add(new URLFieldDeclaration());
		result.add(new UserNameFieldDeclaration());
		result.add(new PasswordFieldDeclaration());
		return result;
	}

	@Override
	public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
		HashMap<String, String> result = new HashMap<String, String>();
		SVNTeamShareConfig svnCfg = (SVNTeamShareConfig) teamShareConfig;
		result.put(URL_PROPERTY, svnCfg.getUrl());
		return result;
	}

	@Override
	public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
		SVNTeamShareConfig config = new SVNTeamShareConfig();
		config.setUrl(properties.getProperty(URL_PROPERTY));
		return config;
	}

	@Override
	public String getTemplateForConfiguration() {
		StringBuilder sb = new StringBuilder();
		String lf = "\n";
		sb.append("teamshare.svn.url=$SVN_URL$").append(lf);
		return sb.toString();
	}
}
