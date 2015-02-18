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
package org.testeditor.ui.uiscanner.expressions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;

/**
 * Reader class for the Expression files.
 * 
 * @author dkuhlmann
 * 
 */
public class ExpressionReader {

	private File file;
	private FileReader fileReader;
	private PushbackReader pushbackReader;

	/**
	 * read the file from the filePath and returns a HashMap with all typs and
	 * their Expressions.
	 * 
	 * @param filename
	 *            String: FilePath
	 * @return HashMap<String, Expression>: String Type of the Expression
	 * @throws IOException
	 *             IOException
	 * @throws ExpressionException
	 *             ExpressionException
	 */
	public HashMap<String, Expression> readCheck(String filename)
			throws IOException, ExpressionException {
		HashMap<String, Expression> result = new HashMap<>();
		String str = new String("");
		file = new File(filename);
		fileReader = new FileReader(file);
		pushbackReader = new PushbackReader(fileReader);
		int c;
		while ((c = pushbackReader.read()) != -1) {
			str += (char) c;
		}
		str = delAllWhitespaces(str);
		String[] rows = str.split(";");
		for (String row : rows) {
			result.put(row.split("=", 2)[0],
					getExpression(row.split("=", 2)[1]));
		}
		return result;
	}

	/**
	 * Removes all Tabs Whitespaces and the Enters.
	 * 
	 * @param str
	 *            String where the Whitespaces should be removed.
	 * @return String: copy of the String.
	 */
	private String delAllWhitespaces(String str) {
		String result = new String();
		int count = 0;
		do {
			if (str.charAt(count) != '\t' && str.charAt(count) != '\r'
					&& str.charAt(count) != '\n' && str.charAt(count) != ' ') {
				result += str.charAt(count);
			}
			count++;
		} while (count != str.length());
		return result;
	}

	/**
	 * converts the given String into a Expression.
	 * 
	 * @param str
	 *            String to be converted into a Expression.
	 * @return Expression
	 * @throws ExpressionException
	 *             ExpressionException
	 */
	private Expression getExpression(String str) throws ExpressionException {
		Expression expression = null;
		if (str.startsWith("!")) {
			expression = new ExpressionNot();
			expression.addExpression(getExpression(str.substring(1)));
		} else {
			String exp = str.substring(1, str.length() - 1);
			if (exp.startsWith("(")) {
				Expression expLeft = getExpression(getExpString(exp));
				exp = exp.substring(getExpString(exp).length(), exp.length());
				do {
					switch (exp.charAt(0)) {
					case '&':
						expression = makeAnd(expression);
						break;
					case '|':
						expression = makeOr(expression);
						break;
					}
					exp = exp.substring(1, exp.length());
					if (expLeft != null) {
						expression.addExpression(expLeft);
						expLeft = null;
					}
					expression.addExpression(getExpression(getExpString(exp)));
					exp = exp.substring(getExpString(exp).length(),
							exp.length());
				} while (exp.length() != 0);
			} else {
				expression = getBaseExpression(exp);
			}
		}
		return expression;
	}

	/**
	 * Make a ExpressionAnd if the given Expression is not already a
	 * ExpressionAnd.
	 * 
	 * @param expression
	 *            ExpressionAnd
	 * @return Expression
	 * @throws ExpressionException
	 *             ExpressionException
	 */
	private Expression makeAnd(Expression expression)
			throws ExpressionException {
		if (expression == null) {
			expression = new ExpressionAnd();
		} else {
			if (!(expression instanceof ExpressionAnd)) {
				throw new ExpressionException();
			}
		}
		return expression;
	}

	/**
	 * Make a ExpressionOr if the given Expression is not already a
	 * ExpressionOr.
	 * 
	 * @param expression
	 *            ExpressionOr
	 * @return Expression
	 * @throws ExpressionException
	 *             ExpressionException
	 */
	private Expression makeOr(Expression expression) throws ExpressionException {
		if (expression == null) {
			expression = new ExpressionOr();
		} else {
			if (!(expression instanceof ExpressionOr)) {
				throw new ExpressionException();
			}
		}
		return expression;
	}

	/**
	 * returns the String for a Expression.
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	private String getExpString(String str) {
		StringBuilder result = new StringBuilder();
		int count = 0, barcels = 0;
		do {
			if (str.charAt(count) == '!') {
				result.append(str.charAt(count));
				count++;
			}
			if (str.charAt(count) == '(') {
				barcels++;
			} else if (str.charAt(count) == ')') {
				barcels--;
			}
			result.append(str.charAt(count));
			count++;
		} while (barcels != 0);
		return result.toString();
	}

	/**
	 * turns the String into a ExpressionBase.
	 * 
	 * @param str
	 *            String of the Expression.
	 * @return ExpressionBase
	 */
	private Expression getBaseExpression(String str) {
		if (str.contains("=")) {
			String[] strs = str.split("=");
			return new ExpressionBaseEqual(strs[0], strs[1]);
		} else {
			String[] strs = str.split("%");
			return new ExpressionBaseContains(strs[0], strs[1]);
		}
	}
}
