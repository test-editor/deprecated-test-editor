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
package org.testeditor.ui.adapter;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Adapter for IStructuredSelection to build tests.
 *
 */
public class StructuredSelectionAdapter implements IStructuredSelection {

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Object getFirstElement() {
		return null;
	}

	@Override
	public Iterator iterator() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public List toList() {
		return null;
	}

}
