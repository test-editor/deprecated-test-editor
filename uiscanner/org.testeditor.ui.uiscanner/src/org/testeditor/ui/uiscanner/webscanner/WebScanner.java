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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testeditor.ui.uiscanner.expressions.Expression;

/**
 * @author dkuhlmann
 * 
 */
public class WebScanner extends Scanner {

	/**
	 * Search in the opened webDriver the WebElements after the passing filters.
	 * Possible filters: all with ID, XPath, input, button, select, checkbox,
	 * radiobutton.
	 * 
	 * @param filters
	 *            List<String> of the filters which will be searched.
	 * @param xPath
	 *            String of the XPath search.
	 * @param monitor
	 *            IProgressMonitor
	 * @return List<UiScannerWebElement> List of all founded elements
	 */
	public ArrayList<UiScannerWebElement> scanWebsite(ArrayList<String> filters, String xPath, IProgressMonitor monitor) {
		ArrayList<UiScannerWebElement> result = new ArrayList<UiScannerWebElement>();
		result = scanFullContent(filters, xPath, monitor, result);
		return result;
	}

	/**
	 * Search in the opened webDriver the WebElements after the passing filters.
	 * Possible filters: all with ID, XPath, input, button, select, checkbox,
	 * radiobutton. Scanes the first content looks after that content for
	 * iframes and scan that contents too.
	 * 
	 * * @param filters List<String> of the filters which will be searched.
	 * 
	 * @param xPath
	 *            String of the XPath search.
	 * @param monitor
	 *            IProgressMonitor
	 * 
	 * @param uiScannerWebElements
	 *            ArrayList<UiScannerWebElement> where the founded Elements
	 *            should be added.
	 * 
	 * @return List<UiScannerWebElement> List of all founded elements
	 */
	private ArrayList<UiScannerWebElement> scanFullContent(ArrayList<String> filters, String xPath,
			IProgressMonitor monitor, ArrayList<UiScannerWebElement> uiScannerWebElements) {
		uiScannerWebElements = scanCurrentContent(filters, xPath, monitor, uiScannerWebElements);
		List<WebElement> iFrames = findIframes();
		if (!iFrames.isEmpty()) {
			for (WebElement iFrame : iFrames) {
				switchToIframe(iFrame);
				uiScannerWebElements = scanCurrentContent(filters, xPath, monitor, uiScannerWebElements);
				switchToDefaultContent();
			}
		}
		return uiScannerWebElements;
	}

	/**
	 * Search in the opened webDriver the WebElements after the passing filters.
	 * Possible filters: all with ID, XPath, input, button, select, checkbox,
	 * radiobutton. Scans only the current active website content (iframes not
	 * included). To scan full content with iframes use
	 * {@link #scanFullContent(ArrayList, String, IProgressMonitor, ArrayList)}
	 * 
	 * * @param filters List<String> of the filters which will be searched.
	 * 
	 * @param xPath
	 *            String of the XPath search.
	 * @param monitor
	 *            IProgressMonitor
	 * 
	 * @param uiScannerWebElements
	 *            ArrayList<UiScannerWebElement> where the founded Elements
	 *            should be added.
	 * @return List<UiScannerWebElement> List of all founded elements
	 */
	private ArrayList<UiScannerWebElement> scanCurrentContent(ArrayList<String> filters, String xPath,
			IProgressMonitor monitor, ArrayList<UiScannerWebElement> uiScannerWebElements) {
		for (String filter : filters) {
			switch (filter) {
			case "XPath":
				uiScannerWebElements = addXPaths(uiScannerWebElements, xPath);
				monitor.worked(1);
				break;
			case "button":
				uiScannerWebElements = addButtons(uiScannerWebElements);
				monitor.worked(1);
				break;
			case "select":
				uiScannerWebElements = addSelects(uiScannerWebElements);
				monitor.worked(1);
				break;
			case "input":
				uiScannerWebElements = addInputs(uiScannerWebElements);
				monitor.worked(1);
				break;
			case "radio":
				uiScannerWebElements = addRadioButtons(uiScannerWebElements);
				monitor.worked(1);
				break;
			case "checkbox":
				uiScannerWebElements = addCheckBoxs(uiScannerWebElements);
				monitor.worked(1);
				break;
			default:
				break;
			}
		}
		if (filters.contains("unkown")) {
			uiScannerWebElements = addAllWithID(uiScannerWebElements);
			monitor.worked(1);
		}
		return uiScannerWebElements;
	}

	/**
	 * adds all new xPath WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @param xPath
	 *            String
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addXPaths(ArrayList<UiScannerWebElement> result, String xPath) {
		for (WebElement elem : findXpath(xPath)) {
			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_ID) == null
					|| elem.getAttribute(UiScannerConstants.ATTRIBUTE_ID).equals("")) {
				result.add(new UiScannerWebElement(UiScannerConstants.TYP_XPATH, xPath));
			} else {
				result = elemIntoList(result, UiScannerConstants.TYP_XPATH, elem);
			}
		}
		return result;
	}

	/**
	 * adds all new WebElements with IDs to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addAllWithID(ArrayList<UiScannerWebElement> result) {
		for (WebElement elem : findAll()) {
			if (!elem.getAttribute(UiScannerConstants.ATTRIBUTE_ID).equals("")) {
				result = elemIntoList(result, UiScannerConstants.TYP_ALL, elem);
			}
		}
		return result;
	}

	/**
	 * adds all new Button WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addButtons(ArrayList<UiScannerWebElement> result) {
		for (WebElement elem : findButtons()) {
			result = elemIntoList(result, UiScannerConstants.TYP_BUTTON, elem);
		}
		return result;
	}

	/**
	 * adds all new Input WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addInputs(ArrayList<UiScannerWebElement> result) {

		for (WebElement elem : findInputs()) {
			result = elemIntoList(result, UiScannerConstants.TYP_INPUT, elem);
		}
		return result;
	}

	/**
	 * adds all new RadioButton WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addRadioButtons(ArrayList<UiScannerWebElement> result) {
		for (WebElement elem : findRadioElement()) {
			result = elemIntoList(result, UiScannerConstants.TYP_RADIO, elem);
		}
		return result;
	}

	/**
	 * adds all new Checkbox WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addCheckBoxs(ArrayList<UiScannerWebElement> result) {
		for (WebElement elem : findCheckbox()) {
			result = elemIntoList(result, UiScannerConstants.TYP_CHECKBOX, elem);
		}
		return result;
	}

	/**
	 * adds all new Selects WebElements to the given List.
	 * 
	 * @param result
	 *            List<UiScannerWebElement> to add the new WebElements.
	 * @return List<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> addSelects(ArrayList<UiScannerWebElement> result) {
		for (WebElement elem : findSelects()) {
			result = elemIntoList(result, UiScannerConstants.TYP_SELECT, elem);
			result.get(result.size() - 1).setValue(getValuesOfElement(elem));
		}
		return result;
	}

	/**
	 * Attach the WebElement into the UiScannerWebElement list with the type.
	 * 
	 * @param list
	 *            List<UiScannerWebElement> to put in the WebElement.
	 * @param typ
	 *            String type of the the Element.
	 * @param elem
	 *            WebElement that should be insert into the list.
	 * @return List<UiScannerWebElement> The list with the inserted WebElement.
	 */
	private ArrayList<UiScannerWebElement> elemIntoList(ArrayList<UiScannerWebElement> list, String typ, WebElement elem) {
		list.add(new UiScannerWebElement(typ, elem.getAttribute(UiScannerConstants.ATTRIBUTE_ID)));
		if (typ.equals(UiScannerConstants.TYP_SELECT)) {
			list.get(list.size() - 1).setValue(getValuesOfElement(elem));
		}
		return list;
	}

	/**
	 * Find and return all checkbox WebElement on the website.
	 * 
	 * @return List<WebElement> List of all founded checkbox Elements
	 */
	@Override
	protected List<WebElement> findCheckbox() {
		List<WebElement> result = new ArrayList<WebElement>();
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_INPUT + "[@id] ")) {
			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE) != null) {
				if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(UiScannerConstants.TAGNAME_CHECKBOX)) {
					result.add(elem);
				}
			}
		}
		return result;
	}

	/**
	 * Find and return all radiobuttons on the website.
	 * 
	 * @return List<WebElement> founded radiobuttons.
	 */
	@Override
	protected List<WebElement> findRadioElement() {
		List<WebElement> result = new ArrayList<WebElement>();
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_INPUT + "[@id] ")) {

			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE) != null) {
				if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(UiScannerConstants.TAGNAME_RADIO)) {
					result.add(elem);
				}
			}
		}
		return result;
	}

	/**
	 * Find and return all input WebElements on the website. That includes the
	 * inputs, textarea and fieldset.
	 * 
	 * @return List<WebElement> List of all founded input Elements
	 */
	@Override
	protected List<WebElement> findInputs() {
		List<WebElement> result = new ArrayList<WebElement>();
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_INPUT + "[@id] ")) {
			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE) != null) {
				if ((!elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(UiScannerConstants.TAGNAME_SUBMIT))
						&& (!elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(
								UiScannerConstants.TAGNAME_RESET))
						&& (!elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(
								UiScannerConstants.TAGNAME_CHECKBOX))
						&& (!elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(
								UiScannerConstants.TAGNAME_RADIO))) {
					result.add(elem);
				}
			}
		}
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_TEXTAREA + "[@id] ")) {
			result.add(elem);
		}
		return result;
	}

	/**
	 * Find and return all button WebElements on the website. That includes the
	 * submits, rests, div buttons and normal buttons.
	 * 
	 * @return List<WebElement> List of all founded Button Elements
	 */
	@Override
	protected List<WebElement> findButtons() {
		List<WebElement> result = new ArrayList<WebElement>();
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_BUTTON + "[@id]")) {
			result.add(elem);
		}
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_INPUT + "[@id] ")) {
			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE) != null) {
				if ((elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE).contains(UiScannerConstants.TAGNAME_SUBMIT))
						|| (elem.getAttribute(UiScannerConstants.ATTRIBUTE_TYPE)
								.contains(UiScannerConstants.TAGNAME_RESET))) {
					result.add(elem);
				}
			}
		}
		for (WebElement elem : findXpath("//" + UiScannerConstants.TAGNAME_DIV + "[@id] ")) {
			if (elem.getAttribute(UiScannerConstants.ATTRIBUTE_CLASS) != null
					&& elem.getAttribute(UiScannerConstants.ATTRIBUTE_CLASS).contains("button")) {
				result.add(elem);
			}
		}
		return result;
	}

	/**
	 * Finds and return all Select WebElements on the Website.
	 * 
	 * @return List<WebElement>
	 */
	@Override
	protected List<WebElement> findSelects() {
		return findXpath("//" + UiScannerConstants.TAGNAME_SELECT + "[@id]");
	}

	/**
	 * Finds and return all WebElements with an ID on the Website.
	 * 
	 * @return List<WebElement>
	 */
	@Override
	protected List<WebElement> findAll() {
		return findXpath("//*[@id]");
	}

	/**
	 * Finds and return all WebElements with the given xPath on the Website.
	 * 
	 * @param xPath
	 *            String for the xPath search.
	 * @return List<WebElement>
	 */
	@Override
	protected List<WebElement> findXpath(String xPath) {
		return getWebDriver().findElements(By.xpath(xPath));
	}

	/**
	 * Finds and return all iFrame WebElements.
	 * 
	 * @return List<WebElement>
	 */
	@Override
	protected List<WebElement> findIframes() {
		return findXpath("//iframe");
	}

	/**
	 * scan a website with the filters.
	 * 
	 * @param elems
	 *            ArrayList<UiScannerWebElement> where the filterd Elems should
	 *            be added
	 * @param exprs
	 *            HashMap<String, Expression>
	 * @param filters
	 *            List<String> filters
	 * @param xPath
	 *            String
	 * @return the new filtered list.
	 */
	public ArrayList<UiScannerWebElement> scanFilteredWithExpression(ArrayList<UiScannerWebElement> elems,
			HashMap<String, Expression> exprs, List<String> filters, String xPath) {
		for (UiScannerWebElement elem : scanUnfilterdWithExpression(exprs)) {
			if (filters.contains(elem.getTyp())) {
				elems.add(elem);
			}
		}
		if (filters.contains(UiScannerConstants.TYP_XPATH)) {
			for (WebElement elem : findXpath(xPath)) {
				elems = elemIntoList(elems, UiScannerConstants.TYP_XPATH, elem);
			}
		}
		return elems;
	}

	/**
	 * scans a WebSite and sort it with the given Expression.
	 * 
	 * @param exprs
	 *            HashMap<String, Expression>
	 * @return ArrayList<UiScannerWebElement>
	 */
	public ArrayList<UiScannerWebElement> scanUnfilterdWithExpression(HashMap<String, Expression> exprs) {
		ArrayList<UiScannerWebElement> elems = new ArrayList<UiScannerWebElement>();
		for (WebElement elem : findAll()) {
			elems = makeUiScannerElementWithExpression(exprs, elems, elem);
		}
		List<WebElement> iFrames = findIframes();
		if (!iFrames.isEmpty()) {
			for (WebElement iFrame : iFrames) {
				switchToIframe(iFrame);
				for (WebElement webelem : findAll()) {
					elems = makeUiScannerElementWithExpression(exprs, elems, webelem);
				}
				switchToDefaultContent();
			}
		}
		return elems;
	}

	/**
	 * makes out the given WebElement a UiScannerElement and adds it into the
	 * given list.
	 * 
	 * @param exprs
	 *            HashMap<String, Expression>
	 * @param elems
	 *            ArrayList<UiScannerWebElement>
	 * @param elem
	 *            WebElement
	 * @return ArrayList<UiScannerWebElement>
	 */
	private ArrayList<UiScannerWebElement> makeUiScannerElementWithExpression(HashMap<String, Expression> exprs,
			ArrayList<UiScannerWebElement> elems, WebElement elem) {
		for (String key : exprs.keySet()) {
			if (exprs.get(key).evalute(elem)) {
				return elemIntoList(elems, key, elem);
			}
		}
		return elems;
	}
}
