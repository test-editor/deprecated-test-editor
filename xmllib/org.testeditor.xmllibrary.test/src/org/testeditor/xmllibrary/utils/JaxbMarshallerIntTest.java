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
package org.testeditor.xmllibrary.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.testeditor.xmllibrary.domain.action.Action;
import org.testeditor.xmllibrary.domain.action.ActionGroup;
import org.testeditor.xmllibrary.domain.action.ActionGroups;
import org.testeditor.xmllibrary.domain.action.ActionName;
import org.testeditor.xmllibrary.domain.binding.ActionPart;
import org.testeditor.xmllibrary.domain.binding.ActionType;
import org.testeditor.xmllibrary.domain.binding.TechnicalBindingType;
import org.testeditor.xmllibrary.domain.binding.TechnicalBindingTypes;
import org.xml.sax.SAXException;

/**
 * Tests the marshal and unmarshal of XML files (ActionGroup and
 * TechnicalBindingType).
 */
public class JaxbMarshallerIntTest {
	private static final String PATH_ACTION_GROUPS_TEMPORARY_XML = "target" + File.separatorChar
			+ "AllActionGroups.xml";
	private static final String PATH_ACTION_GROUPS_XML = "testLibrary" + File.separatorChar + "AllActionGroups.xml";
	private static final String PATH_ACTION_GROUPS_XSD = JaxbTestHelper.getBundleLocation() + File.separatorChar
			+ "resources" + File.separatorChar + "AllActionGroups.xsd";
	private static final String PATH_TECHNICAL_BINDINGS_TEMPORARY_XML = "target" + File.separatorChar
			+ "TestTechnicalBindingTypes.xml";
	private static final String PATH_TECHNICAL_BINDINGS_XML = "testLibrary" + File.separatorChar
			+ "TechnicalBindingTypeCollection.xml";
	private static final String PATH_TECHNICAL_BINDINGS_XSD = JaxbTestHelper.getBundleLocation() + File.separatorChar
			+ "resources" + File.separatorChar + "TechnicalBindingTypeCollection.xsd";

	private static final Logger LOGGER = Logger.getLogger(JaxbMarshallerIntTest.class);

	/**
	 * this test validates the AllActionGroup.xml.
	 */
	@Test
	public void validateTestActionGroupXML() {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		boolean result = false;
		try {
			schema = schemaFactory.newSchema(new File(PATH_ACTION_GROUPS_XSD));

			Validator validator = schema.newValidator();
			validator.setErrorHandler(new XsdValidationLoggingErrorHandler());
			validator.validate(new StreamSource(new File(PATH_ACTION_GROUPS_XML)));
			result = true;
			assertTrue(result);
		} catch (SAXException e) {
			LOGGER.error("Test validate ActionGroup failed" + e.getMessage());
			fail("Test validate ActionGroup failed with exception: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Test validate ActionGroup failed" + e.getMessage());
			fail("Test validate ActionGroup failed with exception: " + e.getMessage());
		}
	}

	/**
	 * this test validates the TechnicalBindingTypes.xml.
	 */
	@Test
	public void validateTestBindingsXML() {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		boolean result = false;
		try {
			schema = schemaFactory.newSchema(new File(PATH_TECHNICAL_BINDINGS_XSD));

			Validator validator = schema.newValidator();
			validator.setErrorHandler(new XsdValidationLoggingErrorHandler());
			validator.validate(new StreamSource(new File(PATH_TECHNICAL_BINDINGS_XML)));
			result = true;
			assertTrue(result);
		} catch (SAXException e) {
			LOGGER.error("Test validate TechnicalBindingTypes failed" + e.getMessage());
			fail("Test validate TechnicalBindingTypes failedd with exception: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Test validate TechnicalBindingTypes failed" + e.getMessage());
			fail("Test validate TechnicalBindingTypes failed with exception: " + e.getMessage());
		}
	}

	/**
	 * Tests the marshall and unmarshall of technical bindings. That implies the
	 * following test steps:
	 * <ul>
	 * <li>Creating a temporary XML file (target/TestTechnicalBindingTypes.xml)
	 * based on internal Java objects (marshal)</li>
	 * <li>Reading back the temporary XML file into internal Java test objects
	 * (unmarshal)</li>
	 * <li>Comparing the content of the original objects with the detected
	 * objects</li>
	 * </ul>
	 */
	@Test
	public void testMarshalTechnicalBindings() {
		TechnicalBindingType technicalBindingType = new TechnicalBindingType();
		technicalBindingType.setId("WertSetzen");
		technicalBindingType.setName("Setze einen Wert");

		ActionPart actionPart = new ActionPart();
		actionPart.setType(ActionType.TEXT);
		actionPart.setValue("Setze einen Wert");
		actionPart.setPosition(1);
		technicalBindingType.getActionPart().add(actionPart);

		TechnicalBindingTypes technicalBindingTypes = new TechnicalBindingTypes();
		technicalBindingTypes.getTechnicalBindingType().add(technicalBindingType);
		technicalBindingTypes.setSchemaVersion(new BigDecimal("1.1"));
		try {
			JaxbMarshaller.marshal(PATH_TECHNICAL_BINDINGS_XSD, PATH_TECHNICAL_BINDINGS_TEMPORARY_XML,
					technicalBindingTypes);
		} catch (Exception e) {
			LOGGER.error("Test unmarshal failed" + e.getMessage());
			fail("Test marshal failed with exception: " + e.getMessage());

		}

		try {
			TechnicalBindingTypes newTechnicalBindingTypes = JaxbMarshaller.unmarshal(PATH_TECHNICAL_BINDINGS_XSD,
					PATH_TECHNICAL_BINDINGS_TEMPORARY_XML, TechnicalBindingTypes.class);
			assertTrue(technicalBindingType.getId().equalsIgnoreCase(
					newTechnicalBindingTypes.getTechnicalBindingType().get(0).getId()));
			assertTrue(technicalBindingType.getName().equalsIgnoreCase(
					newTechnicalBindingTypes.getTechnicalBindingType().get(0).getName()));
		} catch (Exception e) {
			LOGGER.error("Test unmarshal failed" + e.getMessage());
			fail("Test unmarshal failed with exception: " + e.getMessage());
		}
	}

	/**
	 * Tests the marshall and unmarshall of action groups. That implies the
	 * following test steps:
	 * <ul>
	 * <li>Creating a temporary XML file (target/TestActionGroups.xml) based on
	 * internal Java objects (marshal)</li>
	 * <li>Reading back the temporary XML file into internal Java test objects
	 * (unmarshal)</li>
	 * <li>Comparing the content of the original objects with the detected
	 * objects</li>
	 * </ul>
	 */

	@Test
	public void testMarshalAllActionGroups() {
		ActionGroup actionGroup = new ActionGroup();
		actionGroup.setName("Test");
		Action action = new Action();
		action.setTechnicalBindingType("techId");

		ActionName actionName = new ActionName();
		actionName.setValue("testDisplay");
		actionName.setLocator("testIt");

		action.setActionName(actionName);
		actionGroup.getAction().add(action);

		ActionGroups actionGroups = new ActionGroups();
		actionGroups.getActionGroup().add(actionGroup);
		actionGroups.setSchemaVersion(new BigDecimal("1.1"));

		try {
			JaxbMarshaller.marshal(PATH_ACTION_GROUPS_XSD, PATH_ACTION_GROUPS_TEMPORARY_XML, actionGroups);
		} catch (Exception e) {
			LOGGER.error("Test marshal failed " + e.getMessage());
			fail("Test marshal failed with exception: " + e.getMessage());

		}

		try {
			ActionGroups newActionGroups = JaxbMarshaller.unmarshal(PATH_ACTION_GROUPS_XSD,
					PATH_ACTION_GROUPS_TEMPORARY_XML, ActionGroups.class);
			assertEquals(newActionGroups.getActionGroup().get(0).getName(), actionGroups.getActionGroup().get(0)
					.getName());
			assertEquals(newActionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType(),
					actionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType());
		} catch (Exception e) {
			LOGGER.error("Test unmarshal failed" + e.getMessage());
			fail("Test unmarshal failed with exception: " + e.getMessage());
		}
	}

	/**
	 * marshal with null xsd.
	 */
	@Test
	public void testmarshalWithNullXSD() {
		File xmlFile = new File(PATH_ACTION_GROUPS_TEMPORARY_XML);
		if (xmlFile.exists()) {
			assertTrue(xmlFile.delete());
		}
		ActionGroup actionGroup = new ActionGroup();
		actionGroup.setName("Test");
		Action action = new Action();
		action.setTechnicalBindingType("techId");

		ActionName actionName = new ActionName();
		actionName.setValue("testDisplay");
		actionName.setLocator("testIt");

		action.setActionName(actionName);
		actionGroup.getAction().add(action);

		ActionGroups actionGroups = new ActionGroups();
		actionGroups.getActionGroup().add(actionGroup);
		actionGroups.setSchemaVersion(new BigDecimal("1.1"));

		try {
			JaxbMarshaller.marshal(null, PATH_ACTION_GROUPS_TEMPORARY_XML, actionGroups);
		} catch (Exception e) {
			LOGGER.error("Test marshal failed " + e.getMessage());
			fail("Test marshal failed with exception: " + e.getMessage());
		}

		try {
			ActionGroups newActionGroups = JaxbMarshaller.unmarshal(null, PATH_ACTION_GROUPS_TEMPORARY_XML,
					ActionGroups.class);
			assertEquals(newActionGroups.getActionGroup().get(0).getName(), actionGroups.getActionGroup().get(0)
					.getName());
			assertEquals(newActionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType(),
					actionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType());
		} catch (Exception e) {
			LOGGER.error("Test unmarshal failed" + e.getMessage());
			fail("Test unmarshal failed with exception: " + e.getMessage());
		}

	}

	/**
	 * marshal with null xsd.
	 */
	@Test
	public void testmarshalWitXSDemptyStringName() {
		File xmlFile = new File(PATH_ACTION_GROUPS_TEMPORARY_XML);
		if (xmlFile.exists()) {
			assertTrue(xmlFile.delete());
		}
		ActionGroup actionGroup = new ActionGroup();
		actionGroup.setName("Test");
		Action action = new Action();
		action.setTechnicalBindingType("techId");

		ActionName actionName = new ActionName();
		actionName.setValue("testDisplay");
		actionName.setLocator("testIt");

		action.setActionName(actionName);
		actionGroup.getAction().add(action);

		ActionGroups actionGroups = new ActionGroups();
		actionGroups.getActionGroup().add(actionGroup);
		actionGroups.setSchemaVersion(new BigDecimal("1.1"));

		try {
			JaxbMarshaller.marshal(" ", PATH_ACTION_GROUPS_TEMPORARY_XML, actionGroups);
		} catch (Exception e) {
			LOGGER.error("Test marshal failed " + e.getMessage());
			fail("Test marshal failed with exception: " + e.getMessage());
		}

		try {
			ActionGroups newActionGroups = JaxbMarshaller.unmarshal(" ", PATH_ACTION_GROUPS_TEMPORARY_XML,
					ActionGroups.class);
			assertEquals(newActionGroups.getActionGroup().get(0).getName(), actionGroups.getActionGroup().get(0)
					.getName());
			assertEquals(newActionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType(),
					actionGroups.getActionGroup().get(0).getAction().get(0).getTechnicalBindingType());
		} catch (Exception e) {
			LOGGER.error("Test unmarshal failed" + e.getMessage());
			fail("Test unmarshal failed with exception: " + e.getMessage());
		}

	}
}
