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
import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.e4.core.services.translation.TranslationService;

/**
 * Utiliy-class to translate some special the exceptions.
 * 
 */
@SuppressWarnings("restriction")
public class SVNTeamShareTranslateExceptions {
	/**
	 * 
	 * @param e
	 *            Exception
	 * @param translationService
	 *            the TranslationService
	 * @return the translated message.
	 */
	protected String substitudeSVNException(Exception e, TranslationService translationService) {
		String message = e.getMessage();
		String[] strings = message.split("'");

		if (message.startsWith("svn: E125002:")) {
			return translate(translationService, "%svnE125002", strings[1]);
		} else if (message.startsWith("svn: E160020:")) {
			// .i.e: message = svn: E160020: File already exists: filesystem
			// 'D:\Testeditor_12_sep\org.testeditor.teamshare.svn.test\testrepo\db',
			// transaction '1-1', path '/DemoWebTests/AllActionGroups.xml'
			return translate(translationService, "%svnE160020", strings[1], strings[5]);
		} else if (message.startsWith("svn: E170001:")) {
			return translate(translationService, "%svnE170001", strings[1]);
		} else if (message.startsWith("svn: E175005:")) {
			return translate(translationService, "%svnE175005", strings[1]);
		} else if (message.startsWith("svn: E175002:")) {
			return translate(translationService, "%svnE175002", strings[1]);
		} else if (message.startsWith("svn: E200009:")) {
			return translate(translationService, "%svnE200009", strings[1], strings[3]);
		} else if (message.startsWith("svn: E155015:")) {
			return translate(translationService, "%svnE155015", replaceTechnicalServerPath(strings[1]));
		} else if (message.startsWith("svn: E155011:")) { // out of date
			String[] path = strings[1].split(Pattern.quote(File.separator));
			return translate(translationService, "%svnE155011", path[path.length - 1]);
		} else if (message.startsWith("svn: E160024:")) {
			if (strings[1].equals("content.txt")) {
				return translate(translationService, "%svnE160024", replaceTechnicalServerPath(strings[3]));
			} else {
				return translate(translationService, "%svnE160024", replaceTechnicalServerPath(strings[1]));
			}
		}

		return e.getMessage();
	}

	/**
	 * translates the message with the given key.
	 * 
	 * @param translationService
	 *            {@link TranslationService}
	 * @param key
	 *            the key
	 * @param params
	 *            additional, optional parameters
	 * @return the translated message
	 */
	private String translate(TranslationService translationService, String key, Object... params) {
		String translatedText = translationService.translate(key, "platform:/plugin/org.testeditor.teamshare.svn");
		return MessageFormat.format(translatedText, params);
	}

	/**
	 * Extension to fix TE-1437 (Errormessage contains "/content.txt")<br/>
	 * see <a href="https://jira.akq-eng.de/browse/TE-1437">Jira Ticket</a>
	 * 
	 * We replace "/content.txt" in the message, since that is a technical
	 * detail, we don't want the user to see. The User tries to approve (commit)
	 * a TestCase. The later is identified by its name, which technically
	 * relates to the matching directory. The Exception is thrown when the
	 * commit fails on the first file in the test case's directory, which
	 * happens to be content.txt. That file is transparent to the user and thus
	 * should not appear in the final error message.
	 * 
	 * @param original
	 *            the original Message from the Server Exception
	 * 
	 * @return the corrected message without the "/content.txt" part
	 */
	private String replaceTechnicalServerPath(String original) {
		// replace both slash and backslash. We are not sure, how the Path will
		// change, if the svn server is located on a windows machine.
		return original.replace("/content.txt", "").replace("\\content.txt", "");
	}

}
