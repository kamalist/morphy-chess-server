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

import org.apache.commons.lang.StringUtils;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.service.ServerListManagerService;
import morphy.service.UserService;
import morphy.user.PersonalList;
import morphy.user.User;
import morphy.user.UserSession;
import morphy.utils.john.ServerList;
import morphy.utils.john.ServerList.ListType;

public class AddListCommand extends AbstractCommand {
	public AddListCommand() {
		super("AddList");
	}

	public void process(String arguments, UserSession userSession) {
		
		String[] args = arguments.split(" ");
		if (args.length != 2) {
			userSession.send(getContext().getUsage());
			return;
		}
		String listName = args[0];
		String value = args[1];
		
		if (UserService.getInstance().isAdmin(userSession.getUser().getUserName())) {
			ServerListManagerService serv = ServerListManagerService.getInstance();
			ServerList s = serv.getList(listName);
			if (s != null) {
				if (userSession.getUser().getUserLevel().ordinal() >= s.getPermissions().ordinal()) {
					if (s.getType().equals(ListType.Integer) && !StringUtils.isNumeric(value)) {
						userSession.send("Bad value provided for that list (Integer required)");
						return;
					} else if (s.getType().equals(ListType.Username) && !UserService.getInstance().isValidUsername(value)) {
						userSession.send("Bad value provided for that list (Username required)");
						return;
					} else if (s.getType().equals(ListType.IPAddress) && !value.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")) { 
						userSession.send("Bad value provided for that list (IPAddress required)");
						return;
					} else if (s.getType().equals(ListType.String) && value.contains(" ")) {
						userSession.send("Bad value provided for that list (String required)");
						return;
					}
					
					if (!serv.isOnList(s,value)) {
						serv.getElements().get(s).add(value);
						userSession.send("[" + value + "] added to the " + listName + " list.");
						return;
					} else {
						userSession.send("[" + value + "] is already on the " + listName + " list.");
						return;
					}
				} else {
					userSession.send("\"" + listName + "\" is not an appropriate list name or you have insufficient rights.");
				}
			}
		}
		
		listName = listName.toLowerCase();
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
		if (!myList.contains(value)) {
			if (list == PersonalList.channel) {
				ChannelService cS = ChannelService.getInstance();
				try {
					int intVal = Integer.parseInt(value);
					if (intVal < Channel.MINIMUM || intVal > Channel.MAXIMUM)
						throw new NumberFormatException();
					Channel c = cS.getChannel(intVal);
					if (c != null)
						c.addListener(userSession);
					else
						userSession.send("That channel should, but does not, exist.");
				} catch (NumberFormatException e) {
					userSession
							.send("The channel to add must be a number between "
									+ Channel.MINIMUM
									+ " and "
									+ Channel.MAXIMUM + ".");
					return;
				}
			}

			myList.add(value);
			userSession.send("[" + value + "] added to your " + listName
					+ " list.");
		} else {
			userSession.send("[" + value + "] is already on your " + listName
					+ " list.");
		}
	}

}
