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

package org.testeditor.ui.uiscanner.allactiongroup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Action complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Action">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}actionName" minOccurs="0"/>
 *         &lt;element ref="{}argument" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="technicalBindingType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sort" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Action", propOrder = { "actionName", "argument" })
public class Action {

	private ActionName actionName;
	private Argument argument;
	@XmlAttribute(name = "technicalBindingType", required = true)
	private String technicalBindingType;
	@XmlAttribute(name = "sort")
	private Integer sort;

	/**
	 * Ruft den Wert der actionName-Eigenschaft ab.
	 * 
	 * @return possible object is {@link ActionName }
	 * 
	 */
	public ActionName getActionName() {
		return actionName;
	}

	/**
	 * Legt den Wert der actionName-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link ActionName }
	 * 
	 */
	public void setActionName(ActionName value) {
		this.actionName = value;
	}

	/**
	 * Ruft den Wert der argument-Eigenschaft ab.
	 * 
	 * @return possible object is {@link Argument }
	 * 
	 */
	public Argument getArgument() {
		return argument;
	}

	/**
	 * Legt den Wert der argument-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link Argument }
	 * 
	 */
	public void setArgument(Argument value) {
		this.argument = value;
	}

	/**
	 * Ruft den Wert der technicalBindingType-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTechnicalBindingType() {
		return technicalBindingType;
	}

	/**
	 * Legt den Wert der technicalBindingType-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTechnicalBindingType(String value) {
		this.technicalBindingType = value;
	}

	/**
	 * Ruft den Wert der sort-Eigenschaft ab.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * Legt den Wert der sort-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setSort(Integer value) {
		this.sort = value;
	}

}
