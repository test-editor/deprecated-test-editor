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
package org.testeditor.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * Core Bundle to initialize the Core Component. With: - Configuration for Log4J
 * 
 */
public class CoreActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(CoreActivator.class);

	private File log4jconfiguration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		File wsDir = Platform.getLocation().toFile();

		File metaInfdir = new File(wsDir.getAbsolutePath() + File.separator + ".metadata");

		File configDir = new File(metaInfdir + File.separator + "testEditorLog4j");
		log4jconfiguration = new File(configDir.getAbsolutePath() + File.separator + "log4j.xml");

		if (!log4jconfiguration.exists()) {
			createFile(bundleContext, configDir);
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db
				.parse(new InputSource(new InputStreamReader(new FileInputStream(log4jconfiguration), "UTF-8")));

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression expr = xpath.compile("//appender/param[@name = 'file']");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList paramNodes = (NodeList) result;
		for (int i = 0; i < paramNodes.getLength(); i++) {

			NamedNodeMap attributes = paramNodes.item(i).getAttributes();

			Node valueItem = attributes.getNamedItem("value");
			String appenderName = paramNodes.item(i).getParentNode().getAttributes().getNamedItem("name")
					.getTextContent();

			valueItem.setTextContent((metaInfdir + File.separator + "logs" + File.separator + appenderName + ".log")
					.replace("\\", "/"));

		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult updatedXml = new StreamResult(new File(log4jconfiguration.getAbsolutePath()));
		transformer.transform(source, updatedXml);

		DOMConfigurator.configure(log4jconfiguration.getAbsolutePath());

		LOGGER.debug("CoreActivator initialized Logging");
	}

	/**
	 * Creates the Log4j Config file to the workspace.
	 * 
	 * @param bundleContext
	 *            to find the template
	 * @param configDir
	 *            to store the log4j config in.
	 */
	private void createFile(BundleContext bundleContext, File configDir) {
		if (!configDir.exists() && !configDir.mkdir()) {
			if (!configDir.getParentFile().mkdir()) {
				configDir.getParentFile().getParentFile().mkdir();
				configDir.getParentFile().mkdir();
			}
			configDir.mkdir();
		}
		// copy this file from the bundle
		URL bundleConfURL = bundleContext.getBundle().getEntry("log4j.xml");

		try {
			String bundelConfFile = FileLocator.toFileURL(bundleConfURL).getFile();

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(bundelConfFile), "UTF-8"));
			PrintWriter writer = new PrintWriter(log4jconfiguration, "UTF-8");

			String line;
			// copy the file content in bytes
			while ((line = in.readLine()) != null) {
				writer.write(line + System.getProperty("line.separator"));
			}
			in.close();
			writer.close();

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}

}
