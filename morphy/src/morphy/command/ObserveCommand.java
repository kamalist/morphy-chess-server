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

import morphy.game.Game;
import morphy.service.GameService;
import morphy.service.UserService;
import morphy.user.UserSession;

public class ObserveCommand extends AbstractCommand {

	public ObserveCommand() {
		super("observe");
	}
	
	public void process(String arguments, UserSession userSession) {
		if (arguments.equals("")) {
			process(userSession.getUser().getUserName(),userSession);
			return;
		}
		
		GameService gs = GameService.getInstance();
		
		Game g = null;
		if (arguments.matches("[0-9]+")) {
			g = gs.findGameById(Integer.parseInt(arguments));
		} else if (arguments.matches("\\w{3,17}")) {
			String[] array = UserService.getInstance().completeHandle(arguments);
			if (array.length == 0) {
				userSession.send(arguments + " is not logged in.");
				return;
			}
			if (array.length > 1) {
				StringBuilder b = new StringBuilder();
				b.append("-- Matches: " + array.length + " player(s) --\n");
				for(int i=0;i<array.length;i++) {
					b.append(array[i] + "  ");
				}
				userSession.send(b.toString());
				return;
			}
			g = gs.map.get(array[0]);
		}
		
		g.addObserver(userSession);
		g.processMoveUpdate(userSession);
	}

}
