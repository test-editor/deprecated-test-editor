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
package org.testeditor.ui.parts.editor.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * 
 * This class extends the ByteArrayTransfer to match the project specific
 * requirements. This class should transfer the stored TestComponents and the
 * TestProject.
 * 
 */
public abstract class TestEditorTransfer extends ByteArrayTransfer {

	private static final Logger LOGGER = Logger.getLogger(TestEditorTransfer.class);

	/**
	 * checks the object.
	 * 
	 * @param object
	 *            Object
	 * @return true, if it's instance of TestEditorTestFlowDataTransferContainer
	 *         and the TestProject is not null and their are TestComponents
	 */
	boolean checkMyType(Object object) {
		if (object == null || !(object instanceof TestEditorTestDataTransferContainer)) {
			return false;
		}
		TestEditorTestDataTransferContainer myTypes = (TestEditorTestDataTransferContainer) object;
		if (myTypes == null || myTypes.getTestProjectName() == null || myTypes.getStoredTestComponents() == null
				|| myTypes.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * validates the object.
	 * 
	 * @param object
	 *            the Object
	 * @return the result of the method checkMyType
	 */
	@Override
	protected boolean validate(Object object) {
		return checkMyType(object);
	}

	/**
	 * This implementation of <code>javaToNative</code> converts
	 * TestEditorTestFlowDataTransferContainer represented by a java
	 * <code>TestEditorTestFlowDataTransferContainer</code> to a platform
	 * specific representation.
	 * 
	 * @param object
	 *            a java <code>TestEditorTestFlowDataTransferContainer</code>
	 *            containing TestEditorTestFlowDataTransferContainer
	 * @param transferData
	 *            an empty <code>TransferData</code> object that will be filled
	 *            in on return with the platform specific format of the data
	 * 
	 *            see also Transfer#javaToNative
	 */

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof TestEditorTestDataTransferContainer)) {
			return;
		}

		if (isSupportedType(transferData)) {
			TestEditorTestDataTransferContainer myTypes = (TestEditorTestDataTransferContainer) object;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream o = new ObjectOutputStream(out);
				o.writeObject(myTypes);
				byte[] buffer = out.toByteArray();
				o.close();
				super.javaToNative(buffer, transferData);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
	}

	/**
	 * This implementation of <code>nativeToJava</code> converts a platform
	 * specific representation of the
	 * {@link TestEditorTestDataTransferContainer} to a java <code>String</code>
	 * .
	 * 
	 * @param transferData
	 *            the platform specific representation of the data to be
	 *            converted
	 * @return a java <code>TestEditorTestFlowDataTransferContainer</code>
	 *         containing TestEditorTestFlowDataTransferContainer if the
	 *         conversion was successful; otherwise null
	 * 
	 *         see also Transfer#nativeToJava
	 * 
	 */

	@Override
	public Object nativeToJava(TransferData transferData) {

		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null) {
				return null;
			}

			try {
				ByteArrayInputStream b = new ByteArrayInputStream(buffer);
				ObjectInputStream o = new ObjectInputStream(b);
				return o.readObject();
			} catch (IOException ex) {
				LOGGER.error("Error nativeToJava", ex);
				return null;
			} catch (ClassNotFoundException e) {
				LOGGER.error("Error nativeToJava", e);
			}
		}
		return null;
	}
}