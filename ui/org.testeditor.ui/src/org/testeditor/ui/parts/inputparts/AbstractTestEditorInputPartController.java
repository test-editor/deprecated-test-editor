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
package org.testeditor.ui.parts.inputparts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.testeditor.ui.constants.TestEditorEventConstants;

/**
 * 
 * Interface for the inoutparts on the right hand site of the view of the
 * application.
 * 
 */
public abstract class AbstractTestEditorInputPartController {

	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IEclipseContext context;
	private boolean reactOnGetFocusOnInputPart = false;

	/**
	 * clean the InputArea synchron and close them.
	 */
	public abstract void cleanViewsSynchron();

	/**
	 * disables the views.
	 */
	public abstract void disableViews();

	/**
	 * caches the input temporary.
	 * 
	 * @param obj
	 *            Object
	 */
	@Inject
	@Optional
	public abstract void cacheInput(
			@UIEventTopic(TestEditorEventConstants.CACHE_TEST_COMPONENT_OF_PART_TEMPORARY) Object obj);

	/**
	 * this method removes the connection to the controller of the view of the
	 * TestFlow.
	 */
	public abstract void removeTestEditorController();

	/**
	 * will be called by the eventBroker, if a GET_FOCUS_ON_INPUT_PART-event is
	 * sent.
	 * 
	 * @param obj
	 *            Object
	 */
	@Inject
	@Optional
	public void getFocusOnInputPart(@UIEventTopic(TestEditorEventConstants.GET_FOCUS_ON_INPUT_PART) Object obj) {
		if (reactOnGetFocusOnInputPart) {
			if (obj == null || obj.toString().equalsIgnoreCase("")) {
				disableViews();
				removeTestEditorController();
			}
			cleanViewsAsynchron();
		}
	}

	/**
	 * initialize the handler for the other part in focus event.
	 */
	@PostConstruct
	public void initialize() {
		reactOnGetFocusOnInputPart = true;
	}

	/**
	 * 
	 * @return the eventBroker.
	 */
	protected IEventBroker getEventBroker() {
		return eventBroker;
	}

	/**
	 * clean the InputArea asynchron and close them.
	 */
	public abstract void cleanViewsAsynchron();
}
