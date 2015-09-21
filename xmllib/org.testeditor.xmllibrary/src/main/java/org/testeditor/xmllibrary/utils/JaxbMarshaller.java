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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Marshals internal objects into XML files and vise versa.
 */
public final class JaxbMarshaller {

	/**
	 * Unmarshals the given XML file into JAXB elements by using the XSD schema.
	 * 
	 * @param xsdSchema
	 *            related XSD schema
	 * @param xmlFile
	 *            XML file
	 * @param clazz
	 *            the class of the root object
	 * @param <T>
	 *            type of the returned JAXB element
	 * @return the JAXB object
	 * @throws JAXBException
	 *             JAXBException
	 * @throws SAXException
	 *             SAXException
	 */
	public static <T> T unmarshal(String xsdSchema, String xmlFile, Class<T> clazz) throws JAXBException, SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;

		if (xsdSchema != null && xsdSchema.trim().length() > 0) {
			schema = schemaFactory.newSchema(new File(xsdSchema));
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getPackage().getName());
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(schema);
		return clazz.cast(unmarshaller.unmarshal(new File(xmlFile)));
	}

	/**
	 * Marshals the JAXB element into the XML file by using the XSD schema.
	 * 
	 * @param xsdSchema
	 *            related XSD schema
	 * @param xmlFile
	 *            the new XML file
	 * @param jaxbElement
	 *            JAXB element
	 * @throws JAXBException
	 *             JAXBException
	 * @throws SAXException
	 *             SAXException
	 */
	public static void marshal(String xsdSchema, String xmlFile, Object jaxbElement) throws JAXBException, SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;

		if (xsdSchema != null && xsdSchema.trim().length() > 0) {
			schema = schemaFactory.newSchema(new File(xsdSchema));
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getClass().getPackage().getName());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setSchema(schema);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(jaxbElement, new File(xmlFile));
	}

	/**
	 * Don't create objects of this utility class.
	 */
	private JaxbMarshaller() {
	}
}