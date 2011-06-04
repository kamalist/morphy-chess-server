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

import morphy.user.UserSession;

public class PendingCommand extends AbstractCommand {

	public PendingCommand() {
		super("pending");
	}
	
	public void process(String arguments, UserSession userSession) {
		if (!arguments.equals("")) {
			process(userSession.getUser().getUserName(),userSession);
			return;
		}
		
		
		StringBuilder b = new StringBuilder();
		//b.append("There are no offers pending to other players.\n\nThere are no offers pending from other players.\n");
		
		b.append("Offers to other players:\n\n");
		b.append(" " + String.format("%2d",36) + ": You are offering johnthegreatguest a challenge: GuestVNNP (----) johnthegreatguest (----) unrated crazyhouse 2 0.\n\n");
		b.append("If you wish to withdraw any of these offers type \"withdraw number\".\n\n");
		b.append("Offers from other players:\n\n");
		b.append(" " + String.format("%2d",8) + ": johnthegreat is offering a challenge: johnthegreat (2151) GuestVNNP (----) unrated crazyhouse 2 0.\n\n");
		b.append("If you wish to accept any of these offers type \"accept number\".\nIf you wish to decline any of these offers type \"decline number\".");
		userSession.send(b.toString());
	}
}
