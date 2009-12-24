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

import morphy.service.UserService;
import morphy.user.UserSession;

public class ShoutCommand extends AbstractCommand {
	public ShoutCommand() {
		super("shout");
	}

	public void process(String arguments, UserSession userSession) {
		UserSession[] sessions = UserService.getInstance().getLoggedInUsers();
		String shoutedMessage = "(shouted to " + sessions.length + " players)";
		boolean sendShoutedMessage = false;
		for (UserSession session : sessions) {
			if (session.getUser().getUserVars().isShoutOn()) {
				if (session == userSession) {
					session.send(userSession.getUser().getUserName()
							+ " shouts: " + arguments + "\n" + shoutedMessage);
					sendShoutedMessage = true;
				} else {
					session.send(userSession.getUser().getUserName()
							+ " shouts: " + arguments);
				}
			}
		}

		if (!sendShoutedMessage) {
			userSession.send(shoutedMessage);
		}
	}
}