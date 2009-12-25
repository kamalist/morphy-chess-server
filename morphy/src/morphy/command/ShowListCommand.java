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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import morphy.user.PersonalList;
import morphy.user.User;
import morphy.user.UserSession;

public class ShowListCommand extends AbstractCommand {
	public ShowListCommand() {
		super("ShowList");
	}

	public void process(String arguments, UserSession userSession) {
		if (arguments.indexOf(" ") != -1) arguments = arguments.substring(0,arguments.indexOf(" "));
		String listName = arguments.toLowerCase();
		if (listName.equals("")) {
			PersonalList[] arr = PersonalList.values();
			StringBuilder str = new StringBuilder();
			str.append("Lists:\n\n");
			for(int i=0;i<arr.length;i++) {
				str.append(String.format("%-20s is %s",arr[i].name(),"PERSONAL"));
				if (i != arr.length-1) str.append("\n");
			}
			userSession.send(str.toString());
			return;
		}
		
		PersonalList list = null;
		try {
			list = PersonalList.valueOf(listName);
		} catch(Exception e) { userSession.send("\"" + listName + "\" does not match any list name."); return; }
		List<String> myList = userSession.getUser().getLists().get(list);
		if (myList == null) {
			myList = new ArrayList<String>(User.MAX_LIST_SIZE);
			userSession.getUser().getLists().put(list, myList);
		}
		StringBuilder str = new StringBuilder(50);
		Collections.sort(myList);
		str.append("-- " + listName + " list: " + myList.size() + " names --\n");
		for(int i=0;i<myList.size();i++) {
			str.append(myList.get(i));
			if (i != myList.size()-1) str.append(" ");
		}
		userSession.send(str.toString());
	}

}
