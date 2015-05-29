package org.testeditor.teamshare.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareConfigurationService;
import org.testeditor.core.services.interfaces.TeamShareService;

@SuppressWarnings("restriction")
public class GitTeamShareService implements TeamShareService, IContextFunction {

	private static final Logger LOGGER = Logger.getLogger(GitTeamShareService.class);

	private IEventBroker eventBroker;

	/**
	 * remove the GitMetaData from the given testProject.
	 * 
	 * @param testProject
	 *            TestProject.
	 * @return true when all svnMeatData were deleted.
	 */
	private boolean deleteGitMetaData(TestProject testProject) {
		String svnPath = testProject.getTestProjectConfig().getProjectPath() + File.separator + ".git";
		return deleteDirectory(new File(svnPath));
	}

	/**
	 * Deletes directory and its content.
	 * 
	 * @param file
	 *            directory or file to be deleted
	 * @return true if delete was successful else false
	 * */
	private boolean deleteDirectory(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return file.delete();
	}

	@Override
	public void disconnect(TestProject testProject, TranslationService translationService,
			TeamShareConfigurationService teamShareConfigurationService) throws SystemException {
		testProject.getTestProjectConfig().setTeamShareConfig(null);
		deleteGitMetaData(testProject);
		List<TestStructure> childrenWithScenarios = testProject.getAllTestChildrenWithScenarios();
		for (TestStructure testStructure : childrenWithScenarios) {
			testStructure.setTeamChangeType(TeamChangeType.NONE);
		}
		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED, testProject.getFullName());
		}

	}

	@Override
	public void share(TestProject testProject, TranslationService translationService, String svnComment)
			throws SystemException {

		Repository repository = null;

		try {

			GitTeamShareConfig teamShareConfig = (GitTeamShareConfig) testProject.getTestProjectConfig()
					.getTeamShareConfig();

			File projectDir = new File(testProject.getTestProjectConfig().getProjectPath());
			Git.init().setDirectory(projectDir).call();

			repository = FileRepositoryBuilder.create(new File(projectDir.getAbsolutePath(), ".git"));

			Git git = new Git(repository);

			git.add().addFilepattern(".").call();
			git.commit().setMessage("init project").call();

			// push the changes to remote git repo
			git.push()
					.setOutputStream(System.out)
					.setRemote(teamShareConfig.getUrl())
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider(teamShareConfig.getUserName(), teamShareConfig
									.getPassword())).call();

			LOGGER.info("Created a new repository at " + repository.getDirectory());

		} catch (IOException | IllegalStateException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}

	}

	@Override
	public String approve(TestStructure testStructure, TranslationService translationService, String comment)
			throws SystemException {

		Repository repository = null;

		try {

			GitTeamShareConfig teamShareConfig = (GitTeamShareConfig) testStructure.getRootElement()
					.getTestProjectConfig().getTeamShareConfig();

			repository = getRepository(testStructure);
			Git git = new Git(repository);

			Status status = new Git(repository).status().call();

			if (status.getModified().size() > 0) {
				String fullNamePath = testStructure.getFullName().replaceAll("\\.", "/");
				git.add().addFilepattern("FitNesseRoot/" + fullNamePath).call();
			}

			// commit the changes
			git.commit().setHookOutputStream(System.out).setMessage(comment).call();

			// push the changes to remote git repo
			git.push()
					.setOutputStream(System.out)
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider(teamShareConfig.getUserName(), teamShareConfig
									.getPassword())).call();

			String url = teamShareConfig.getUrl();
			LOGGER.info("Pushed from repository: " + repository.getDirectory() + " to remote repository at " + url);

		} catch (IOException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}
		
		return "";

	}

	@Override
	public String update(TestStructure testStructure, TranslationService translationService)
			throws SystemException {

		Repository repository = null;

		try {

			repository = getRepository(testStructure);
			Git git = new Git(repository);
			git.pull().call();

		} catch (IOException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}

		return "revisionnumber";

	}

	@Override
	public void checkout(TestProject testProject, TranslationService translationService) throws SystemException,
			TeamAuthentificationException {

		Git result = null;

		try {

			TeamShareConfig teamShareConfig = testProject.getTestProjectConfig().getTeamShareConfig();
			String REMOTE_URL = ((GitTeamShareConfig) teamShareConfig).getUrl();

			File dstPath = new File(testProject.getTestProjectConfig().getProjectPath());

			// then clone
			System.out.println("Cloning from " + REMOTE_URL + " to " + dstPath);
			result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(dstPath).call();

			// Note: the call() returns an opened repository already which needs
			// to be closed to avoid file handle leaks!
			LOGGER.info("Having repository: " + result.getRepository().getDirectory());
		} catch (IllegalStateException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (result != null) {
				result.close();
			}
		}

	}

	@Override
	public String getId() {
		return GitTeamShareConfig.GIT_TEAM_SHARE_PLUGIN_ID;
	}

	@Override
	public void delete(TestStructure testStructure, TranslationService translationService) throws SystemException {

		Repository repository = null;
		try {

			repository = getRepository(testStructure);
			Git git = new Git(repository);

			String fullNamePath = testStructure.getFullName().replaceAll("\\.", "/");

			// remove files
			git.rm().addFilepattern("FitNesseRoot/" + fullNamePath).call();

		} catch (NoWorkTreeException | IOException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}

	}

	@Override
	public String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException {
		// TODO
		return null;
	}

	@Override
	public void addProgressListener(ProgressListener listener) {
		// TODO

	}

	@Override
	public void addChild(TestStructure testStructureChild, TranslationService translationService)
			throws SystemException {

		Repository repository = null;
		try {
			repository = getRepository(testStructureChild);
			Git git = new Git(repository);

			String fullNamePath = testStructureChild.getFullName().replaceAll("\\.", "/");

			// run the add-call
			git.add().addFilepattern("FitNesseRoot/" + fullNamePath).call();

		} catch (NoWorkTreeException | IOException | GitAPIException e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}

	}

	@Override
	public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
			throws SystemException {
		// TODO
		return false;
	}

	@Override
	public List<TeamChange> revert(TestStructure testStructure, TranslationService translationService)
			throws SystemException {
		// TODO
		return null;
	}

	@Override
	public void rename(TestStructure testStructure, String newName, TranslationService translationService)
			throws SystemException {
		// TODO
	}

	private Repository getRepository(final TestStructure testStructure) throws IOException {
		Repository repository;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.findGitDir(
				new File(testStructure.getRootElement().getTestProjectConfig().getProjectPath())).build();
		return repository;
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
		}
		return this;
	}

}
