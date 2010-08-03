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
package morphy.command;

import morphy.service.UserService;
import morphy.user.PersonalList;
import morphy.user.UserSession;

public class SummonCommand extends AbstractCommand {

	public SummonCommand() {
		super("summon");
	}

	public void process(String arguments, UserSession userSession) {
		if (arguments.equals("")) {
			userSession.send(getContext().getUsage());
			return;
		}

		String name = arguments;
		if (!UserService.getInstance().isValidUsername(name)) {
			userSession.send("There is no player matching the name " + name + ".");
			return;
		}

		String myname = userSession.getUser().getUserName();
		
		if (UserService.getInstance().getUserSession(name).getUser().isOnList(
				PersonalList.notify,myname)) {
			userSession.getUser().getLists().get(PersonalList.idlenotify).add(name);
			userSession.send("Summoning sent to \"" + name + "\".\n\n[" + name
					+ "] added to your idlenotify list.");
		} else {
			userSession
				.send("You cannot summon a player who doesn't have you on his/her notify list.");
		}
		
		UserService.getInstance().getUserSession(name).send(myname + " needs to speak to you.  To contact him/her type \"tell " + myname + " hello\".");
	}

}
