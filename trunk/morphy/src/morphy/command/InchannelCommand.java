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
package morphy.command;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.service.UserService;
import morphy.user.PersonalList;
import morphy.user.UserSession;

public class InchannelCommand extends AbstractCommand {

	public InchannelCommand() {
		super("Inchannel");
	}

	public void process(String arguments, UserSession userSession) {
		// int spaceIndex = arguments.indexOf(' ');
		// if (spaceIndex == -1) {
		// userSession.send(getContext().getUsage());
		if (arguments.equals("")) {
			userSession
					.send("inchannel [no params] has been removed.\nPlease use inchannel [name] or inchannel [number]");
		} else {
			// String userName = arguments.substring(0, spaceIndex);
			final String userName = arguments;

			if (userName.matches("[0-9]+")) {
				ChannelService channelService = ChannelService.getInstance();
				int number = Integer.parseInt(userName);
				Channel c = channelService.getChannel(number);
				if (c == null || number < Channel.MINIMUM
						|| number > Channel.MAXIMUM) {
					userSession.send("Bad channel number.");
				} else {
					int numUsers = c.getListeners().size();
					StringBuilder str = new StringBuilder(100);
					str.append("Channel " + c.getNumber() + " \"" + c.getName()
							+ "\": ");
					for (int i = 0; i < numUsers; i++) {
						str.append(c.getListeners().get(i).getUser()
								.getUserName());
						if (i != numUsers + 1)
							str.append(" ");
					}
					str.append("\n" + numUsers + " players are in channel "
							+ c.getNumber() + ".");
					userSession.send(str.toString());
				}
			} else {
				StringBuilder str = new StringBuilder("50");
				str.append(userName + " is in the following channels:\n");
				UserService uS = UserService.getInstance();
				UserSession sess = uS.getUserSession(userName);
				if (sess == null) {
					userSession.send(userName + " is not logged in.");
				}
				final int numUsers = sess.getUser().getLists().get(
						PersonalList.channel).size();
				for (int i = 0; i < numUsers; i++) {
					str.append(sess.getUser().getLists().get(
							PersonalList.channel).get(i));
					if (i != numUsers + 1)
						str.append(" ");
				}
				userSession.send(str.toString());
			}
		}
	}
}
