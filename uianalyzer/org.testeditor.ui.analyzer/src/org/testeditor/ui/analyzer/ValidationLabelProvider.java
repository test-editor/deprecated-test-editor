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
package org.testeditor.ui.analyzer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.testeditor.ui.analyzer.errormodel.Error;
import org.testeditor.ui.analyzer.errormodel.ErrorContainer;

/**
 * 
 * LabelProvider to display Icons in the ValidationView.
 * 
 */
public class ValidationLabelProvider extends BaseLabelProvider implements ILabelProvider {

	private Image containerImage;
	private Image errorImage;

	/**
	 * Constructor creates the Images used in the view.
	 */
	public ValidationLabelProvider() {
		Bundle bundle = FrameworkUtil.getBundle(getClass()).getBundleContext().getBundle();
		containerImage = ImageDescriptor.createFromURL(bundle.getEntry("/icons/error_log.gif")).createImage();
		errorImage = ImageDescriptor.createFromURL(bundle.getEntry("/icons/error_tsk.gif")).createImage();
	}

	@Override
	public void dispose() {
		containerImage.dispose();
		errorImage.dispose();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Error) {
			return errorImage;
		}
		if (element instanceof ErrorContainer) {
			return containerImage;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		return element.toString();
	}

}
