/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008-2010  http://code.google.com/p/morphy-chess-server/
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

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import morphy.user.UserSession;
import morphy.utils.john.DBConnection;
import morphy.utils.john.ServerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserService implements Service {
	protected static Log LOG = LogFactory.getLog(UserService.class);

	private static final UserService singletonInstance = new UserService();

	public String generateAnonymousHandle() {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<4;i++) {
			s.append((char)(65+new Random().nextInt(26)));
		}
		return "Guest" + s.toString();
	}
	
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
		String announcement = "    **ANNOUNCEMENT** " + message;
		for (UserSession session : getLoggedInUsers()) {
			session.send(announcement);
		}
	}
	
	public boolean isRegistered(String username) {
		try {
			DBConnection conn = new DBConnection();
			boolean hasResults = conn.executeQuery("SELECT `id` FROM `users` WHERE `username` = '" + username + "'");
			if (hasResults) {
				ResultSet results = conn.getStatement().getResultSet();
				return results.next();
			}
		} catch(Exception e) { 
			e.printStackTrace(System.err);
		}
		return false;
	}
	
	public String getTags(String username) {
		StringBuilder tags = new StringBuilder();
		ServerListManagerService service = ServerListManagerService.getInstance();
		List<ServerList> list = service.getLists();
		for(ServerList sl : list) {
			if (service.isOnList(sl, username)) {
				tags.append(sl.getTag());
			}
		}
		
		return username + tags.toString();
	}
}
