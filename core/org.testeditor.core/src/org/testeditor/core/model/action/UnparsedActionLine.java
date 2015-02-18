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
package org.testeditor.core.model.action;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link UnparsedActionLine} extends the {@link IAction}.
 * 
 * 
 * @author llipinski
 */
public class UnparsedActionLine extends AbstractAction {

	private List<Argument> arguments = new ArrayList<Argument>();
	private ArrayList<String> texts = new ArrayList<String>();
	private String error;
	private Object[] errorParams;
	public static final String UNCORRECT_ARGUMENT = "uncorrect_argument";
	public static final String ACTION_NOT_FOUND = "action_not_found";

	/**
	 * copy-constructor.
	 * 
	 * @param unparsedActionLine
	 *            UnparsedActionLine
	 */
	public UnparsedActionLine(UnparsedActionLine unparsedActionLine) {
		texts = unparsedActionLine.getTexts();
	}

	/**
	 * constructor.
	 * 
	 * @param line
	 *            String
	 */
	public UnparsedActionLine(String line) {
		arguments = new ArrayList<Argument>();
		texts = new ArrayList<String>();
		texts.add(line);
	}

	/**
	 * constructor.
	 * 
	 * @param line
	 *            String
	 * @param error
	 *            String
	 * @param errorParams
	 *            Object[]
	 */
	public UnparsedActionLine(String line, String error, Object... errorParams) {

		arguments = new ArrayList<Argument>();
		texts = new ArrayList<String>();
		texts.add(line);
		this.error = error;
		this.errorParams = errorParams;
	}

	@Override
	public TechnicalBindingType getTechnicalBindingType() {
		return new TechnicalBindingType();
	}

	@Override
	public List<Argument> getArguments() {
		return arguments;
	}

	@Override
	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	@Override
	public ArrayList<String> getTexts() {
		return texts;
	}

	@Override
	public ArrayList<String> getSourceCode() {
		return texts;
	}

	/**
	 * 
	 * @return the actual error.
	 */
	public String getError() {
		return error;
	}

	@Override
	public ArrayList<TextType> getTextTypes() {
		ArrayList<TextType> types = new ArrayList<TextType>();
		types.add(TextType.UNPARSED_ACTION_lINE);
		return types;
	}

	/**
	 * 
	 * @return the parameters for the error
	 */
	public Object[] getErrorParams() {
		return errorParams;
	}

	@Override
	public List<String> getParameters() {
		return null;
	}

}
