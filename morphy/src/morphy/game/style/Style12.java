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
package morphy.game.style;

import board.PositionState;
import morphy.game.Game;
import morphy.user.UserSession;

/** Class implementing the style12 string. */
public class Style12 implements StyleInterface {

	public String print(UserSession userSession, Game g) {
		PositionState p = g.getBoard().getLatestMove();
		
		String notation = "none", verboseNotation = "none";
		if (p.getPrettyNotation() != null) notation = p.getPrettyNotation();
		if (p.getVerboseNotation() != null) {
			verboseNotation = p.getVerboseNotation();
			verboseNotation = verboseNotation.substring(0,1) + verboseNotation.substring(1).toLowerCase();
		}
		
		//System.err.println(p.getNotation() + " " + p.getVerboseNotation() + " " + p.getFEN());
		int myrelation = 0;
		boolean amIPlaying = userSession.getUser().getUserName().equals(g.getWhite().getUser().getUserName()) || userSession.getUser().getUserName().equals(g.getBlack().getUser().getUserName());
		if (!amIPlaying) myrelation = 0;
		boolean amIWhite = userSession.getUser().getUserName().equals(g.getWhite().getUser().getUserName());
		boolean whitesMove = !p.isWhitesMove();
		if ((amIWhite && whitesMove) || (!amIWhite && !whitesMove)) myrelation = 1;
		if (amIWhite && !whitesMove || !amIWhite && whitesMove) myrelation = -1;
		
		String style12string = "" + p.draw() + "" + (p.isWhitesMove()?"B":"W") + " -1 " + (p.canWhiteCastleKingside()?"1":"0") + " " + (p.canWhiteCastleQueenside()?"1":"0") + " " + (p.canBlackCastleKingside()?"1":"0") + " " + (p.canBlackCastleQueenside()?"1":"0") + " 0 " + g.getGameNumber() + " " + g.getWhite().getUser().getUserName() + " " + g.getBlack().getUser().getUserName() + " " + myrelation + " " + (g.getTime()) + " " + g.getIncrement() + " " + g.getWhiteBoardStrength() + " " + g.getBlackBoardStrength() + " " + g.getWhiteClock() + " " + g.getBlackClock() + " " + (g.getBoard().getPositions().size()/2) + " " + verboseNotation + " (0:00" + (userSession.getUser().getUserVars().getIVariables().get("ms").equals("1")?".000":"") + ") " + notation + " 0 0 0";
		System.err.println(userSession.getUser().getUserName() + " > " + style12string);
		return style12string;
	}
}
