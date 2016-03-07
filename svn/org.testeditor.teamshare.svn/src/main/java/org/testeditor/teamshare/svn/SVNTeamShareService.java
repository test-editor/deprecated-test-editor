/*******************************************************************************
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.testeditor.core.constants.TestEditorCoreEventConstants;
import org.testeditor.core.exceptions.SystemException;
import org.testeditor.core.exceptions.TeamAuthentificationException;
import org.testeditor.core.model.team.TeamShareConfig;
import org.testeditor.core.model.teststructure.TestCompositeStructure;
import org.testeditor.core.model.teststructure.TestProject;
import org.testeditor.core.model.teststructure.TestProjectConfig;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.core.services.interfaces.ProgressListener;
import org.testeditor.core.services.plugins.TeamShareServicePlugIn;
import org.testeditor.core.services.plugins.TeamShareStatusServicePlugIn;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
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
import org.tmatesoft.svn.core.internal.wc17.db.ISVNWCDb.SVNWCDbOpenMode;
import org.tmatesoft.svn.core.internal.wc17.db.SVNWCDb;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNMoveClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnGetInfo;

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
	public static final String[] IGNORE_LIST = { "ErrorLogs", "testProgress", "RecentChanges", "testResults",
			".DS_Store", ".svn" };

	private static final Logger logger = Logger.getLogger(SVNTeamShareService.class);

	private StringBuilder svnStatus;

	private ProgressListener listener;

	private IEventBroker eventBroker;

	private TeamShareStatusServicePlugIn teamShareStatusService;

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
		logger.trace("Creating SVNClientManager for: " + testProject);
		ISVNAuthenticationManager authManager = getAuthManager(testProject);
		SVNClientManager clientManger = SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), authManager);
		return clientManger;
	}

	/**
	 * Creates an Authentication manager for the test project.
	 * 
	 * @param testProject
	 *            with svn configuration.
	 * @return ISVNAuthenticationManager to access the svn repository.
	 */
	private ISVNAuthenticationManager getAuthManager(TestProject testProject) {
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
		return authManager;
	}

	/**
	 * Returns the File for checkin to SVN or checkout from SVN.
	 * 
	 * @param testStructure
	 *            TestStructure
	 * @return File
	 * @throws SystemException
	 *             on lookup the file of the TestStructure.
	 */
	private File getFile(TestStructure testStructure) throws SystemException {
		try {
			return new File(testStructure.getUrl().toURI());
		} catch (URISyntaxException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	@Override
	public void share(TestProject testProject, TranslationService translationService, String svnComment)
			throws SystemException {

		logger.trace("call to share: TestProject '" + testProject.getFullName() + "'");

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

			clientManager.getCommitClient().setEventHandler(new SVNLoggingEventHandler(listener, logger));

			setIgnoreList(clientManager);

			// useGlobalIgnores must be set to TRUE, otherwise ignore list will
			// not work.
			SVNCommitInfo doImport = clientManager.getCommitClient().doImport(new File(projectPath), svnUrl, svnComment,
					new SVNProperties(), true, false, SVNDepth.INFINITY);

			logger.trace("doImport: " + doImport);

			// delete directory for checkout from repository
			FileUtils.deleteDirectory(new File(projectPath));

			// checkout
			SVNUpdateClient updateClient = clientManager.getUpdateClient();
			long doCheckout = updateClient.doCheckout(svnUrl,
					new File(testProject.getTestProjectConfig().getProjectPath()), SVNRevision.HEAD, SVNRevision.HEAD,
					SVNDepth.INFINITY, false);

			logger.trace("doCheckout: " + doCheckout);

		} catch (SVNException e) {
			logger.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new SystemException(e.getMessage(), e);
		}
		logger.trace("call to share: TestProject '" + testProject.getFullName() + "' done");

	}

	@Override
	public String approve(TestStructure testStructure, TranslationService translationService, String svnComment)
			throws SystemException {
		String resultState = "";
		logger.trace("call to approve; testStructure: '" + testStructure.getFullName() + "'");

		try {
			TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNWCClient wcClient = clientManager.getWCClient();

			File checkinFile = getFile(testStructure);

			doAdd(clientManager, wcClient, checkinFile);

			SVNCommitClient cc = clientManager.getCommitClient();
			cc.setEventHandler(new SVNLoggingEventHandler(listener, logger));
			SVNCommitInfo doCommit = cc.doCommit(new File[] { checkinFile }, false, svnComment, null, null, false, true,
					SVNDepth.INFINITY);
			if (doCommit.getNewRevision() < 0) {
				resultState = translationService.translate("%svn.state.nochanges",
						"platform:/plugin/org.testeditor.teamshare.svn");
			} else {
				resultState = translationService.translate("%svn.state.approve",
						"platform:/plugin/org.testeditor.teamshare.svn") + " " + doCommit.getNewRevision();
			}

			updateSvnstate(testStructure);

			if (logger.isInfoEnabled()) {
				logger.info("CommitInfo: " + doCommit.toString());
			}
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message);
		}
		logger.trace("call to approve done; testStructure: '" + testStructure.getFullName() + "'");
		return resultState;
	}

	public void doAdd(SVNClientManager clientManager, SVNWCClient wcClient, File checkinFile) {

		String fullName = checkinFile.getAbsolutePath();

		try {
			if (!checkinFile.isDirectory()) {
				throw new IllegalArgumentException(checkinFile.getAbsolutePath() + " is not a directory.");
			}
			for (int i = 0; i < SVNTeamShareService.IGNORE_LIST.length; i++) {
				if (fullName.matches(".*" + SVNTeamShareService.IGNORE_LIST[i])) {
					return;
				}
			}
			final SVNStatus info = clientManager.getStatusClient().doStatus(checkinFile, false);
			if (!info.isVersioned()) {
				wcClient.doAdd(checkinFile, false, true, true, SVNDepth.INFINITY, false, true);
			}
			for (File file : checkinFile.listFiles()) {
				if (file.isDirectory()) {
					doAdd(clientManager, wcClient, file);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error during adding file to subversion. Message: " + e.getMessage(), e);
		}
	}

	@Override
	public String update(TestStructure testStructure, TranslationService translationService) throws SystemException {
		String resultState = "";
		logger.trace("call to update; testStructure: '" + testStructure.getFullName() + "'");

		try {
			TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNUpdateClient updateClient = clientManager.getUpdateClient();
			File checkoutFile = getFile(testStructure);

			SvnGetInfo info = clientManager.getWCClient().getOperationsFactory().createGetInfo();

			long revisionNumber = updateClient.doUpdate(checkoutFile, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);

			if (info.getRevision().getID() < revisionNumber) {
				resultState = translationService.translate("%svn.state.update",
						"platform:/plugin/org.testeditor.teamshare.svn") + " " + revisionNumber;
				logger.info("revisionNumber: " + revisionNumber);
			} else {
				resultState = translationService.translate("%svn.state.update",
						"platform:/plugin/org.testeditor.teamshare.svn") + " " + revisionNumber;
			}

			revertMemoryModel(testStructure);

			SVNStatusClient statusClient = clientManager.getStatusClient();
			List<String> conflicts = checkWcState(statusClient, checkoutFile, revisionNumber);

			if (!conflicts.isEmpty()) {
				throw new SystemException(createConflictErrorMessage(conflicts, translationService));
			}
			fireEvents(testStructure);
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
		logger.trace("call to update done; testStructure: '" + testStructure.getFullName() + "'");
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
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_RESET,
					testStructure.getRootElement().getFullName());
			eventBroker.post(TestEditorCoreEventConstants.TEAMSHARE_UPDATE,
					testStructure.getRootElement().getFullName());
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
	List<String> checkWcState(SVNStatusClient statusClient, File checkoutFile, long revisionNumber)
			throws SVNException {
		final List<String> result = new ArrayList<>();
		statusClient.doStatus(checkoutFile, SVNRevision.create(revisionNumber), SVNDepth.INFINITY, false, true, false,
				false, new ISVNStatusHandler() {

					@Override
					public void handleStatus(SVNStatus arg0) throws SVNException {
						if (arg0.isConflicted()) {
							result.add(arg0.getFile().toString());
							logger.error("SVN conflict at file: " + arg0.getFile());
						}

					}
				}, new ArrayList<String>());
		return result;
	}

	@Override
	public void checkout(TestProject testProject, TranslationService translationService)
			throws SystemException, TeamAuthentificationException {

		logger.trace("call to checkout; testProject: '" + testProject.getFullName() + "'");

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

			updateClient.setEventHandler(new SVNLoggingEventHandler(listener, logger));
			File dstPath = new File(testProject.getTestProjectConfig().getProjectPath()).getParentFile();

			long doCheckout = updateClient.doCheckout(svnUrl, new File(dstPath, testProject.getName()),
					SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);

			logger.info("doCheckout: " + doCheckout);
		} catch (SVNCancelException canExp) {
			File dirtyPrjPath = new File(testProject.getTestProjectConfig().getProjectPath());
			logger.info("Checkout canceled.");
			try {
				FileUtils.deleteDirectory(dirtyPrjPath);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("File system cleaned:" + dirtyPrjPath);
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);

			int errorCode = e.getErrorMessage().getErrorCode().getCode();

			if ((errorCode == AUTHENTICATION_REQUIRED)
					|| (errorCode == CONNECTION_REFUSED || errorCode == AUTHENTICATION_REQUIRED_2)) {
				throw new TeamAuthentificationException("Authentification failed !", e);
			}

			throw new SystemException(message, e);
		}
		logger.trace("call to checkout done; testProject: '" + testProject.getFullName() + "'");

	}

	@Override
	public String getId() {
		return SVNTeamShareConfig.SVN_TEAM_SHARE_PLUGIN_ID;
	}

	@Override
	public void delete(TestStructure testStructure, TranslationService translationService) throws SystemException {

		logger.trace("call to delete; testStructure: '" + testStructure.getFullName() + "'");

		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNStatusClient statusClient = clientManager.getStatusClient();
		final SVNWCClient wcClient = clientManager.getWCClient();

		ISVNStatusHandler statusHandler = new ISVNStatusHandler() {

			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				if (status.getFile().isDirectory()) {
					if (logger.isTraceEnabled()) {
						logger.trace("Add to delete: Handle status: " + status.getNodeStatus().toString()
								+ " from file: " + status.getFile().getAbsolutePath());
					}
					wcClient.doDelete(status.getFile(), true, false, false);
				}
			}
		};

		List<String> changeLists = new ArrayList<String>();
		try {
			statusClient.doStatus(getFile(testStructure), SVNRevision.HEAD, SVNDepth.FILES, true, true, true, false,
					statusHandler, changeLists);

			updateSvnstate(testStructure);

		} catch (SVNException e) {
			logger.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			logger.error(e);
			throw new SystemException(e.getMessage());
		}
		logger.trace("call to delete done; testStructure: '" + testStructure.getFullName() + "'");
	}

	@Override
	public String getStatus(TestStructure testStructure, TranslationService translationService) throws SystemException {

		logger.trace("call to getStatus; testStructure: '" + testStructure.getFullName() + "'");

		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNStatusClient statusClient = clientManager.getStatusClient();

		ISVNStatusHandler statusHandler = new ISVNStatusHandler() {
			@Override
			public void handleStatus(SVNStatus status) throws SVNException {
				if (logger.isTraceEnabled()) {
					logger.trace("Handle status: " + status.getNodeStatus().toString() + " "
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
			statusClient.doStatus(new File(pathInProject), SVNRevision.HEAD, SVNDepth.INFINITY, true, true, true, false,
					statusHandler, changeLists);
			logger.trace("call to getStatus done; testStructure: '" + testStructure.getFullName() + "'");
			return svnStatus.toString();
		} catch (SVNException e) {
			logger.error(e.getMessage());
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (Exception e) {
			logger.error(e.getMessage());
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

		logger.trace("call to addChild; testStructure: '" + testStructureChild.getFullName() + "'");

		SVNClientManager clientManager = getSVNClientManager(testStructureChild.getRootElement());
		SVNWCClient wcClient = clientManager.getWCClient();
		File file = getFile(testStructureChild);
		try {
			logger.trace("Start add file: " + file.getAbsolutePath() + " to local svn-client.");

			wcClient.doAdd(file, true, false, false, SVNDepth.FILES, false, false);

			updateSvnstate(testStructureChild);

		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
		logger.trace("call to addChild done; testStructure: '" + testStructureChild.getFullName() + "'");
	}

	/**
	 * send notification for update svn state.
	 * 
	 * @param testStructure
	 */
	private void updateSvnstate(TestStructure testStructure) {
		if (eventBroker != null) {
			eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_STATE_UPDATED, testStructure.getRootElement());
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

		if (eventBroker != null) {
			eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED, testProject.getFullName());

		}

		teamShareStatusService.remove(testProject);

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
	public void revert(TestStructure testStructure, TranslationService translationService) throws SystemException {

		logger.trace("call to revert; testStructure: '" + testStructure.getFullName() + "'");

		try {
			TestProject testProject = testStructure.getRootElement();

			SVNClientManager clientManager = getSVNClientManager(testProject);

			SVNWCClient wcClient = clientManager.getWCClient();
			File fileToRevert = getFile(testStructure);

			File[] filesToReverted = new File[1];
			filesToReverted[0] = fileToRevert;
			String status = getStatus(testStructure, translationService);
			String[] splits = status.split("\n");

			String searchString = "added";

			wcClient.doRevert(filesToReverted, SVNDepth.INFINITY, new ArrayList<String>());

			for (String split : splits) {
				if (split.contains(searchString)) {
					String fileName = split.substring(0, split.lastIndexOf(searchString) - 1);
					File fileToDeleteLc = new File(fileName);
					if (fileToDeleteLc.isDirectory()) {
						if (eventBroker != null) {
							String subPath = Paths.get(testStructure.getUrl().toURI())
									.relativize(fileToDeleteLc.toPath()).toString();
							if (testStructure instanceof TestProject) {
								subPath = subPath.substring(subPath.lastIndexOf(testStructure.getName()),
										subPath.length());
							}
							String deletedTSFullName = subPath.replaceAll("\\" + File.separator, ".");
							logger.trace(
									"Deleted test structure in revert operation with fullname: " + deletedTSFullName);
							eventBroker.send(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_DELETED,
									deletedTSFullName);
						}
						Files.walkFileTree(fileToDeleteLc.toPath(),
								org.testeditor.core.util.FileUtils.getDeleteRecursiveVisitor());
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
			updateSvnstate(testStructure);
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			throw new SystemException(e.getLocalizedMessage(), e);
		}
		logger.trace("call to revert done; testStructure: '" + testStructure.getFullName() + "'");
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
		logger.trace("call to rename; testStructure: '" + testStructure.getFullName() + "'");

		SVNClientManager clientManager = getSVNClientManager(testStructure.getRootElement());
		SVNMoveClient client = clientManager.getMoveClient();
		File src = getFile(testStructure);
		File dest = new File(src.getParentFile().getAbsolutePath() + File.separator + newName);
		logger.trace("Renaming from: " + src + " to: " + dest);
		try {
			client.doMove(src, dest);

			updateSvnstate(testStructure);

		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			String message = substitudeSVNException(e, translationService);
			throw new SystemException(message, e);
		}
		logger.trace("call to rename done; testStructure: '" + testStructure.getFullName() + "'");
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		if (eventBroker == null) {
			eventBroker = context.get(IEventBroker.class);
		}
		return this;
	}

	@Override
	public void addAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {

		logger.trace("call to addAdditonalFile; testStructure: " + testStructure.getFullName());

		TestProject testProject = testStructure.getRootElement();

		SVNWCClient wcClient = getSVNClientManager(testProject).getWCClient();

		File file = new File(getFile(testStructure), fileName);

		try {
			final SVNStatus info = getSVNClientManager(testProject).getStatusClient().doStatus(file, false);
			if (!info.isVersioned()) {
				wcClient.doAdd(file, false, false, false, SVNDepth.INFINITY, true, true);
			}
		} catch (Exception e) {
			// TODO should be analyzed
			// org.tmatesoft.svn.core.SVNException: svn: E150002: 'file' is
			// already under version control
			logger.warn(e.getMessage(), e);
		}
		logger.trace("done addAdditonalFile; testStructure: " + testStructure.getFullName());

	}

	@Override
	public void removeAdditonalFile(TestStructure testStructure, String fileName) throws SystemException {
		logger.trace("call to removeAdditonalFile; testStructure: " + testStructure.getFullName() + " for file  "
				+ fileName);

		TestProject testProject = testStructure.getRootElement();

		SVNWCClient wcClient = getSVNClientManager(testProject).getWCClient();

		File file = new File(getFile(testStructure), fileName);

		if (!file.exists()) {
			logger.info("file " + fileName + " does not exists");
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
			logger.warn(e.getMessage());
		}
		logger.trace("call to removeAdditonalFil deone; testStructure: '" + testStructure.getFullName() + "'");

	}

	@Override
	public int availableUpdatesCount(TestProject testProject) throws SystemException {
		logger.trace("call to availableUpdatesCount; testProject: '" + testProject.getFullName() + "'");
		SVNClientManager clientManager = getSVNClientManager(testProject);
		try {
			File rootDir = new File(testProject.getTestProjectConfig().getProjectPath() + "\\config.tpr");
			SVNRevision localRevision = clientManager.getStatusClient().doStatus(rootDir, false).getRevision();

			SVNTeamShareConfig teamShareConfig = (SVNTeamShareConfig) testProject.getTestProjectConfig()
					.getTeamShareConfig();
			String url = teamShareConfig.getUrl();
			SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
			repository.setAuthenticationManager(authManager);
			repository.getRepositoryUUID(true);
			Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, localRevision.getNumber(),
					SVNRevision.HEAD.getNumber(), true, true);
			String currentUser = System.getProperty("user.name");
			if (logEntries != null) {
				int changes = 0;
				for (SVNLogEntry logEntry : logEntries) {
					if (!logEntry.getAuthor().equals(currentUser)) {
						changes++;
					}
				}
				return changes;
			}
			return 0;
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			throw new SystemException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * @param teamShareStatusPlugin
	 *            to be binded to this service.
	 */
	public void bind(TeamShareStatusServicePlugIn teamShareStatusPlugin) {
		if (teamShareStatusPlugin.getId().equals(getId())) {
			teamShareStatusService = teamShareStatusPlugin;
		}
	}

	/**
	 * 
	 * @param teamShareStatusPlugin
	 *            on shutdown plug-in remove this service.
	 */
	public void unBind(TeamShareStatusServicePlugIn teamShareStatusPlugin) {
		if (teamShareStatusPlugin.getId().equals(getId())) {
			teamShareStatusService = null;
		}
	}

	@Override
	public boolean isCleanupNeeded(TestProject testProject) throws SystemException {

		logger.trace("start hasGlobalLock");
		SVNWCDb db = new SVNWCDb();
		db.open(SVNWCDbOpenMode.ReadWrite, (ISVNOptions) null, true, false);
		try {

			File projectFile = new File(testProject.getTestProjectConfig().getProjectPath());
			boolean value = db.isWCLocked(projectFile);
			logger.trace("done hasGlobalLock: " + value);
			return value;
		} catch (SVNException e) {
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public void cleanup(TestProject testProject) throws SystemException {

		logger.trace("start cleanUp");

		try {
			SVNWCClient wcclient = getSVNClientManager(testProject).getWCClient();
			String projectPath = testProject.getTestProjectConfig().getProjectPath();

			wcclient.doCleanup(new File(projectPath));

		} catch (SVNException e) {
			throw new SystemException(e.getMessage(), e);
		}
		logger.trace("done cleanUp");

	}

	@Override
	public Map<String, String> getAvailableReleases(TestProject testProject) throws SystemException {
		Map<String, String> result = new HashMap<String, String>();
		SVNTeamShareConfig teamShareConfig = (SVNTeamShareConfig) testProject.getTestProjectConfig()
				.getTeamShareConfig();
		try {
			String projectUrl = getProjectURL(teamShareConfig.getUrl());
			SVNRepository repo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(projectUrl));
			repo.setAuthenticationManager(getAuthManager(testProject));
			Collection<SVNDirEntry> dir = repo.getDir("", -1, null, (Collection<SVNDirEntry>) null);
			for (SVNDirEntry svnFolder : dir) {
				if (svnFolder.getName().equals("trunk")) {
					result.put(svnFolder.getName(), svnFolder.getURL().toString());
				}
				if (svnFolder.getName().equals("branches")) {
					Collection<SVNDirEntry> branches = repo.getDir(svnFolder.getRelativePath(), -1, null,
							(Collection<SVNDirEntry>) null);
					for (SVNDirEntry branch : branches) {
						result.put(branch.getName(), branch.getURL().toString());
					}
				}
			}

		} catch (SVNException e) {
			throw new SystemException(e.getMessage(), e);
		}
		return result;
	}

	protected String getProjectURL(String url) {
		if (url.lastIndexOf("trunk") > -1) {
			return url.substring(0, url.lastIndexOf("trunk"));
		}
		if (url.lastIndexOf("tags") > -1) {
			return url.substring(0, url.lastIndexOf("tags"));
		}
		if (url.lastIndexOf("branches") > -1) {
			return url.substring(0, url.lastIndexOf("branches"));
		}
		return url;
	}

	@Override
	public void switchToBranch(TestProject testProject, String url) throws SystemException {
		SVNUpdateClient updateClient = getSVNClientManager(testProject).getUpdateClient();
		try {
			updateClient.setEventHandler(new SVNLoggingEventHandler(listener, logger));
			SVNURL targetUrl = getTargetUrl(url, testProject);
			logger.info("Switch project " + testProject.getName() + " to " + targetUrl);
			updateClient.doSwitch(getFile(testProject), targetUrl, SVNRevision.HEAD, SVNRevision.HEAD,
					SVNDepth.INFINITY, true, true);
			fireEvents(testProject);
			if (eventBroker != null) {
				eventBroker.post(TestEditorCoreEventConstants.TESTSTRUCTURE_MODEL_CHANGED_RELOADED, testProject);
			}
			((SVNTeamShareConfig) testProject.getTestProjectConfig().getTeamShareConfig()).setUrl(url);

		} catch (SVNException e) {
			logger.error(e.getErrorMessage(), e);
			throw new SystemException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Builds the target url for a switch operation.
	 * 
	 * @param url
	 *            of the target branch
	 * @param testProject
	 *            used to determine the name
	 * @return svn url
	 * @throws SVNException
	 *             on problems building the url.
	 */
	protected SVNURL getTargetUrl(String url, TestProject testProject) throws SVNException {
		return SVNURL.parseURIEncoded(url + "/" + testProject.getName());
	}

	@Override
	public String getCurrentBranch(TestProject testProject) {
		String branch = null;

		if (testProject.getTestProjectConfig().getTeamShareConfig() != null) {
			String url = ((SVNTeamShareConfig) (testProject.getTestProjectConfig().getTeamShareConfig())).getUrl();
			if (url != null && url.indexOf("branches") != -1) {
				return url.substring(url.indexOf("branches") + "branches".length() + 1);
			}
		}
		return branch;
	}

	@Override
	public boolean isDirty(TestProject testProject) throws SystemException {
		final List<File> fileList = new ArrayList<File>();
		try {
			SVNClientManager svnClientManager = SVNClientManager.newInstance();
			File rootFile = getFile(testProject);

			svnClientManager.getStatusClient().doStatus(rootFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false,
					false, false, new ISVNStatusHandler() {
						@Override
						public void handleStatus(SVNStatus status) throws SVNException {
							SVNStatusType statusType = status.getContentsStatus();
							if (statusType != SVNStatusType.STATUS_IGNORED) {
								fileList.add(status.getFile());
							}
						}
					}, null);
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
			throw new SystemException(e.getMessage(), e);
		}
		return fileList.size() > 0;
	}
}
