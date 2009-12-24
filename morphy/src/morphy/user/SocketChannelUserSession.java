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
package morphy.user;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.TreeMap;

import morphy.Morphy;
import morphy.service.ScreenService;
import morphy.service.SocketConnectionService;
import morphy.service.UserService;
import morphy.service.ScreenService.Screen;
import morphy.utils.BufferUtils;
import morphy.utils.MorphyStringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketChannelUserSession implements UserSession {
	protected static Log LOG = LogFactory
			.getLog(SocketChannelUserSession.class);

	protected User user;
	protected SocketChannel channel;
	protected StringBuilder inputBuffer = new StringBuilder(400);
	protected long lastReceivedTime;
	protected boolean hasLoggedIn = false;
	protected long loginTime = System.currentTimeMillis();
	protected Map<UserSessionKey, Object> objectMap = new TreeMap<UserSessionKey, Object>();

	public SocketChannelUserSession(User user, SocketChannel channel) {
		this.user = user;
		this.channel = channel;

		if (LOG.isInfoEnabled()) {
			LOG.info("Created SocketChannelUserSession user "
					+ user.getUserName() + " "
					+ channel.socket().getInetAddress());
		}
	}

	public StringBuilder getInputBuffer() {
		return inputBuffer;
	}

	public void setInputBuffer(StringBuilder inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	public boolean hasLoggedIn() {
		return hasLoggedIn;
	}

	public void setHasLoggedIn(boolean hasLoggedIn) {
		this.hasLoggedIn = hasLoggedIn;
	}

	public Object get(UserSessionKey key) {
		return objectMap.get(key);
	}

	public void put(UserSessionKey key, Object object) {
		objectMap.put(key, object);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public boolean isConnected() {
		return channel.isOpen();
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				send(ScreenService.getInstance().getScreen(Screen.Logout));
				channel.close();
			} catch (Throwable t) {
				Morphy.getInstance().onError(
						"Error disconnecting socket channel", t);
			}
		}
		UserService.getInstance().removeLoggedInUser(this);
		SocketConnectionService.getInstance().removeUserSession(this);

		if (LOG.isInfoEnabled()) {
			LOG.info("Disconnected user " + user.getUserName());
		}
	}

	public void touchLastReceivedTime() {
		lastReceivedTime = System.currentTimeMillis();
	}

	public long getIdleTimeMillis() {
		return lastReceivedTime == 0 ? 0 : lastReceivedTime
				- System.currentTimeMillis();
	}

	public void send(String message) {
		try {
			if (isConnected()) {
				ByteBuffer buffer = BufferUtils.createBuffer(MorphyStringUtils
						.replaceNewlines(message + "\nfics% "));
				channel.write(buffer);
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Tried to send message to a logged off user "
							+ user.getUserName() + " " + message);
				}
				disconnect();
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error sending message to user " + user.getUserName() + " "
							+ message, t);
			disconnect();
		}
	}

	public Boolean getBoolean(UserSessionKey key) {
		return (Boolean) get(key);
	}

	public Integer getInt(UserSessionKey key) {
		return (Integer) get(key);
	}

	public String getString(UserSessionKey key) {
		return (String) get(key);
	}
}
