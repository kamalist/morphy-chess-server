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

	public void setProperty(PreferenceKeys key, Object value) {
		super.setProperty(key.toString(), value);
	}

	public String getString(PreferenceKeys key) {
		return getString(key.toString());
	}

	public int getInt(PreferenceKeys key) {
		return getInt(key.toString());
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
