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
 * Comparator for ActionElement to order them by the position field.
 *
 */
public class ActionElementPositionComparator implements Comparator<ActionElement>, Serializable {

	private static final long serialVersionUID = -3798394682611961506L;

	@Override
	public int compare(ActionElement o1, ActionElement o2) {
		if (o2.getPosition() == null && o1.getPosition() == null) {
			return 0;
		}
		if (o1.getPosition() == null) {
			return 1;
		}
		if (o2.getPosition() == null) {
			return -1;
		}
		return o1.getPosition().compareTo(o2.getPosition());
	}

}
