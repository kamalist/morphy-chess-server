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

import java.text.SimpleDateFormat;

import morphy.game.Game;
import morphy.service.GameService;
import morphy.service.UserService;
import morphy.user.UserSession;
import morphy.utils.john.TimeZoneUtils;

public class MovesCommand extends AbstractCommand {

	public MovesCommand() {
		super("moves");
	}
	
	/*
	 * Usage: moves [<empty>|n|username]
     */
	public void process(String arguments, UserSession userSession) {
		arguments = arguments.trim();
		
		GameService gs = GameService.getInstance();
		Game g = null;
		
		UserSession s = null;
		if (arguments.equals("")) {
			s = userSession;
		} else if (arguments.matches("[0-9]+")) {
			int id = Integer.parseInt(arguments);
			g = gs.findGameById(id);
		} else {
			s = UserService.getInstance().getUserSession(arguments);
		}

		if (s != null && g == null) g = gs.map.get(s);
		if (g == null) { System.err.println("args = \"" + arguments + "\""); }
		
		StringBuilder b = new StringBuilder();
		b.append("\nMovelist for game " + g.getGameNumber() + ":\n\n");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, HH:mm z yyyy");
		sdf.setTimeZone(TimeZoneUtils.getTimeZone(userSession.getUser()
				.getUserVars().getVariables().get("tzone").toUpperCase()));
		if (sdf.getTimeZone() == null) sdf.setTimeZone(java.util.TimeZone.getDefault());
		
		b.append(g.getWhite().getUser().getUserName() + " (2151) vs. " + g.getBlack().getUser().getUserName() + " (UNR) --- " + sdf.format(g.getTimeGameStarted()) + "\n");
		b.append((g.isRated()?"Rated":"Unrated") + " " + g.getVariant().name() + " match, initial time: " + g.getTime() + " minutes, increment: " + g.getIncrement() + " seconds.\n\n");

		b.append("Move  " + g.getWhite().getUser().getUserName() + "            " + g.getBlack().getUser().getUserName() + "            \n");
		b.append("----  ---------------------   ---------------------\n"); //21
		//b.append("  1.  e4      (0:00.000)      e5      (0:00.000)   \n");
		b.append("      {Still in progress} *");
		
		userSession.send(b.toString());

	}
}
