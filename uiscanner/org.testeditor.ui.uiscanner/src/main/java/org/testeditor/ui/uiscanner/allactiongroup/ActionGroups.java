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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für anonymous complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActionGroup" type="{}ActionGroup" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="schemaVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" fixed="1.1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "actionGroup" })
@XmlRootElement(name = "ActionGroups")
public class ActionGroups {

	@XmlElement(name = "ActionGroup", required = true)
	private List<ActionGroup> actionGroup;
	@XmlAttribute(name = "schemaVersion", required = true)
	private BigDecimal schemaVersion;

	/**
	 * Gets the value of the actionGroup property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the actionGroup property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getActionGroup().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ActionGroup }
	 * 
	 * @return List<ActionGroup>
	 */
	public List<ActionGroup> getActionGroup() {
		if (actionGroup == null) {
			actionGroup = new ArrayList<ActionGroup>();
		}
		return this.actionGroup;
	}

	/**
	 * addActionGroup.
	 * 
	 * @param actionGroup2
	 *            ActionGroup
	 */
	public void addActionGroup(ActionGroup actionGroup2) {
		if (actionGroup == null) {
			actionGroup = new ArrayList<ActionGroup>();
		}

		actionGroup.add(actionGroup2);

	}

	/**
	 * Ruft den Wert der schemaVersion-Eigenschaft ab.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public BigDecimal getSchemaVersion() {
		if (schemaVersion == null) {
			return new BigDecimal("1.1");
		} else {
			return schemaVersion;
		}
	}

	/**
	 * Legt den Wert der schemaVersion-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link BigDecimal }
	 * 
	 */
	public void setSchemaVersion(BigDecimal value) {
		this.schemaVersion = value;
	}

}
