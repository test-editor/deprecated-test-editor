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
package org.testeditor.ui.uiscanner.webscanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.testeditor.ui.uiscanner.allactiongroup.Action;
import org.testeditor.ui.uiscanner.allactiongroup.ActionGroup;
import org.testeditor.ui.uiscanner.allactiongroup.ActionGroups;
import org.testeditor.ui.uiscanner.allactiongroup.ActionName;
import org.testeditor.ui.uiscanner.allactiongroup.Argument;

/**
 * Class to write and read the ElementList and ActionList files.
 * 
 * @author dkuhlmann
 * 
 */
public class ScannerReadAndWriter {

	/**
	 * Generate the ElementList from the elements.
	 * 
	 * @param elements
	 *            List<ScannerWebElement> elements to write into the
	 *            ElementList.
	 * @return String: ElementList as a String.
	 */
	public String generateELementList(List<UiScannerWebElement> elements) {
		StringBuilder result = new StringBuilder();
		result.append("#UiScanner" + System.lineSeparator());
		for (UiScannerWebElement element : elements) {
			result.append(element.getLocator() + "=" + element.getTechnicalID() + System.lineSeparator());
		}
		return result.toString();
	}

	/**
	 * Generate a AllActionGroup in XML with all actions for the elements in the
	 * List<ScannerWebElement>.
	 * 
	 * @param elements
	 *            List<ScannerWebElement>: with all the elements.
	 * @return String: with the ActionGroupList
	 * @throws JAXBException
	 *             JAXBException
	 * @throws IOException
	 *             IOException
	 */
	public String generateActionGroup(List<UiScannerWebElement> elements) throws JAXBException, IOException {
		StringBuilder result = new StringBuilder();

		ActionGroups actionGroups = new ActionGroups();

		ActionGroup actionGroup = new ActionGroup();
		actionGroup.setName("UiScanner");

		for (UiScannerWebElement scannerWebElement : elements) {
			scannerWebElement.setDefaultActions();
			for (String technicalBinidingType : scannerWebElement.getActions()) {
				Action action = new Action();
				ActionName actionName = new ActionName();
				actionName.setLocator(scannerWebElement.getLocator());
				actionName.setValue(scannerWebElement.getName());
				action.setActionName(actionName);
				action.setTechnicalBindingType(technicalBinidingType);
				if (technicalBinidingType.equals(UiScannerConstants.ACTION_SELECT_VALUE)) {
					Argument argument = new Argument();
					argument.setId("argument_" + scannerWebElement.getLocator());
					argument.setValue(scannerWebElement.getValue());
					action.setArgument(argument);
				}
				actionGroup.addAction(action);
			}
		}

		actionGroups.addActionGroup(actionGroup);

		// create JAXB context and instantiate marshaller
		JAXBContext context = JAXBContext.newInstance(ActionGroups.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		// Write to System.out
		// m.marshal(actionGroups, System.out);
		// Write to File
		File file = new File("actiongroup.xml");
		file.delete();
		m.marshal(actionGroups, file);
		FileReader fileReader = new FileReader("actiongroup.xml");
		String line = new String();
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line + System.lineSeparator());
		}
		bufferedReader.close();
		file.delete();
		return result.toString();
	}
}
