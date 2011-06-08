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

import org.apache.commons.lang.StringUtils;

import morphy.service.UserService;
import morphy.user.SocketChannelUserSession;
import morphy.user.UserSession;

public class BugWhoCommand extends AbstractCommand {

	public BugWhoCommand() {
		super("bugwho");
	}
	
	public void process(String arguments, UserSession userSession) {
//		int pos = arguments.indexOf(" ");
//		if (arguments.equals("")) {
//			process(userSession.getUser().getUserName(),userSession);
//			return;
//		}

//		30 2677 GMFressinet 2705 GMBacrot   [ su120   0] 1:31:00 -1:32:00 (39-39) B:  7
		
		
//		GameService gs = GameService.getInstance();
//		List<Game> list = gs.getGames();
//		if (list.size() == 0) {
//			userSession.send("There are no games in progress.");
//			return;
//		}
		
		StringBuilder b = new StringBuilder();
		
		b.append("Bughouse games in progress\n");
		b.append("160 1770 knighttour  1680 BishopBlud [pBu  2   0]   0:58 -  0:14 (35-23) W: 27\n");
		b.append("179 1486 EagleMorphy ++++ DogWithSky [pBu  2   0]   1:09 -  0:22 (43-55) B: 20\n");

		b.append(String.format("%2d",1) + " game displayed.\n\n");

		b.append("Partnerships not playing bughouse\n");
		UserService us = UserService.getInstance();
		UserSession u = us.getUserSession("johnthegreat");
		b.append(String.format("%4s","9999") + " " + getChar(u) + StringUtils.rightPad(us.getTags(u.getUser().getUserName()), 20));
		b.append(" / 2789:ChIcKeNcRoSsRoAd(FM)(CA)");

		b.append("\n3 partnerships displayed.\n\n");
		
		b.append("Unpartnered players with bugopen on\n\n");
		b.append("2789:ChIcKeNcRoSsRoAd(FM)(CA)  1369^bachio");
		
		userSession.send(b.toString());
	}
	
	private String getChar(UserSession u) {
		if (u == null) return null;
		String pChar = " ";
		SocketChannelUserSession s = (SocketChannelUserSession)u;
		if (s.isPlaying()) { pChar = "^"; } else
		if (s.isExamining()) { pChar = "#"; } else
		if (s.getIdleTimeMillis() > 300000 || 
			!s.getUser().getUserVars().getVariables().get("busy").equals("")) { pChar = "."; } else
		if (s.getUser().getUserVars().getVariables().get("tourney").equals("1")) { pChar = "&"; }
		return pChar;
	}
}
