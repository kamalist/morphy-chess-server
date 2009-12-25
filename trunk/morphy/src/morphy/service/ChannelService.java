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

import java.util.ArrayList;
import java.util.List;

import morphy.channel.Channel;
import morphy.user.PlayerTitle;
import morphy.user.UserLevel;
import morphy.user.UserSession;

public class ChannelService implements Service {
	public static final int MAX_NUM_CHANNELS = 30;
	
	private static final ChannelService singletonInstance = new ChannelService();
	
	private List<Channel> channels = new ArrayList<Channel>(1);

	private ChannelService() {
		addChannel(new Channel(1,"Help","The help channel.",UserLevel.Player,PlayerTitle.values()));
		//getChannel(1).addListener();
	}

	public static ChannelService getInstance() {
		return singletonInstance;
	}

	public void dispose() {

	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	public List<Channel> getChannels() {
		return channels;
	}
	
	public void addChannel(Channel c) {
		getChannels().add(c);
	}
	
	public Channel getChannel(int number) {
		for(Channel c : getChannels()) {
			if (c.getNumber() == number) return c;
		}
		return null;
	}
	
	public int tell(Channel channel, String message, UserSession sender) {
		int sentTo = 0;
		for(UserSession person : channel.getListeners()) {
			person.send(sender.getUser().getUserName() + PlayerTitle.toString(sender.getUser().getTitles()) + "(" + channel.getNumber() + "): " + message);
			sentTo++;
		}
		return sentTo;
	}
}
