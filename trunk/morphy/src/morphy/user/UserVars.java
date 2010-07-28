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
package morphy.user;

import java.util.HashMap;

import morphy.utils.john.DBConnection;

public class UserVars {
	private User user;
	private HashMap<String,String> variables = new HashMap<String,String>();
	
	public UserVars(User user) {
		this.user = user;
		initialize();
	}
	
	private void initialize() {
		// set defaults
		variables.put("time","2");
		variables.put("increment","12");
		variables.put("rated","1");
		variables.put("open","1");
		variables.put("bugopen","0");
		variables.put("tourney","0");
		variables.put("provshow","0");
		variables.put("autoflag","0");
		variables.put("minmovetime","1");
		variables.put("private","0");
		variables.put("jprivate","0");
		variables.put("automail","0");
		variables.put("pgn","0");
		variables.put("mailmess","0");
		variables.put("messreply","0");
		variables.put("unobserve","1");
		variables.put("shout","0");
		variables.put("cshout","0");
		variables.put("kibitz","1");
		variables.put("kiblevel","0");
		variables.put("tell","1");
		variables.put("ctell","1");
		variables.put("chanoff","0");
		variables.put("silence","0");
		variables.put("echo","0");
		variables.put("tolerance","1");
		variables.put("pin","0");
		variables.put("notifiedby","0");
		variables.put("availinfo","0");
		variables.put("availmin","0");
		variables.put("availmax","0");
		variables.put("gin","0");
		variables.put("seek","0");
		variables.put("showownseek","0");
		variables.put("examine","0");
		variables.put("noescape","0");
		variables.put("style","12");
		variables.put("flip","0");
		variables.put("highlight","0");
		variables.put("bell","0");
		variables.put("width","79");
		variables.put("height","24");
		variables.put("ptime","0");
		variables.put("tzone","SERVER");
		variables.put("Lang","English");
		variables.put("notakeback","0");
		variables.put("Prompt","fics%");
		variables.put("Interface","NULL");
	}
	
	public User getUser() {
		return user;
	}
	
	public HashMap<String,String> getVariables() {
		return variables;
	}
	
	public void dumpToDB() {
		HashMap<String,String> variables = getVariables();
		String[] keys = variables.keySet().toArray(new String[0]);
		String[] values = variables.values().toArray(new String[0]);
		
		String username = getUser().getUserName();
		Object query = null;
		DBConnection conn = new DBConnection();
		conn.executeQuery("SELECT `id` FROM `user_vars` WHERE `user_id` = (SELECT `id` FROM `users` WHERE `username` = '" + username + "')");
		
		StringBuilder cols = new StringBuilder(200);
		StringBuilder vals = new StringBuilder(100);
		for(int i=0;i<keys.length;i++) {
			cols.append("`" + keys[i] + "`");
			if (i != keys.length-1) cols.append(",");
			vals.append("'" + values[i] + "'");
			if (i != keys.length-1) vals.append(",");
		}
		
		query = "INSERT IGNORE INTO `user_vars` (`id`,`user_id`," + cols.toString() + ") VALUES (NULL,(SELECT `id` FROM `users` WHERE `username` = '" + username + "')," + vals.toString() + ")";

		conn.executeQuery(query.toString());
		
		query = new StringBuilder("UPDATE `user_vars` SET ");
		
		for(int i=0;i<keys.length;i++) {
			((StringBuilder)query).append("`" + keys[i] + "` = '" + values[i] + "'");
			if (i != keys.length-1) ((StringBuilder)query).append(", ");
		}
		((StringBuilder)query).append(" WHERE user_id = (SELECT `id` FROM `users` WHERE `username` = '" + username + "')");

		conn.executeQuery(query.toString());
			
	}
}
