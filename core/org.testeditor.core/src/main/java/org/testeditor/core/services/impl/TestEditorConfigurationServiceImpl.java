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
package org.testeditor.core.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.testeditor.core.constants.TestEditorGlobalConstans;
import org.testeditor.core.services.interfaces.ServiceLookUpForTest;
import org.testeditor.core.services.interfaces.TestEditorConfigurationService;
import org.testeditor.core.util.FileLocatorService;

/**
 * 
 * Implementation of the TestEditorConfigurationService.
 * 
 */
public class TestEditorConfigurationServiceImpl implements TestEditorConfigurationService {

	private static final String RESET_APP_PROPERTY = "resetApp";
	private static final Logger LOGGER = Logger.getLogger(TestEditorConfigurationServiceImpl.class);
	public static final String ID_TE_PROPERTIES = "org.testeditor.config.service";
	private static final String ID_CONFIGURATION = "org.testeditor.config.service.configinternal";
	static final String SLIM_CMD_PREFIX = "SLIM_CMD_VAR_";
	private static final String WS_VERSION_ID = "TE_WS_VERSION";
	private static final String CURRENT_WS_VERSION = "1.1.0";
	private FileLocatorService fileLocatorService;

	/**
	 * Described in Bug TE-760 and TE-1038 it doesn't work on all operating
	 * system to quate a path with whitespace's before the testserver creates
	 * the command. A better way is to replace the whitespace's with %20. This
	 * method checks for paths to log4j and if they contain whitespace's. Only
	 * in that case it will replace the whitespace's with %20.
	 * 
	 * @param replaceStr
	 *            path to log4j
	 * @return fixed Path to log4j
	 */
	protected String fixWhiteSpaceOnPreferenceString(String replaceStr) {
		String result = replaceStr;
		if (replaceStr.indexOf(' ') > -1) {
			result = replaceStr.replaceAll(" ", "%20");
			LOGGER.info("Found CMD-Pattern Path with whitespaces and fixing it to: " + result);
		}
		return result;
	}

	@Override
	public void exportGlobalVariablesToSystemProperties(boolean override) throws BackingStoreException {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES);
		setDefaultVariablesIfNotset();
		String[] keysOfSystemPreferences = prefs.keys();
		for (String key : keysOfSystemPreferences) {
			if (override) {
				updatePair(key, prefs.get(key, ""));
			} else {
				if (System.getProperty(key) == null) {
					updatePair(key, prefs.get(key, ""));
				}
			}
		}

		System.setProperty(SLIM_CMD_PREFIX + TestEditorGlobalConstans.LOG4J_PATH_VARIABLE,
				fixWhiteSpaceOnPreferenceString(getLogPath()));
		try {
			System.setProperty(SLIM_CMD_PREFIX + TestEditorGlobalConstans.DEFINE_VARIABLE_IEWEBDRIVERSERVER,
					fixWhiteSpaceOnPreferenceString(getDefaultWebDriverServer()));
			System.setProperty(SLIM_CMD_PREFIX + TestEditorGlobalConstans.DEFINE_VARIABLE_CHROMEWEBDRIVERSERVER,
					fixWhiteSpaceOnPreferenceString(getChromeWebDriverServer()));
		} catch (IOException e) {
			LOGGER.error("Error Reading Config from preferencestrore.", e);
			throw new RuntimeException("Error Reading Config from preferencestroree.", e);
		}
	}

	/**
	 * sets the default-Values for the needed variables, if the variable not
	 * exists.
	 * 
	 * @throws BackingStoreException
	 *             on accessing the PreferenceStore
	 */
	private void setDefaultVariablesIfNotset() throws BackingStoreException {
		Set<String> keys = getGlobalVariables().keySet();

		updateKeyWithValueIfEmpty(keys, TestEditorGlobalConstans.DEFINE_WAITS_AFTER_TEST_STEP, "1");
		updateKeyWithValueIfEmpty(keys, TestEditorGlobalConstans.PATH_BROWSER, "");
		updateKeyWithValueIfEmpty(keys, TestEditorGlobalConstans.SVN_COMMENT_DEFAULT, "");
	}

	/**
	 * Checks if the key is already in the preference store. If not it stores a
	 * default value.
	 * 
	 * @param keys
	 *            actual used in the
	 * @param key
	 *            to be checked
	 * @param value
	 *            default value for the key.
	 */
	private void updateKeyWithValueIfEmpty(Set<String> keys, String key, String value) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES);
		if (!keys.contains(key)) {
			updatePair(key, prefs.get(key, value));
		}
	}

	/**
	 * 
	 * @return to the log4j config.
	 */
	private String getLogPath() {
		return Platform.getLocation().toFile().getAbsolutePath() + File.separatorChar
				+ ".metadata/testEditorLog4j/log4j.xml";
	}

	/**
	 * 
	 * @return the default webdriver-server for the IE-fixture
	 * @throws IOException
	 *             while file operation
	 */
	private String getDefaultWebDriverServer() throws IOException {
		return getIeWebDribverName();
	}

	/**
	 * <pre>
	 * Returns the absolute file path of the ieDriver.
	 * 
	 * Hint: Only the IEDriverServer_32.exe will be used. The IEDriverServer_64.exe is in the moment too slow.
	 * 
	 * example:
	 * 32 bit -> IEDriverServer_32.exe
	 * 
	 * </pre>
	 * 
	 * @throws IOException
	 *             will be thrown if file is not found.
	 * 
	 * @return the absolute path of file of found iedriver
	 * 
	 */
	private String getIeWebDribverName() throws IOException {
		// set IEWebDriver path
		FileLocatorService fileLocatorService = ServiceLookUpForTest.getService(FileLocatorService.class);
		File filePathOfIeWebDriverServer = new File(
				fileLocatorService.findBundleFileLocationAsString("org.testeditor.fixture.lib"), "/");
		return new File(filePathOfIeWebDriverServer, "IEDriverServer_32.exe").getAbsolutePath();
	}

	/**
	 * <pre>
	 * Returns the absolute file path of the Chrome Driver.
	 * 
	 * </pre>
	 * 
	 * @throws IOException
	 *             will be thrown if file is not found.
	 * 
	 * @return the absolute path of file of found chrome driver
	 * 
	 */
	private String getChromeWebDriverServer() throws IOException {
		FileLocatorService fileLocatorService = ServiceLookUpForTest.getService(FileLocatorService.class);
		File filePathOfChromeWebDriverServer = new File(
				fileLocatorService.findBundleFileLocationAsString("org.testeditor.fixture.lib"), "/");
		return new File(filePathOfChromeWebDriverServer, "ChromeDriver_32.exe").getAbsolutePath();
	}

	@Override
	public void updatePair(String key, String value) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES);
		prefs.put(key, value);
		System.setProperty(key, value);
	}

	@Override
	public void storeChanges() throws BackingStoreException {
		InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES).flush();
	}

	@Override
	public Map<String, String> getGlobalVariables() throws BackingStoreException {
		Map<String, String> result = new HashMap<String, String>();
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES);
		String[] keys = prefs.keys();
		for (String key : keys) {
			result.put(key, prefs.get(key, ""));
		}
		LOGGER.info(keys.length + " global Variables loaded");
		return result;
	}

	@Override
	public void clearKey(String key) throws BackingStoreException {
		if (key != null && !key.isEmpty() && System.getProperty(key) != null) {
			IEclipsePreferences node = InstanceScope.INSTANCE.getNode(ID_TE_PROPERTIES);
			node.remove(key);
			if (System.clearProperty(key) == null) {
				System.clearProperty(SLIM_CMD_PREFIX + key);
			}
		}
	}

	@Override
	public void setResetApplicationState(boolean resetState) throws BackingStoreException {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_CONFIGURATION);
		prefs.put(RESET_APP_PROPERTY, String.valueOf(resetState));
		prefs.put(WS_VERSION_ID, CURRENT_WS_VERSION);
		prefs.flush();
	}

	@Override
	public boolean isResetApplicationState() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID_CONFIGURATION);
		if (!prefs.get(WS_VERSION_ID, "UNKNOWN").equals(CURRENT_WS_VERSION)) {
			return true;
		}
		return prefs.getBoolean(RESET_APP_PROPERTY, false);
	}

	@Override
	public void initializeSystemProperties() throws IOException {
		String bundleLocation = fileLocatorService.findBundleFileLocationAsString("org.testeditor.fixture.lib");
		String swtBotBundle = fileLocatorService.findBundleFileLocationAsString("org.testeditor.agent.swtbot");
		if (bundleLocation != null) {
			System.setProperty("FIXTURE_LIB_BUNDLE_PATH", bundleLocation);
		}
		if (swtBotBundle != null) {
			System.setProperty("SWT_BOT_AGENT_BUNDLE_PATH", swtBotBundle);
		}
		System.setProperty("APPLICATION_WORK", Platform.getLocation().toOSString());
	}

	/**
	 * Binds a the file locator service to this service.
	 * 
	 * @param fileLocatorService
	 *            used in this service.
	 */
	public void bind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = fileLocatorService;
		LOGGER.trace("Wired up: " + fileLocatorService);
	}

	/**
	 * Unbind the service, if it is removed from the system.
	 * 
	 * @param fileLocatorService
	 *            to be removed.
	 */
	public void unbind(FileLocatorService fileLocatorService) {
		this.fileLocatorService = null;
	}

}
