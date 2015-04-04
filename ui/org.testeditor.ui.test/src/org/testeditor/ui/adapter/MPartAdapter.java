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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.commands.MBindingContext;
import org.eclipse.e4.ui.model.application.commands.MHandler;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MExpression;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;

/**
 * 
 * Adapter for the MPart to be used as a Mock for Tests.
 * 
 */
public class MPartAdapter implements MPart {

	private String label;
	private boolean dirty;
	private boolean onTop;
	private Map<String, String> persistedSate = new HashMap<String, String>();

	@Override
	public Object getWidget() {
		return null;
	}

	@Override
	public void setWidget(Object value) {

	}

	@Override
	public Object getRenderer() {
		return null;
	}

	@Override
	public void setRenderer(Object value) {

	}

	@Override
	public boolean isToBeRendered() {
		return false;
	}

	@Override
	public void setToBeRendered(boolean value) {
	}

	@Override
	public boolean isOnTop() {
		return onTop;
	}

	@Override
	public void setOnTop(boolean value) {
		this.onTop = value;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean value) {
	}

	@Override
	public MElementContainer<MUIElement> getParent() {
		return null;
	}

	@Override
	public void setParent(MElementContainer<MUIElement> value) {

	}

	@Override
	public String getContainerData() {
		return null;
	}

	@Override
	public void setContainerData(String value) {

	}

	@Override
	public MPlaceholder getCurSharedRef() {
		return null;
	}

	@Override
	public void setCurSharedRef(MPlaceholder value) {

	}

	@Override
	public MExpression getVisibleWhen() {
		return null;
	}

	@Override
	public void setVisibleWhen(MExpression value) {

	}

	@Override
	public String getAccessibilityPhrase() {
		return null;
	}

	@Override
	public void setAccessibilityPhrase(String value) {

	}

	@Override
	public String getLocalizedAccessibilityPhrase() {
		return null;
	}

	@Override
	public String getElementId() {
		return "";
	}

	@Override
	public void setElementId(String value) {

	}

	@Override
	public Map<String, String> getPersistedState() {
		return persistedSate;
	}

	@Override
	public List<String> getTags() {
		return null;
	}

	@Override
	public String getContributorURI() {
		return null;
	}

	@Override
	public void setContributorURI(String value) {

	}

	@Override
	public Map<String, Object> getTransientData() {
		return null;
	}

	@Override
	public String getContributionURI() {
		return null;
	}

	@Override
	public void setContributionURI(String value) {

	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public void setObject(Object value) {

	}

	@Override
	public IEclipseContext getContext() {
		return null;
	}

	@Override
	public void setContext(IEclipseContext value) {

	}

	@Override
	public List<String> getVariables() {
		return null;
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String value) {
		this.label = value;
	}

	@Override
	public String getIconURI() {
		return null;
	}

	@Override
	public void setIconURI(String value) {

	}

	@Override
	public String getTooltip() {
		return null;
	}

	@Override
	public void setTooltip(String value) {

	}

	@Override
	public String getLocalizedLabel() {
		return null;
	}

	@Override
	public String getLocalizedTooltip() {
		return null;
	}

	@Override
	public List<MHandler> getHandlers() {
		return null;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean value) {
		this.dirty = value;
	}

	@Override
	public List<MBindingContext> getBindingContexts() {
		return null;
	}

	@Override
	public List<MMenu> getMenus() {
		return null;
	}

	@Override
	public MToolBar getToolbar() {
		return null;
	}

	@Override
	public void setToolbar(MToolBar value) {

	}

	@Override
	public boolean isCloseable() {
		return false;
	}

	@Override
	public void setCloseable(boolean value) {

	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void setDescription(String value) {

	}

	@Override
	public String getLocalizedDescription() {
		return null;
	}

	@Override
	public void updateLocalization() {
	}

}
