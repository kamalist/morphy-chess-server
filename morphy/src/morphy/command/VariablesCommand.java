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
import morphy.user.UserSession;

public class VariablesCommand extends AbstractCommand {
	public VariablesCommand() {
		super("Variables");
	}

	public void process(String arguments, UserSession userSession) {
		String[] args = arguments.split(" ");
		if (args.length != 1) {
			userSession.send(getContext().getUsage());
			return;
		}
		
		// i was wondering if we should have some additional functionality
		// by perhaps having a variable name as a second argument, and then
		// only return that variable's value. i will leave this out of
		// implementation for now, since FICS does not support this.
		
		String userName = args[0];
		UserSession personQueried = UserService.getInstance().getUserSession(userName);
		//personQueried.getUser().getUserVars().
		
		StringBuilder builder = new StringBuilder(700);
		
		builder.append("Variable settings of " + personQueried + ":\n\n");
		builder.append(String.format("time=%d       private=%d    shout=%d         pin=%d           style=%d\n"			,2,0,0,0,12));
		builder.append(String.format("inc=%d        jprivate=%d   cshout=%d        notifiedby=%d    flip=%d\n"			,12,0,0,0));
		builder.append(String.format("rated=%d                    kibitz=%d        availinfo=%d     highlight=%d\n"		,0,1,0,0,0));
		builder.append(String.format("open=%d       automail=%d   kiblevel=%d      availmin=%d      bell=%d\n"			,1,0,0,0));
		builder.append(String.format("     		    pgn=%d        tell=%d          availmax=%d      width=%d\n"			,0,1,0,79));
		builder.append(String.format("bugopen=%d                  ctell=%d         gin=%d           height=%d\n"		,0,1,0,24)); 
		builder.append(String.format("		        mailmess=%d                    seek=%d          ptime=%d\n"			,0,0,0));
		builder.append(String.format("tourney=%d    messreply=%d  chanoff=%d       showownseek=%d   tzone=%s\n"			,0,0,0,0,"SERVER"));
		builder.append(String.format("provshow=%d                 silence=%d                        Lang=%s\n"			,0,0,"English"));
		builder.append(String.format("autoflag=%d   unobserve=%d  echo=%d          examine=%d\n"						,0,1,1,0));
		builder.append(String.format("minmovetime=%d              tolerance=%d      noescape=%d     notakeback=%d\n\n"	,1,1,0,0));
		builder.append(String.format("Prompt: %s","fics%"));
		builder.append(String.format("Interface: \"%s\"","Thief 1.1"));
		
		userSession.send(builder.toString());
		
		System.out.println(builder.toString().length());
	}
}
