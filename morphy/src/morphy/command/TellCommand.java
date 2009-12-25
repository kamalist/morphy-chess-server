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
import morphy.user.UserSession;

public class TellCommand extends AbstractCommand {
	public TellCommand() {
		super("tell");
	}

	public void process(String arguments, UserSession userSession) {
		int spaceIndex = arguments.indexOf(' ');
		if (spaceIndex == -1) {
			userSession.send(getContext().getUsage());
		} else {
			String userName = arguments.substring(0, spaceIndex);
			String message = arguments.substring(spaceIndex + 1, arguments
					.length());
			
			if (userName.matches("[0-9]+")) {
				ChannelService channelService = ChannelService.getInstance();
				int number = Integer.parseInt(userName);
				Channel c = channelService.getChannel(number);
				if (c == null || number < Channel.MINIMUM || number > Channel.MAXIMUM)
					{ userSession.send("Bad channel number."); } 
				else { 
					int sentTo = channelService.tell(c, message, userSession);
					userSession.send("(told " + sentTo + " players in channel " + c.getNumber() + " \"" + c.getName() + "\")");
				}
			}
			else {	
				UserSession personToTell = UserService.getInstance()
						.getUserSession(userName);
				if (personToTell == null) {
					userSession.send("User " + userName + " is not logged in.");
				} else {
					personToTell.send(userSession.getUser().getUserName()
							+ " tells you: " + message);
					userSession.send("(told "
							+ personToTell.getUser().getUserName() + ")");
				}
			}
		}
	}
}
