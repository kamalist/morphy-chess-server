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

import java.util.ArrayList;
import java.util.List;

import morphy.service.UserService;
import morphy.user.UserInfoList;
import morphy.user.UserLevel;
import morphy.user.UserSession;
import morphy.utils.MorphyStringUtils;

public class FingerCommand extends AbstractCommand {

	public FingerCommand() {
		super("Finger");
	}
	
	public void process(String arguments, UserSession userSession) {
		int pos = arguments.indexOf(" ");
		if (arguments.equals("")) {
			process(userSession.getUser().getUserName(),userSession);
			return;
		}
		
		
		String user = arguments.substring(0,((pos == -1) ? arguments.length() : pos));
		if (pos != -1) {
		String flags = arguments.substring(pos);
			//finger [user] [/[b][s][l][w][B][S]] [r][n]
			if (flags.contains("r")) { } // don't show notes
			if (flags.contains("n")) { } // don't show ratings
		}
		
		StringBuilder str = new StringBuilder(200);
		UserService userService = UserService.getInstance();
		UserSession query = userService.getUserSession(user);
		
		str.append("Finger of " + userService.getTags(query.getUser().getUserName()) + ":\n\n");
		
		long loggedInMillis = System.currentTimeMillis() - query.getLoginTime();
		long idleTimeMillis = query.getIdleTimeMillis();
		str.append("On for: "
				+ MorphyStringUtils.formatTime(loggedInMillis, !false)
				+ "\tIdle: "
				+ ((idleTimeMillis == 0) ? (idleTimeMillis + " secs") : MorphyStringUtils.formatTime(idleTimeMillis, true)));
		str.append("\n");
		str.append(String.format("%15s %7s %7s %7s %7s %7s %7s","rating","RD","win","loss","draw","total","best") + "\n");
		
		// variants, ratings
		
		// total time online, etc
		
		str.append("\n\n");
		UserLevel lvl = query.getUser().getUserLevel();
		if (lvl == UserLevel.Admin || lvl == UserLevel.SuperAdmin || lvl == UserLevel.HeadAdmin) {
			str.append("Administrator Level: ");
			if (lvl == UserLevel.Admin) str.append("Administrator");
			if (lvl == UserLevel.SuperAdmin) str.append("Senior Administrator");
			if (lvl == UserLevel.HeadAdmin) str.append("Head Administrator");
			str.append("\n");
		}
		str.append("Timeseal 1: On\n\n");
		List<String> notes = query.getUser().getUserInfoLists().get(UserInfoList.notes);
		if (notes == null) {
			notes = new ArrayList<String>(UserInfoList.MAX_NOTES);
			userSession.getUser().getUserInfoLists().put(UserInfoList.notes,notes);
		}
		for(int i=0;i< (notes.size()) ;i++) {
			String note = notes.get(i);
			if (!note.equals(""))
				str.append(format(i+1) + ": " + note + "\n");
		}
		
		userSession.send(str.toString());
	}

	private String format(int x) {
		if (x < 10) 
		 return " " + x; 
		else 
		 return "" + x;
	}
}
