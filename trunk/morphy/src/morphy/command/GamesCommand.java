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

import java.util.List;

import morphy.game.ExaminedGame;
import morphy.game.Game;
import morphy.game.GameInterface;
import morphy.service.GameService;
import morphy.user.UserSession;

public class GamesCommand extends AbstractCommand {

	public GamesCommand() {
		super("games");
	}
	
	public void process(String arguments, UserSession userSession) {
//		int pos = arguments.indexOf(" ");
//		if (arguments.equals("")) {
//			process(userSession.getUser().getUserName(),userSession);
//			return;
//		}

//		30 2677 GMFressinet 2705 GMBacrot   [ su120   0] 1:31:00 -1:32:00 (39-39) B:  7
		
		
		GameService gs = GameService.getInstance();
		List<GameInterface> list = gs.getGames();
		if (list.size() == 0) {
			userSession.send("There are no games in progress.");
			return;
		}
		
		StringBuilder b = new StringBuilder();
		for(int i=0;i<list.size();i++) {
			GameInterface g = list.get(i);
			if (g instanceof Game) {
				b.append(String.format("%3d ---- %-17s ---- %-17s [ %3d %3d] x:xx:xx x:xx:xx (%2d-%2d)\n",g.getGameNumber(),((Game)g).getWhite().getUser().getUserName(),((Game)g).getBlack().getUser().getUserName(),g.getTime(),g.getIncrement(),g.getWhiteBoardStrength(),g.getBlackBoardStrength()));
			}
			if (g instanceof ExaminedGame) {
				ExaminedGame gg = (ExaminedGame)g;
				b.append(String.format("%3d (Exam. %4d %-11s %4d %-11s) [ uu%3d %3d] ",gg.getGameNumber(),0,gg.getWhiteName(),0,gg.getBlackName(),gg.getTime(),gg.getIncrement()));
			}
		}
		
		userSession.send(b.toString());
	}

}
