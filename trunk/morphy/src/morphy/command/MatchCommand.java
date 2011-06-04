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

import morphy.game.MatchParams;
import morphy.game.Variant;
import morphy.service.UserService;
import morphy.user.PersonalList;
import morphy.user.SocketChannelUserSession;
import morphy.user.UserSession;
import morphy.utils.MorphyStringUtils;

public class MatchCommand extends AbstractCommand {

	public MatchCommand() {
		super("match");
	}
	
	/*
	 * Usage: match user [rated|unrated] [Time] [Inc] [White|Black]
     *            [{board_categoryboard}|w#|variant]
     */
	public void process(String arguments, UserSession userSession) {
		int pos = arguments.indexOf(" ");
		if (arguments.equals("")) {
			process(userSession.getUser().getUserName(),userSession);
			return;
		}
		
		UserService userService = UserService.getInstance();
		
		String user = arguments.substring(0,((pos == -1) ? arguments.length() : pos));
		
		String[] matches = UserService.getInstance().completeHandle(user);
		if (matches.length > 1) {
			userSession.send("Ambiguous handle \"" + user + "\". Matches: " + MorphyStringUtils.toDelimitedString(matches," "));
			return;
		}
		
		if (matches.length == 1)
			user = matches[0];
		
		if (!UserService.getInstance().isValidUsername(user)) {
			userSession.send("There is no player matching the name " + user + ".");
			return;
		}
		
		if (user.equals(userSession.getUser().getUserName())) {
			userSession.send("You cannot match yourself.");
			return;
		}
		
		if (userSession.getUser().isOnList(PersonalList.censor,user)) {
			userSession.send("You are censoring " + user + ".");
			return;
		}
		
		if (userSession.getUser().isOnList(PersonalList.noplay,user)) {
			userSession.send("You have " + user + " on your noplay list.");
			return;
		}
		
		SocketChannelUserSession sess = (SocketChannelUserSession)userService.getUserSession(user);
		
		if (sess.getUser().isOnList(PersonalList.censor,userSession.getUser().getUserName())) {
			userSession.send(user + " is censoring you.");
			return;
		}
		
		if (sess.getUser().isOnList(PersonalList.noplay,userSession.getUser().getUserName())) {
			userSession.send("You are on " + user + "'s noplay list.");
			return;
		}
		
		if (sess.isPlaying()) {
			userSession.send(user + " is playing a game.");
			return;
		}
		
		if (sess.isExamining()) {
			userSession.send(user + " is examining a game.");
		}
		
		// defaults
		MatchParams p = new MatchParams();
		p.setTime(Integer.parseInt(userSession.getUser().getUserVars().getVariables().get("time")));
		p.setIncrement(Integer.parseInt(userSession.getUser().getUserVars().getVariables().get("inc")));
		p.setRated(userSession.getUser().getUserVars().getVariables().get("rated").equals("1")?true:false);
		p.setVariant(Variant.blitz);
		p.setColorRequested(MatchParams.ColorRequested.Black);
		
		// todo
		if (sess.getUser().getFormula() != null) {
			if (!sess.getUser().getFormula().matches(p)) {
				userSession.send("Match request does not fit formula for " + user + ": ");
				sess.send("Ignoring (formula): ");
				return;
			}
		}
	
		StringBuilder str = new StringBuilder(200);
		str.append("Challenge: " + userSession.getUser().getUserName() + " (----) " + (p.getColorRequested()!=MatchParams.ColorRequested.Neither?"[" + p.getColorRequested().name() + "]":"") + " " + user + " (----) " + (p.isRated()?"rated":"unrated") + " " + p.getVariant() + " " + p.getTime() + " " + p.getIncrement() + ".");
		str.append("\nYou can \"accept\" or \"decline\", or propose different parameters.");
		sess.send(str.toString());
		
		str = new StringBuilder(200);
		str.append("Issuing: " + userSession.getUser().getUserName() + " (----) " + user + " (----) " + (p.getColorRequested()!=MatchParams.ColorRequested.Neither?"[" + p.getColorRequested().name() + "]":"") + " " + (p.isRated()?"rated":"unrated") + " " + p.getVariant() + " " + p.getTime() + " " + p.getIncrement() + ".");
		userSession.send(str.toString());
	}
}
