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

import org.apache.commons.lang.StringUtils;

public class WhoCommand extends AbstractCommand {
	public WhoCommand() {
		super("who");
	}

	public void process(String arguments, UserSession userSession) {
		UserService us = UserService.getInstance();
		UserSession[] users = us.getLoggedInUsers();

		StringBuilder output = new StringBuilder(150);
		output.append("List of users logged in:\n");
		int counter = 0;
		for (int i = 0; i < users.length; i++) {
			output.append(StringUtils.rightPad(
					us.getTags(users[i].getUser().getUserName()), 20));
			if (counter >= 4) {
				output.append("\n");
				counter = 0;
			}
		}
		output.append("\n" + users.length + " users displayed (of " + users.length + "). (*) indicates system administrator.");
		userSession.send(output.toString());
	}
}