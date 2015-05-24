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
package org.testeditor.ui.handlers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.testeditor.core.model.teststructure.TestStructure;
import org.testeditor.ui.parts.testExplorer.TestExplorer;

/**
 * 
 * Mock of the TestExplorer for CanExecute Handler Test.
 * 
 */
public class TestExplorerMock extends TestExplorer {

	private List<TestStructure> selection;

	/**
	 * 
	 * @param list
	 *            to be used as the Selection.
	 */
	public TestExplorerMock(List<TestStructure> list) {
		super(null);
		this.selection = list;
	}

	@Override
	public IStructuredSelection getSelection() {
		return new TreeSelection() {
			@Override
			public Iterator<TestStructure> iterator() {
				return selection.iterator();
			}

			@Override
			public Object getFirstElement() {
				return selection.get(0);
			}

			@Override
			public int size() {
				return selection.size();
			}

			@Override
			public boolean isEmpty() {
				return false;
			}
		};
	}

}
