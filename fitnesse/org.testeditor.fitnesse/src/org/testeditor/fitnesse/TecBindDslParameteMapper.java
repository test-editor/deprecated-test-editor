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
package org.testeditor.fitnesse;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Stores the mapping between technical binding and dsl parameters.
 * 
 * @author karsten
 */
public class TecBindDslParameteMapper{
	
	private String dsl;
	private String technicalBinding;
	private Map<String,Integer> technicalBindingIndexMap;

	/**
	 * 
	 * @return the dsl
	 */
	protected String getDsl() {
		return dsl;
	}

	/**
	 * 
	 * @return technicalBinding paramterlist
	 */
	protected String getTechnicalBinding() {
		return technicalBinding;
	}


	/**
	 * @param dsl FitNesse DSL 
	 * @param technicalBinding Order of the TechnicalBinding Parameters
	 */
	public TecBindDslParameteMapper(String dsl, String technicalBinding) {
		this.dsl = dsl;
		this.technicalBinding = technicalBinding;
		prepareIndexMap();
	}

	/**
	 * Creates an IndexMap.
	 */
	private void prepareIndexMap() {
		technicalBindingIndexMap = new HashMap<String, Integer>();
		String[] strings = technicalBinding.split(",");
		for (int i = 0; i < strings.length; i++) {
			technicalBindingIndexMap.put(strings[i].trim(), i);
		}
	}

	/**
	 * 
	 * @param dslPart to lookup the index.
	 * @return the index of the technical Binding for the dslPart
	 */
	public int getIndexFor(String dslPart) {
		return technicalBindingIndexMap.get(dslPart);
	}
}