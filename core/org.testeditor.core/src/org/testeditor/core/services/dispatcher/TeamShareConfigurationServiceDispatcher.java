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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.services.interfaces.FieldMappingExtension;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;

public class TeamShareConfigurationServiceDispatcher implements TeamShareConfigurationService {

	@Override
	public String getTranslatedHumanReadablePlugInName(TranslationService translationService) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeamShareConfig createAnEmptyTeamShareConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FieldMappingExtension> getFieldMappingExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAsProperties(TeamShareConfig teamShareConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeamShareConfig createTeamShareConfigFrom(Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

}
