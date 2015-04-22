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

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Comparator for ActionGroup to order them by the position field or if it is
 * null by the name.
 *
 */
public class ActionGroupPositionComparator implements Comparator<ActionGroup>, Serializable {

	private static final long serialVersionUID = -5787865417074579647L;

	@Override
	public int compare(ActionGroup o1, ActionGroup o2) {
		if (o1.getSorting() == null && o2.getSorting() == null) {
			return o1.getName().compareTo(o2.getName());
		}
		if (o1.getSorting() == null) {
			return 1;
		}
		if (o2.getSorting() == null) {
			return -1;
		}
		int comp = o1.getSorting().compareTo(o2.getSorting());
		if (comp != 0) {
			return comp;
		} else {
			return o1.getName().compareTo(o2.getName());
		}
	}

}
