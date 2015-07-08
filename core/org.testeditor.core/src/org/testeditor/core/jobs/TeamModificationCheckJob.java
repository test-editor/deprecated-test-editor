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
package org.testeditor.core.jobs;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.services.interfaces.TeamShareService;
import org.testeditor.core.services.interfaces.TestProjectService;
import org.testeditor.core.util.TestStateProtocolService;

/**
 * This Job checks periodically team server about modifications that can be
 * updated.
 *
 */
public class TeamModificationCheckJob implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(TeamModificationCheckJob.class);

	@Inject
	private TestProjectService testProjectService;

	@Inject
	private TeamShareService teamShareService;

	@Inject
	private TestStateProtocolService testProtocolService;

	private Set<TestProject> blackList = new HashSet<TestProject>();

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10000);
				checkForModifications();
			} catch (InterruptedException e) {
				LOGGER.info("Team server observer job terminated.");
				return;
			}
		}
	}

	/**
	 * Iterates over the TestProjects and selects the projects with team
	 * support. For this projects a check of newer versions on the team server
	 * is executed.
	 * 
	 * The informations are reported to the TestStateProtocolService.
	 */
	protected void checkForModifications() {
		List<TestProject> unmodifiableList = Collections.unmodifiableList(testProjectService.getProjects());
		for (TestProject tp : unmodifiableList) {
			if (tp.getTestProjectConfig().isTeamSharedProject()) {
				try {
					if (!blackList.contains(tp)) {
						int updatesCount = teamShareService.availableUpdatesCount(tp);
						if (updatesCount > 0) {
							testProtocolService.set(tp, updatesCount);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error check for available updated.", e);
					blackList.add(tp);
					LOGGER.info("Blacklisted: " + tp.getFullName());
				}
			}
		}

	}
}