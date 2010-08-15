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
package morphy.user;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import morphy.channel.Channel;
import morphy.service.ScreenService;
import morphy.service.SocketConnectionService;
import morphy.service.UserService;
import morphy.service.ScreenService.Screen;
import morphy.utils.BufferUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketChannelUserSession implements UserSession,
		Comparable<UserSession> {
	protected static Log LOG = LogFactory
			.getLog(SocketChannelUserSession.class);

	protected User user;
	protected SocketChannel channel;
	protected StringBuilder inputBuffer = new StringBuilder(400);
	protected long lastReceivedTime;
	protected boolean hasLoggedIn = false;
	protected long loginTime = System.currentTimeMillis();
	protected Map<UserSessionKey, Object> objectMap = new TreeMap<UserSessionKey, Object>();
	protected Timer idleLogoutTimer = new Timer();
	protected Channel lastChannelToldTo = null;
	protected UserSession lastPersonToldTo = null;
	
	public SocketChannelUserSession(User user, SocketChannel channel) {
		this.user = user;
		this.channel = channel;

		if (LOG.isInfoEnabled()) {
			LOG.info("Created SocketChannelUserSession user "
					+ user.getUserName() + " "
					+ channel.socket().getInetAddress());
		}
	}
	
	public void scheduleIdleTimeout() {
		// admins don't idle out
		if (UserService.getInstance().isAdmin(getUser().getUserName()))
			return;
		
		final int millis = 60*60*1000;
		
		if (idleLogoutTimer != null)
			idleLogoutTimer.cancel();
		
		idleLogoutTimer = new Timer();
		idleLogoutTimer.schedule(new java.util.TimerTask() {
			public void run() {
					if (getIdleTimeMillis() >= millis-1) {
						send("**** Auto-logout because you were idle for 60 minutes ****");
						disconnect();
					} else {
						idleLogoutTimer.purge();
						scheduleIdleTimeout();
					}
			} }, 60*60*1000);
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				String v = getNotifyNames();
				if (!v.equals("")) send("Your departure was noted by the following: " + v);
				send(ScreenService.getInstance().getScreen(Screen.Logout));
				getUser().getUserVars().dumpToDB();
				channel.close();
			} catch (Throwable t) {
				if (LOG.isErrorEnabled())
					LOG.error("Error disconnecting socket channel", t);
			}
			
			if (user.getUserName() != null) {
				UserService.getInstance().removeLoggedInUser(this);
				SocketConnectionService.getInstance().removeUserSession(this);

				if (LOG.isInfoEnabled()) {
					LOG.info("Disconnected user " + user.getUserName());
				}	
				
				UserSession[] sessions = UserService.getInstance().fetchAllUsersWithVariable("pin","1");
				for(UserSession s : sessions) {
					s.send(String.format("[%s has disconnected.]",getUser().getUserName()));
				}
			}
		}
	}

	public Object get(UserSessionKey key) {
		return objectMap.get(key);
	}

	public Boolean getBoolean(UserSessionKey key) {
		return (Boolean) get(key);
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public long getIdleTimeMillis() {
		return lastReceivedTime == 0 ? 0 : System.currentTimeMillis()
				- lastReceivedTime;
	}

	public StringBuilder getInputBuffer() {
		return inputBuffer;
	}

	public Integer getInt(UserSessionKey key) {
		return (Integer) get(key);
	}

	public long getLoginTime() {
		return loginTime;
	}

	public String getString(UserSessionKey key) {
		return (String) get(key);
	}

	public User getUser() {
		return user;
	}

	public boolean hasLoggedIn() {
		return hasLoggedIn;
	}

	public boolean isConnected() {
		return channel.isOpen();
	}

	public void put(UserSessionKey key, Object object) {
		objectMap.put(key, object);
	}

	public void send(String message) {
		try {
			if (isConnected()) {
				ByteBuffer buffer = BufferUtils
						.createBuffer(SocketConnectionService.getInstance()
								.formatMessage(this, message + "\nfics% "));
				channel.write(buffer);
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Tried to send message to a logged off user "
							+ user.getUserName() + " " + message);
				}
				disconnect();
			}
		} catch (Throwable t) {
			if (LOG.isErrorEnabled())
				LOG.error("Error sending message to user " + user.getUserName()
						+ " " + message, t);
			disconnect();
		}
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public void setHasLoggedIn(boolean hasLoggedIn) {
		this.hasLoggedIn = hasLoggedIn;
	}

	public void setInputBuffer(StringBuilder inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void touchLastReceivedTime() {
		lastReceivedTime = System.currentTimeMillis();
		scheduleIdleTimeout();
	}

	public int compareTo(UserSession o) {
		return getUser().getUserName().compareToIgnoreCase(
				o.getUser().getUserName());
	}
	
	private String getNotifyNames() {
		StringBuilder b = new StringBuilder();
		final UserSession[] arr = UserService.getInstance().getLoggedInUsers();
		java.util.Arrays.sort(arr);
		for(int i=0;i<arr.length;i++) {
			UserSession s = arr[i];
			List<String> l = s.getUser().getLists().get(PersonalList.notify);
			if (l.contains(getUser().getUserName())) {
				b.append(s.getUser().getUserName());
				
				if (i != l.size()-1)
					b.append(" ");
			}
		}
		return b.toString();
	}

	public Channel getLastChannelToldTo() {
		return lastChannelToldTo;
	}

	public void setLastChannelToldTo(Channel lastChannelToldTo) {
		this.lastChannelToldTo = lastChannelToldTo;
	}

	public UserSession getLastPersonToldTo() {
		return lastPersonToldTo;
	}

	public void setLastPersonToldTo(UserSession lastPersonToldTo) {
		this.lastPersonToldTo = lastPersonToldTo;
	}
}
