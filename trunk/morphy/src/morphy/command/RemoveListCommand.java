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

import java.util.ArrayList;
import java.util.List;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.user.PersonalList;
import morphy.user.User;
import morphy.user.UserSession;

public class RemoveListCommand extends AbstractCommand {
	public RemoveListCommand() {
		super("RemoveList");
	}

	public void process(String arguments, UserSession userSession) {
		String[] args = arguments.split(" ");
		if (args.length != 2) {
			userSession.send(getContext().getUsage());
			return;
		}
		String listName = args[0].toLowerCase();
		String value = args[1];
		PersonalList list = null;
		try {
			list = PersonalList.valueOf(listName);
		} catch (Exception e) {
			userSession.send("\"" + listName
					+ "\" does not match any list name.");
			return;
		}
		List<String> myList = userSession.getUser().getLists().get(list);
		if (myList == null) {
			myList = new ArrayList<String>(User.MAX_LIST_SIZE);
			userSession.getUser().getLists().put(list, myList);
		}

		if (value.equals("*")) {
			myList.clear();
			userSession.send("All players have been removed from your "
					+ listName + " list.");
			return;
		}

		if (myList.contains(value)) {
			if (list == PersonalList.channel) {
				ChannelService cS = ChannelService.getInstance();
				try {
					int intVal = Integer.parseInt(value);
					if (intVal < Channel.MINIMUM || intVal > Channel.MAXIMUM)
						throw new NumberFormatException();
					Channel c = cS.getChannel(intVal);
					c.removeListener(userSession);
				} catch (NumberFormatException e) {
					userSession
							.send("The channel to remove must be a number between "
									+ Channel.MINIMUM
									+ " and "
									+ Channel.MAXIMUM + ".");
					return;
				}
			}

			myList.remove(value);
			userSession.send("[" + value + "] removed from your " + listName
					+ " list.");
		} else {
			userSession.send("[" + value + "] is not in your " + listName
					+ " list.");
		}
	}
}
