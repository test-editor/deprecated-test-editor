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
package org.testeditor.ui.mocks;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

/**
 * 
 * Mock of the Eclipse PreferencesService.
 * 
 */
public class PreferencesServiceMock implements PreferencesService {

	@Override
	public String[] getUsers() {
		return null;
	}

	@Override
	public Preferences getUserPreferences(String name) {
		return null;
	}

	@Override
	public Preferences getSystemPreferences() {
		return new Preferences() {

			@Override
			public void sync() throws BackingStoreException {

			}

			@Override
			public void removeNode() throws BackingStoreException {

			}

			@Override
			public void remove(String key) {

			}

			@Override
			public void putLong(String key, long value) {

			}

			@Override
			public void putInt(String key, int value) {

			}

			@Override
			public void putFloat(String key, float value) {

			}

			@Override
			public void putDouble(String key, double value) {

			}

			@Override
			public void putByteArray(String key, byte[] value) {

			}

			@Override
			public void putBoolean(String key, boolean value) {

			}

			@Override
			public void put(String key, String value) {

			}

			@Override
			public Preferences parent() {
				return null;
			}

			@Override
			public boolean nodeExists(String pathName) throws BackingStoreException {
				return false;
			}

			@Override
			public Preferences node(String pathName) {
				return null;
			}

			@Override
			public String name() {
				return null;
			}

			@Override
			public String[] keys() throws BackingStoreException {
				return new String[] {};
			}

			@Override
			public long getLong(String key, long def) {
				return 0;
			}

			@Override
			public int getInt(String key, int def) {
				return 0;
			}

			@Override
			public float getFloat(String key, float def) {
				return 0;
			}

			@Override
			public double getDouble(String key, double def) {
				return 0;
			}

			@Override
			public byte[] getByteArray(String key, byte[] def) {
				return null;
			}

			@Override
			public boolean getBoolean(String key, boolean def) {
				return false;
			}

			@Override
			public String get(String key, String def) {
				return null;
			}

			@Override
			public void flush() throws BackingStoreException {

			}

			@Override
			public void clear() throws BackingStoreException {

			}

			@Override
			public String[] childrenNames() throws BackingStoreException {
				return null;
			}

			@Override
			public String absolutePath() {
				return null;
			}
		};
	}

}
