package morphy.service;

import java.util.Map;
import java.util.TreeMap;

import morphy.user.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserService implements Service {
	protected static Log LOG = LogFactory.getLog(UserService.class);

	private static final UserService singletonInstance = new UserService();

	Map<String, UserSession> userNameToSessionMap = new TreeMap<String, UserSession>();

	private UserService() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialized UserService.");
		}
	}

	public void sendAnnouncement(String message) {
		String announcement = "Announcement: " + message;
		for (UserSession session : getLoggedInUsers()) {
			session.send(announcement);
		}
	}

	public int getLoggedInUserCount() {
		return userNameToSessionMap.keySet().size();
	}

	public UserSession[] getLoggedInUsers() {
		return userNameToSessionMap.values().toArray(new UserSession[0]);
	}

	public UserSession getUserSession(String userName) {
		return userNameToSessionMap.get(userName.toLowerCase());
	}

	public boolean isLoggedIn(String userName) {
		return userNameToSessionMap.get(userName.toLowerCase()) != null;
	}

	public void removeLoggedInUser(UserSession userSession) {
		if (userSession.getUser() != null
				&& userSession.getUser().getUserName() != null) {
			userNameToSessionMap.remove(userSession.getUser().getUserName()
					.toLowerCase());
		}
	}

	public void addLoggedInUser(UserSession userSessopm) {
		userNameToSessionMap.put(userSessopm.getUser().getUserName()
				.toLowerCase(), userSessopm);
	}

	public static UserService getInstance() {
		return singletonInstance;
	}

	public void dispose() {
		userNameToSessionMap.clear();
	}
}
