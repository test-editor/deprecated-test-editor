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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamChange;
import org.testeditor.core.model.team.TeamChangeType;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.interfaces.TeamShareStatusService;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNMoveClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnGetInfo;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

/**
 * 
 * Subversion implementation of the <code>TeamShareService</code>.
 * 
 */
public class SVNTeamShareService implements TeamShareServicePlugIn, IContextFunction {

	// SVNException: svn: E175002: connection refused by the server
	private static final int CONNECTION_REFUSED = 175002;

	// SVNAuthenticationException: svn: E170001: Authentication required
	private static final int AUTHENTICATION_REQUIRED = 175001;

	private static final int AUTHENTICATION_REQUIRED_2 = 170001;

	// all files/directories in list will be ignored during import to SVN
	static final String[] IGNORE_LIST = { "ErrorLogs", "testProgress", "testResults", ".DS_Store" };

	private static final Logger LOGGER = Logger.getLogger(SVNTeamShareService.class);

	private StringBuilder svnStatus;

	private ProgressListener listener;

	private IEventBroker eventBroker;

	private TeamShareStatusService teamShareStatusService;

	static {

		// SVN setup
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();

	}

	/**
	 * Sets the ignore list.
	 * 
	 * @param clientManager
	 *            SVNClientManager
	 */
	private void setIgnoreList(SVNClientManager clientManager) {

		ISVNOptions isvnOptions = new DefaultSVNOptions() {
			@Override
			public String[] getIgnorePatterns() {
				return IGNORE_LIST;
			}
		};
		clientManager.setOptions(isvnOptions);

	}

	/**
	 * Creates an instance of the svn client manager to work with svn.
	 * 
	 * @param testProject
	 *            used to identify the svn.
	 * @return client to access the svn.
	 */
	private SVNClientManager getSVNClientManager(TestProject testProject) {

		String username = ((SVNTeamShareConfig) (testProject.getTestProjectConfig().getTeamShareConfig()))
				.getUserName();
		String password = ((SVNTeamShareConfig) (testProject.getTestProjectConfig().getTeamShareConfig()))
				.getPassword();

		ISVNAuthenticationManager authManager;
		if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
			authManager = SVNWCUtil.createDefaultAuthenticationManager();
		} else {
			authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
		}

		return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), authManager);
	}

	/**
	 * Returns the File for checkin to SVN or checkout from SVN.
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return File
	 */
	public File getFile(TestStructure testStructure) {

		return new File(getFolderName(testStructure));
	}

	/**
	 * Returns the absolute path of the fitnesse folder of the given
	 * teststructure. If the teststructure is a project
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return File
	 */
	private String getFolderName(TestStructure testStructure) {
		TestProject testProject = testStructure.getRootElement();

		if (testStructure instanceof TestProject) {
			// in case of project the root of project above FitNesseRoot will be
			// checked in.
			return testProject.getTestProjectConfig().getProjectPath();
		} else {
			return testProject.getTestProjectConfig().getProjectPath() + "/FitNesseRoot/"
					+ testStructure.getFullName().replaceAll("\\.", "/");
		}

	}

	/**
	 * convert the given File from the path to a TestStructure FullName. If the
	 * TestStructure FullName don't start with the given TestProject it will
	 * return "";
	 * 
	 * @param file
	 *            File to convert the oath to FullName.
	 * @param testProject
	 *            TestProject where the TestStructure should be.
	 * @return TestStructure FullName of the given file.
	 */
	public String convertFileToFullname(File file, TestProject testProject) {
		/*
		 * Cut the Path before the workspace because everything before
		 * .testeditor is not needed.
		 */
		if (file.isFile()) {
			file = file.getParentFile();
		}
		String path;
		if (!file.getPath().equals(testProject.getTestProjectConfig().getProjectPath())) {
			if (file.getPath().length() < testProject.getTestProjectConfig().getProjectPath().length() + 2) {
				return testProject.getName();
			}
			path = file.getPath().substring(testProject.getTestProjectConfig().getProjectPath().length() + 1);
		} else {
			return testProject.getName();
		}
		/*
		 * Changes in the RecentChanges will not be showed.
		 */
		path = path.replace(File.separator, ".");
		if (!path.startsWith("FitNesseRoot.RecentChanges")) {
			if (path.contains("FitNesseRoot.")) {
				path = path.substring("FitNesseRoot.".length(), path.length());
			}
			if (path.startsWith(testProject.getName())) {
				return path;
			}
		}
		return testProject.getName();
	}

	@Override
	public void share(TestProject testProject, TranslationService translationService, String svnComment)
			throws SystemException {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("testProject: " + testProject.getFullName());
		}

		String projectName = testProject.getName();
		try {

			TeamShareConfig teamShareConfig = testProject.getTestProjectConfig().getTeamShareConfig();
			String url = ((SVNTeamShareConfig) teamShareConfig).getUrl() + "/" + projectName;
			SVNClientManager clientManager = getSVNClientManager(testProject);

			TestProjectConfig testProjectConfig = testProject.getTestProjectConfig();
			String projectPath = testProjectConfig.getProjectPath();

			SVNURL svnUrl;
			// we use the assumption, that a local file Url begins with
			// "file:///c://tmp/test"
			if (url.startsWith(".")) { // only for junit test
				svnUrl = SVNURL.fromFile(new File(url));
			} else {
				svnUrl = SVNURL.parseURIEncoded(url);
			}

			clientManager.getCommitClient().setEventHandler(new SVNLoggingEventHandler(listener, LOGGER));

			setIgnoreList(clientManager);

			// useGlobalIgnores must be set to TRUE, otherwise ignore list will
			// not work.
			SVNCommitInfo doImport = clientManager.getCommitClient().doImport(new File(projectPath), svnUrl,
					svnComment, new SVNProperties(), true, false, SVNDepth.INFINITY);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("doImport: " + doImport);
			}

			// delete directory for checkout from repository
			FileUtils.deleteDirectory(new File(projectPath));

			// checkout
			SVNUpdateClient updateClient = clientManager.getUpdateClient();
			long doCheckout = updateClient.doCheckout(svnUrl, new File(testProject.getTestProjectConfig()
					.getProjectPath()), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("doCheckout: " + doCheckout);
			}

		} catch (SVNException e) {
			LOGGER.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		}

	}

	@Override
	public String approve(TestStructure testStructure, TranslationService translationService, String svnComment)
			throws SystemException {
		String resultState = "";
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("testStructure: " + testStructure.getFullName());
		}

		try {
			TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNWCClient wcClient = clientManager.getWCClient();

			File checkinFile = getFile(testStructure);

			boolean isDir = false;
			if (checkinFile.isDirectory()) {
				isDir = true;
			}

			try {
				wcClient.doAdd(checkinFile, false, isDir, true, SVNDepth.INFINITY, true, true);
			} catch (Exception e) {
				// TODO should be analyzed
				// org.tmatesoft.svn.core.SVNException: svn: E150002: 'file' is
				// already under version control
				LOGGER.warn(e.getMessage(), e);
			}

			SVNCommitClient cc = clientManager.getCommitClient();
			cc.setEventHandler(new SVNLoggingEventHandler(listener, LOGGER));
			SVNCommitInfo doCommit = cc.doCommit(new File[] { checkinFile }, false, svnComment, null, null, false,
					true, SVNDepth.INFINITY);
			resultState = translationService.translate("%svn.state.approve",
					"platform:/plugin/org.testeditor.teamshare.svn") + " " + doCommit.getNewRevision();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("CommitInfo: " + doCommit.toString());
			}
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message);
		}
		return resultState;
	}

	@Override
	public String update(TestStructure testStructure, TranslationService translationService) throws SystemException {
		String resultState = "";
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("testStructure: " + testStructure.getFullName());
		}

		try {
			final TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNUpdateClient updateClient = clientManager.getUpdateClient();
			File checkoutFile = getFile(testStructure);

			long revisionNumber = updateClient.doUpdate(checkoutFile, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
			resultState = translationService.translate("%svn.state.update",
					"platform:/plugin/org.testeditor.teamshare.svn") + " " + revisionNumber;
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("revisionNumber: " + revisionNumber);
			}

			revertMemoryModel(testStructure);

			SVNStatusClient statusClient = clientManager.getStatusClient();
			List<String> conflicts = checkWcState(statusClient, checkoutFile, revisionNumber);

			if (!conflicts.isEmpty()) {
				throw new SystemException(createConflictErrorMessage(conflicts, translationService));
			}
			fireEvents(testStructure);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
		return resultState;
	}

	/**
	 * Fires the events about updating a teststructure.
	 * 
	 * if the event broker is null, nothing is done.
	 * 
	 * @param testStructure
	 *            used in the vents to notify the clients.
	 */
	private void fireEvents(TestStructure testStructure) {
		if (eventBroker != null) {
			String eventTopic = TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_UPDATE_BY_MODIFY;
			eventBroker.post(eventTopic, testStructure.getFullName());
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_RESET, testStructure.getRootElement()
					.getFullName());
		}
	}

	/**
	 * Transforms a list file names in a multi line conflict error message.
	 * 
	 * @param conflicts
	 *            a list of conflicted files
	 * @param translationService
	 *            {@link TranslationService}
	 * @return error message Sting
	 */
	String createConflictErrorMessage(List<String> conflicts, TranslationService translationService) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (String conflict : conflicts) {
			sb.append(conflict).append("\n");
		}
		sb.append("\n");
		String result = translationService.translate("%svn.conflict.message",
				"platform:/plugin/org.testeditor.teamshare.svn");
		result = MessageFormat.format(result, sb.toString());
		return result;
	}

	/**
	 * Checks the working copy if there are any conflicts. Returns a list of
	 * conflicted files form the working copy. If there are no conflicts the
	 * list will be empty.
	 * 
	 * @param statusClient
	 *            subversion status client
	 * @param checkoutFile
	 *            working copy
	 * @param revisionNumber
	 *            the latest revision
	 * @return a list of conflicted files in the working copy
	 * @throws SVNException
	 *             on operation.
	 */
	List<String> checkWcState(SVNStatusClient statusClient, File checkoutFile, long revisionNumber) throws SVNException {
		final List<String> result = new ArrayList<>();
		statusClient.doStatus(checkoutFile, SVNRevision.create(revisionNumber), SVNDepth.INFINITY, false, true, false,
				false, new ISVNStatusHandler() {

					@Override
					public void handleStatus(SVNStatus arg0) throws SVNException {
						if (arg0.isConflicted()) {
							result.add(arg0.getFile().toString());
							LOGGER.error("SVN conflict at file: " + arg0.getFile());
						}

					}
				}, new ArrayList<String>());
		return result;
	}

	@Override
	public void checkout(TestProject testProject, TranslationService translationService) throws SystemException,
			TeamAuthentificationException {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("testProject: " + testProject.getFullName());
		}

		String projectName = testProject.getName();

		try {

			TeamShareConfig teamShareConfig = testProject.getTestProjectConfig().getTeamShareConfig();
			String url = ((SVNTeamShareConfig) teamShareConfig).getUrl() + "/" + projectName;

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNUpdateClient updateClient = clientManager.getUpdateClient();

			SVNURL svnUrl;
			// we use the assumption, that a local file Url begins with
			// "file:///c://tmp/test"

			if (url.startsWith(".")) { // only for junit test
				svnUrl = SVNURL.fromFile(new File(url));
			} else {
				svnUrl = SVNURL.parseURIEncoded(url);
			}

			updateClient.setEventHandler(new SVNLoggingEventHandler(listener, LOGGER));
			File dstPath = new File(testProject.getTestProjectConfig().getProjectPath()).getParentFile();

			long doCheckout = updateClient.doCheckout(svnUrl, new File(dstPath, testProject.getName()),
					SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);

			LOGGER.info("doCheckout: " + doCheckout);
		} catch (SVNCancelException canExp) {
			File dirtyPrjPath = new File(testProject.getTestProjectConfig().getProjectPath());
			LOGGER.info("Checkout canceled.");
			try {
				FileUtils.deleteDirectory(dirtyPrjPath);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			LOGGER.info("File system cleaned:" + dirtyPrjPath);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);

			int errorCode = e.getErrorMessage().getErrorCode().getCode();

			if ((errorCode == AUTHENTICATION_REQUIRED)
					|| (errorCode == CONNECTION_REFUSED || errorCode == AUTHENTICATION_REQUIRED_2)) {
				throw new TeamAuthentificationException("Authentification failed !", e);
			}

			throw new SystemException(message, e);
		}

	}

	@Override
	public String getId() {
		return SVNTeamShareConfig.SVN_TEAM_SHARE_PLUGIN_ID;
	}

	@Override
	public void delete(TestStructure testStructure, TranslationService translationService) throws SystemException {

		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNStatusClient statusClient = clientManager.getStatusClient();
		final SVNWCClient wcClient = clientManager.getWCClient();

		ISVNStatusHandler statusHandler = new ISVNStatusHandler() {

			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				if (status.getFile().isDirectory()) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Add to delete: Handle status: " + status.getNodeStatus().toString()
								+ " from file: " + status.getFile().getAbsolutePath());
					}
					wcClient.doDelete(status.getFile(), true, false, false);
				}
			}
		};

		List<String> changeLists = new ArrayList<String>();
		try {
			String localPathInProject = "";
			if (!testStructure.equals(testStructure.getRootElement())) {
				localPathInProject = "/FitNesseRoot/" + testStructure.getFullName().replaceAll("\\.", "/");
			}
			statusClient.doStatus(new File(new File(testStructure.getRootElement().getTestProjectConfig()
					.getProjectPath()).getAbsoluteFile()
					+ localPathInProject), SVNRevision.HEAD, SVNDepth.FILES, true, true, true, false, statusHandler,
					changeLists);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new SystemException(e.getMessage());
		}
		if (teamShareStatusService != null) {
			teamShareStatusService.setTeamStatusForProject(testStructure.getRootElement());
		}
	}

	@Override
	public String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException {

		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNStatusClient statusClient = clientManager.getStatusClient();

		ISVNStatusHandler statusHandler = new ISVNStatusHandler() {
			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Handle status: " + status.getNodeStatus().toString() + " "
							+ status.getNodeStatus().getID() + " from file: " + status.getFile().getAbsolutePath());
				}
				StringBuilder lcStatus = new StringBuilder(status.getFile().getAbsolutePath());
				lcStatus.append(";");
				lcStatus.append(status.getNodeStatus().toString());
				lcStatus.append(" ");
				lcStatus.append(status.getNodeStatus().getID());
				lcStatus.append("\n");
				addStatusInformation(lcStatus.toString());
			}
		};
		svnStatus = new StringBuilder("LocalFile;Status");
		List<String> changeLists = new ArrayList<String>();
		String localPathInProject = "";
		if (!testStructure.equals(testStructure.getRootElement())) {
			localPathInProject = "/FitNesseRoot/" + testStructure.getFullName().replace('.', '/');
		}

		String pathInProject = new File(testStructure.getRootElement().getTestProjectConfig().getProjectPath())
				.getAbsoluteFile() + localPathInProject;

		try {
			statusClient.doStatus(new File(pathInProject), SVNRevision.HEAD, SVNDepth.INFINITY, true, true, true,
					false, statusHandler, changeLists);
			return svnStatus.toString();
		} catch (SVNException e) {
			LOGGER.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		}

	}

	/**
	 * adds the parameter statusInfo to the variable svnStatus.
	 * 
	 * @param statusInfo
	 *            String
	 */
	private void addStatusInformation(String statusInfo) {
		svnStatus.append(statusInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testeditor.core.services.interfaces.TeamShareService#
	 * addProgressListener
	 * (org.testeditor.core.services.interfaces.ProgressListener)
	 */
	@Override
	public void addProgressListener(TestStructure testStructure, ProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public void addChild(TestStructure testStructureChild, TranslationService translationService)
			throws SystemException {

		SVNClientManager clientManager = getSVNClientManager(testStructureChild.getRootElement());
		SVNWCClient wcClient = clientManager.getWCClient();
		String fullNamePath = testStructureChild.getFullName().replaceAll("\\.", "/");

		String projectPath = testStructureChild.getRootElement().getTestProjectConfig().getProjectPath()
				.replace("\\", "/");
		File file = new File(projectPath + "/FitNesseRoot/" + fullNamePath);
		try {
			wcClient.doAdd(file, true, false, false, SVNDepth.FILES, false, false);

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Add file: " + file.getAbsolutePath() + " to local svn-client.");
			}

		} catch (SVNException e) {
			LOGGER.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
	}

	/**
	 * delegates the translation to the SVNTeamShareTranslateExceptions.class.
	 * 
	 * @param e
	 *            Exception
	 * @param translationService
	 *            TranslationService
	 * @return the translated message
	 */
	private String substitudeSVNException(SVNException e, TranslationService translationService) {
		return new SVNTeamShareTranslateExceptions().substitudeSVNException(e, translationService);
	}

	@Override
	public boolean validateConfiguration(TestProject testProject, TranslationService translationService)
			throws SystemException {
		SVNTeamShareConfig teamShareConfig = (SVNTeamShareConfig) testProject.getTestProjectConfig()
				.getTeamShareConfig();
		String url = teamShareConfig.getUrl();

		try {
			SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
			repository.setAuthenticationManager(authManager);
			repository.getRepositoryUUID(true);
			SVNNodeKind nodeKind = repository.checkPath(testProject.getName(), -1);
			if (nodeKind != SVNNodeKind.DIR) {
				throw new SystemException(translationService.translate("%svn.error.nonexistingproject",
						"platform:/plugin/org.testeditor.teamshare.svn"));
			}
		} catch (SVNException e) {
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}

		return true;
	}

	@Override
	public void disconnect(TestProject testProject, TranslationService translationService) throws SystemException {
		testProject.getTestProjectConfig().setTeamShareConfig(null);
		deleteSvnMetaData(testProject);
		List<TestStructure> childrenWithScenarios = testProject.getAllTestChildrenWithScenarios();
		for (TestStructure testStructure : childrenWithScenarios) {
			testStructure.setTeamChangeType(TeamChangeType.NONE);
		}
		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED, testProject.getFullName());
		}
	}

	/**
	 * remove the SvnMetaData from the given testProject.
	 * 
	 * @param testProject
	 *            TestProject.
	 * @return true when all svnMeatData were deleted.
	 */
	private boolean deleteSvnMetaData(TestProject testProject) {
		String svnPath = testProject.getTestProjectConfig().getProjectPath() + File.separator + ".svn";
		return deleteDirectory(new File(svnPath));
	}

	/**
	 * Deletes directory and its content.
	 * 
	 * @param file
	 *            directory or file to be deleted
	 * @return true if delete was successful else false
	 */
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
	public List<TeamChange> revert(TestStructure testStructure, TranslationService translationService)
			throws SystemException {

		final List<TeamChange> result = new ArrayList<TeamChange>();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("testStructure: " + testStructure.getFullName());
		}

		try {
			final TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNWCClient wcClient = clientManager.getWCClient();
			File fileToRevert = getFile(testStructure);

			wcClient.setEventHandler(new SVNLoggingEventHandler(listener, LOGGER) {
				@Override
				public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
					super.handleEvent(arg0, arg1);
					TeamChange teamChange = new TeamChange(getTeamChangeTypeFrom(arg0), getRelativePathFrom(arg0),
							testProject);
					// We only want the information of chnages not of events
					// like started update and others.
					if (teamChange.getTeamChangeType() != null) {
						result.add(teamChange);
					}
				}

				private String getRelativePathFrom(SVNEvent arg0) {
					return convertFileToFullname(arg0.getFile(), testProject);
				}

				private TeamChangeType getTeamChangeTypeFrom(SVNEvent arg0) {
					if (arg0.getAction().equals(SVNEventAction.REVERT)) {
						return TeamChangeType.REVERT;
					}
					return null;
				}

			});

			Collection<String> changeList = new ArrayList<String>();
			File[] filesToReverted = new File[1];
			filesToReverted[0] = fileToRevert;
			String status = getStatus(testStructure, translationService);
			String[] splits = status.split("\n");

			String searchString = "added";

			wcClient.doRevert(filesToReverted, SVNDepth.INFINITY, changeList);

			for (String split : splits) {
				if (split.contains(searchString)) {
					String fileName = split.substring(0, split.lastIndexOf(searchString) - 1);
					File fileToDeleteLc = new File(fileName);
					if (fileToDeleteLc.isDirectory()) {
						// TODO send an event asynchron to delete the
						// testStructure via the TestStructureService and delete
						// the history
					} else {
						if (fileToDeleteLc.exists()) {
							if (!fileToDeleteLc.delete()) {
								throw new SystemException("could not delete file " + fileName);
							}
						}
					}
				}
			}

			revertMemoryModel(testStructure);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
		return result;
	}

	/**
	 * Drops the Objects in the memory of this teststructure.
	 * 
	 * @param testStructure
	 *            to de dropped out of memory.
	 */
	protected void revertMemoryModel(TestStructure testStructure) {
		TestCompositeStructure testStructureToReset = (TestCompositeStructure) testStructure.getParent();
		if (testStructureToReset == null) {
			testStructureToReset = (TestCompositeStructure) testStructure;
		}
		int childCOunt = testStructureToReset.getTestChildren().size();
		testStructureToReset.setTestChildren(new ArrayList<TestStructure>());
		testStructureToReset.setChildCountInBackend(childCOunt);
	}

	@Override
	public void rename(TestStructure testStructure, String newName, TranslationService translationService)
			throws SystemException {
		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNMoveClient client = clientManager.getMoveClient();
		File src = getFile(testStructure);
		File dest = new File(src.getParentFile().getAbsolutePath() + File.separator + newName);
		LOGGER.trace("Renaming from: " + src + " to: " + dest);
		try {
			client.doMove(src, dest);
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
		}
		if (teamShareStatusService == null) {
			teamShareStatusService = context.get(TeamShareStatusService.class);
		}
		return this;
	}

	@Override
	public void addAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("call to addAdditonalFile: testStructure: " + testStructure.getFullName());
		}

		TestProject testProject = testStructure.getRootElement();

		SVNWCClient wcClient = getSVNClientManager(testProject).getWCClient();

		File file = new File(getFolderName(testStructure) + File.separator + fileName);

		try {
			final SVNStatus info = getSVNClientManager(testProject).getStatusClient().doStatus(file, false);
			if (!info.isVersioned()) {
				wcClient.doAdd(file, false, false, false, SVNDepth.INFINITY, true, true);
			}
		} catch (Exception e) {
			// TODO should be analyzed
			// org.tmatesoft.svn.core.SVNException: svn: E150002: 'file' is
			// already under version control
			LOGGER.warn(e.getMessage(), e);
		}

	}

	@Override
	public void removeAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("call to addAdditonalFile: testStructure: " + testStructure.getFullName() + " for file  "
					+ fileName);
		}

		TestProject testProject = testStructure.getRootElement();

		SVNWCClient wcClient = getSVNClientManager(testProject).getWCClient();

		File file = new File(getFolderName(testStructure) + File.separator + fileName);

		if (!file.exists()) {
			LOGGER.info("file " + fileName + " does not exists");
			return;
		}

		try {
			final SVNStatus info = getSVNClientManager(testProject).getStatusClient().doStatus(file, false);
			if (info.isVersioned()) {
				wcClient.doDelete(file, true, false, false);
			}
		} catch (Exception e) {
			// TODO should be analyzed
			// org.tmatesoft.svn.core.SVNException: svn: E150002: 'file' is
			// already under version control
			LOGGER.warn(e.getMessage(), e);
		}

	}

	@Override
	public int availableUpdatesCount(TestProject testProject) throws SystemException {
		SVNClientManager clientManager = getSVNClientManager(testProject);
		try {
			SvnGetInfo info = clientManager.getWCClient().getOperationsFactory().createGetInfo();
			info.setSingleTarget(SvnTarget.fromFile(getFile(testProject)));

			SVNRevision localRevision = SVNRevision.create(info.run().getLastChangedRevision());
			SvnLog log = clientManager.getWCClient().getOperationsFactory().createLog();

			log.addRange(SvnRevisionRange.create(SVNRevision.HEAD, SVNRevision.HEAD));
			SVNTeamShareConfig cfg = (SVNTeamShareConfig) testProject.getTestProjectConfig().getTeamShareConfig();
			log.setSingleTarget(SvnTarget.fromURL(SVNURL.parseURIEncoded(cfg.getUrl() + "/" + testProject.getName())));
			SVNLogEntry run = log.run();
			return (int) (run.getRevision() - localRevision.getNumber());
		} catch (SVNException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SystemException(e.getLocalizedMessage(), e);
		}
	}

}
