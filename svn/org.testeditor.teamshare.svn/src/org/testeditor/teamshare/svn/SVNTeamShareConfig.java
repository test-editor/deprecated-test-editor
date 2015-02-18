/*******************************************************************************
 * Copyright (c) 2012, 2014 Signal Iduna Corporation and others.
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

import org.testeditor.core.model.team.TeamShareConfig;

/**
 * 
 * SVN Configuration Bean for TeamSharing over subversion.
 * 
 */
public class SVNTeamShareConfig implements TeamShareConfig {

	public static final String SVN_TEAM_SHARE_PLUGIN_ID = "svn-team-share";

	private String url;
	private String userName;
	private String password;

	@Override
	public String getId() {
		return SVN_TEAM_SHARE_PLUGIN_ID;
	}

	/**
	 * 
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 *            of svn.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 
	 * @return username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * @param userName
	 *            to login in svn
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password
	 *            to login to svn.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
