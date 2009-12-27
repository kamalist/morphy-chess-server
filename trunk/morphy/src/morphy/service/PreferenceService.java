/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008,2009  http://code.google.com/p/morphy-chess-server/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package morphy.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import morphy.Morphy;
import morphy.properties.PreferenceKeys;

import org.apache.commons.configuration.PropertiesConfiguration;

public class PreferenceService extends PropertiesConfiguration implements
		Service {
	public static final String PROPERTIES_FILE = Morphy.RESOURCES_DIR
			+ "/morphy.properties";
	private static final PreferenceService singletonInstance = new PreferenceService();

	public static PreferenceService getInstance() {
		return singletonInstance;
	}

	private PreferenceService() {
		super();
		setAutoSave(false);
		loadDefaults();
		File propertiesFile = new File(PROPERTIES_FILE);
		FileInputStream fileIn = null;

		try {
			if (propertiesFile.exists()) {
				load(fileIn = new FileInputStream(PROPERTIES_FILE));
			} else {

			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error loading properties file: " + PROPERTIES_FILE, t);
		} finally {
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (Throwable t) {
				}
			}
		}
	}

	public void dispose() {
		save();
	}

	public int getInt(PreferenceKeys key) {
		return getInt(key.toString());
	}

	public String getString(PreferenceKeys key) {
		return getString(key.toString());
	}

	@Override
	public void save() {
		FileOutputStream fileOut = null;
		try {
			save(fileOut = new FileOutputStream(PROPERTIES_FILE));
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error saving properties file: " + PROPERTIES_FILE, t);
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (Throwable t) {
				}
			}
		}
	}

	public void setProperty(PreferenceKeys key, Object value) {
		super.setProperty(key.toString(), value);
	}

	protected void loadDefaults() {
		setProperty(PreferenceKeys.SocketConnectionServicePorts, 5000);
		setProperty(PreferenceKeys.SocketConnectionServiceHost, "127.0.0.1");
		setProperty(PreferenceKeys.SocketConnectionServiceCharEncoding, "UTF-8");
		setProperty(
				PreferenceKeys.SocketConnectionServiceMaxCommunicationBytes,
				400 * 4);
		setProperty(PreferenceKeys.SocketConnectionLineDelimiter, "\n\r");

		setProperty(PreferenceKeys.ThreadServiceCoreThreads, 100);
		setProperty(PreferenceKeys.ThreadServiceMaxThreads, 1000);
		setProperty(PreferenceKeys.ThreadServiceKeepAlive, 120);

		setProperty(PreferenceKeys.ValidUserNameRegEx, "\\w{3,15}");
	}
}
