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

import java.util.ArrayList;
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

	public static UserService getInstance() {
		return singletonInstance;
	}

	public boolean isAdmin(String username) {
		final ServerListManagerService s = ServerListManagerService
				.getInstance();
		return s.isOnList(s.getList("admin"), username);
	}
	
	/**
	 * Returns whether the player with the given username is idle.
	 * Returns TRUE if the player is not online.
	 * Otherwise returns if the user is over 5 mins idle or has the busy string set.
	 */
	public boolean isIdle(String username) {
		UserSession s = getUserSession(username);
		if (s == null)
			return true;

		return s.getIdleTimeMillis() > 1000 * 60 * 5
				|| !s.getUser().getUserVars().getVariables().get("busy")
						.equals("");
	}

	public String generateAnonymousHandle() {
		StringBuilder s = new StringBuilder();
		Random r = new Random();
		for (int i = 0; i < 4; i++) {
			s.append((char) (65 + r.nextInt(26)));
		}
		return "Guest" + s.toString();
	}
	
	public String generatePassword(int maxlen) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < maxlen; i++) {
			s.append((char) (65 + new Random().nextInt(26)));
		}
		return s.toString().toLowerCase();
	}

	/**
	 * Returns the list of usernames that start with the <tt>partial</tt> parameter.
	 * If the username <tt>partial</tt> is logged on, returns that user.
	 * Otherwise, returns all users starting with <tt>partial</tt>.
	 * If more than one value is in the array, the user needs to be told that that handle is ambiguous.
	 * In this algorithm, online users should be given priority.
	 * @param partial
	 * @return
	 */
	public String[] completeHandle(String partial) {
		if (isLoggedIn(partial)) return new String[] { partial };
		
		String[] allkeys = userNameToSessionMap.keySet().toArray(new String[0]);
		UserSession[] values = userNameToSessionMap.values().toArray(new UserSession[userNameToSessionMap.values().size()]);

		List<String> matches = new ArrayList<String>();
		for (int i=0;i<allkeys.length;i++) {
			String s = allkeys[i];
			if (s.toLowerCase().startsWith(partial.toLowerCase()))
				matches.add(values[i].getUser().getUserName());
		}

		return matches.toArray(new String[matches.size()]);
	}

	public void batchSend(String[] usernames, String message) {
		for (String username : usernames) {
			UserSession s = getUserSession(username);
			if (s.isConnected()) {
				s.send(message);
			}
		}
	}
	
	/** Added by johnthegreat on 6/5/11 */
	public void batchSend(UserSession[] sessions, String message) {
		for (UserSession s : sessions) {
			if (s.isConnected()) {
				s.send(message);
			}
		}
	}


	/** O(N) performance */
	public UserSession[] fetchAllUsersWithVariable(String variable, String value) {
		List<UserSession> list = new ArrayList<UserSession>();

		UserSession[] arr = getLoggedInUsers();
		for (UserSession u : arr) {
			if (u.getUser().getUserVars().getVariables().get(variable).equals(
					value))
				list.add(u);
		}

		return list.toArray(new UserSession[list.size()]);
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
//		final UserSession[] arr = getLoggedInUsers();
//		for(UserSession s : arr) {
//			s.disconnect();
//		}
		
		userNameToSessionMap.clear();
	}

	/**
	 * Checks if a username is valid. That would be either registered or
	 * currently online as a guest.
	 */
	public boolean isValidUsername(String username) {
		return isRegistered(username) || isLoggedIn(username);
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

	public void sendAnnouncement(String username,String message) {
		String announcement = "  **ANNOUNCEMENT**" + (username.equals("")?"":" from " + username + ":")+ " " + message + "\n";
		for (UserSession session : getLoggedInUsers()) {
			session.send(announcement);
		}
	}

	public boolean isRegistered(String username) {
		if (isLoggedIn(username)) {
			return getUserSession(username).getUser().isRegistered();
		} else {
			try {
				DBConnection conn = DBConnectionService.getInstance().getDBConnection();
				java.sql.ResultSet results = conn
						.executeQueryWithRS("SELECT `id` FROM `users` WHERE `username` LIKE '"
								+ username + "'");
					if (results.next()) {
						return true;
					}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		return false;
	}
	
	public int getDBID(String username) {
		try {
			DBConnection conn = DBConnectionService.getInstance().getDBConnection();
			java.sql.ResultSet results = conn
					.executeQueryWithRS("SELECT `id` FROM `users` WHERE `username` LIKE '"
							+ username + "'");
				if (results.next()) {
					return results.getInt(1);
				}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return 0;
	}

	public String getTags(String username) {
		morphy.user.User u = getUserSession(username).getUser();
		if (!u.isRegistered())
			return username + "(U)";

		StringBuilder tags = new StringBuilder();
		ServerListManagerService service = ServerListManagerService
				.getInstance();
		List<ServerList> list = service.getLists();
		for (ServerList sl : list) {
			if (service.isOnList(sl, username)) {
				if (sl.equals(service.getList("admin"))) {
					if (u.getUserVars().getVariables().get("showadmintag").equals("1")) {
						tags.append(sl.getTag());
					}
				} else if (sl.equals(service.getList("sr"))) {
					if (u.getUserVars().getVariables().get("showsrtag").equals("1")) {
						tags.append(sl.getTag());
					}
				} else {
					tags.append(sl.getTag());
				}
			}
		}

		return username + tags.toString();
	}
}
