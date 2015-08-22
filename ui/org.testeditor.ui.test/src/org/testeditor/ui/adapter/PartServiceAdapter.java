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

import java.util.Collection;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;

/**
 * 
 * Adapter Implementation of EPartService for Testpurpose.
 *
 */
public class PartServiceAdapter implements EPartService {

	@Override
	public void addPartListener(IPartListener listener) {
	}

	@Override
	public void removePartListener(IPartListener listener) {
	}

	@Override
	public void activate(MPart part) {
	}

	@Override
	public void activate(MPart part, boolean requiresFocus) {
	}

	@Override
	public void requestActivation() {
	}

	@Override
	public void bringToTop(MPart part) {
	}

	@Override
	public MPart findPart(String id) {
		return null;
	}

	@Override
	public Collection<MPart> getParts() {
		return null;
	}

	@Override
	public MPart getActivePart() {
		return null;
	}

	@Override
	public boolean isPartVisible(MPart part) {
		return false;
	}

	@Override
	public MPart createPart(String id) {
		return null;
	}

	@Override
	public MPlaceholder createSharedPart(String id) {
		return null;
	}

	@Override
	public MPlaceholder createSharedPart(String id, boolean force) {
		return null;
	}

	@Override
	public MPart showPart(String id, PartState partState) {
		return null;
	}

	@Override
	public MPart showPart(MPart part, PartState partState) {
		return null;
	}

	@Override
	public void hidePart(MPart part) {
	}

	@Override
	public void hidePart(MPart part, boolean force) {
	}

	@Override
	public Collection<MPart> getDirtyParts() {
		return null;
	}

	@Override
	public boolean savePart(MPart part, boolean confirm) {
		return false;
	}

	@Override
	public boolean saveAll(boolean confirm) {
		return true;
	}

	@Override
	public Collection<MInputPart> getInputParts(String inputUri) {
		return null;
	}

	@Override
	public void switchPerspective(MPerspective perspective) {
	}

	@Override
	public boolean isPartOrPlaceholderInPerspective(String arg0, MPerspective arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
