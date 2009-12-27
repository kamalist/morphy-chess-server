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

import java.util.Map;
import java.util.TreeMap;

import morphy.user.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserService implements Service {
	protected static Log LOG = LogFactory.getLog(UserService.class);

	private static final UserService singletonInstance = new UserService();

	public static UserService getInstance() {
		return singletonInstance;
	}

	Map<String, UserSession> userNameToSessionMap = new TreeMap<String, UserSession>();

	private UserService() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialized UserService.");
		}
	}

	public void addLoggedInUser(UserSession userSessopm) {
		userNameToSessionMap.put(userSessopm.getUser().getUserName()
				.toLowerCase(), userSessopm);
	}

	public void dispose() {
		userNameToSessionMap.clear();
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

	public void sendAnnouncement(String message) {
		String announcement = "Announcement: " + message;
		for (UserSession session : getLoggedInUsers()) {
			session.send(announcement);
		}
	}
}
