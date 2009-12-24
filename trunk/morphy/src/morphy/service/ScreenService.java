package morphy.service;

import java.util.TreeMap;

import morphy.Morphy;
import morphy.utils.FileUtils;
import morphy.utils.MorphyStringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScreenService {
	protected static Log LOG = LogFactory.getLog(ScreenService.class);

	private static final ScreenService singletonInstance = new ScreenService();

	public static enum Screen {
		Login, Logout, SuccessfulLogin
	};

	TreeMap<Screen, String> screenToMessage = new TreeMap<Screen, String>();

	private ScreenService() {
		screenToMessage.put(Screen.Login, MorphyStringUtils
				.replaceNewlines(FileUtils.fileAsString(Morphy.RESOURCES_DIR
						+ "/screenFiles/login.txt")));
		screenToMessage.put(Screen.Logout, MorphyStringUtils
				.replaceNewlines(FileUtils.fileAsString(Morphy.RESOURCES_DIR
						+ "/screenFiles/logout.txt")));
		screenToMessage.put(Screen.SuccessfulLogin, MorphyStringUtils
				.replaceNewlines(FileUtils.fileAsString(Morphy.RESOURCES_DIR
						+ "/screenFiles/successfulLogin.txt")));

	}

	public void dispose() {
		screenToMessage.clear();
	}

	public String getScreen(Screen screen) {
		return screenToMessage.get(screen);
	}

	public static ScreenService getInstance() {
		return singletonInstance;
	}
}
