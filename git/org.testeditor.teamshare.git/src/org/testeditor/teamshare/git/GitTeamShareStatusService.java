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
package org.testeditor.teamshare.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.TeamShareStatusService;

/**
 * Service to retrieve the state of a team shared test structures. SVN based
 * implementation of the service.
 *
 */
public class GitTeamShareStatusService implements TeamShareStatusService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(GitTeamShareStatusService.class);
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
	@Override
	public void setTeamStatusForProject(final TestProject testProject) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.testeditor.teamshare.svn.xxx#getModifiedFilesFromTestStructure(org
	 * .testeditor.core.model.teststructure.TestStructure)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public List<String> getModifiedFilesFromTestStructure(final TestStructure testStructure) {

		final List<String> result = new ArrayList<String>();
		Repository repository = null;

		try {

			repository = getRepository(testStructure);

			Status status = new Git(repository).status().call();

			result.addAll(status.getAdded());
			result.addAll(status.getChanged());
			result.addAll(status.getModified());
			result.addAll(status.getRemoved());
			result.addAll(status.getUntracked());

		} catch (NoWorkTreeException | IOException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} finally {
			repository.close();
		}

		return result;

	}

	private Repository getRepository(final TestStructure testStructure) throws IOException {
		Repository repository;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.findGitDir(
				new File(testStructure.getRootElement().getTestProjectConfig().getProjectPath())).build();
		return repository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testeditor.teamshare.svn.xxx#isFinish()
	 */
	@Override
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
