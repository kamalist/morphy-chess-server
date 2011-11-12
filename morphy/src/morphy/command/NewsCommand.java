/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008-2011  http://code.google.com/p/morphy-chess-server/
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
import java.util.TimeZone;

import morphy.service.DBConnectionService;
import morphy.user.UserSession;
import morphy.utils.john.DBConnection;
import morphy.utils.john.TimeZoneUtils;

public class NewsCommand extends AbstractCommand {
	public NewsCommand() {
		super("news");
	}

	// news [n | all]
	public void process(String arguments, UserSession userSession) {		
		arguments = arguments.trim();
		
		if (arguments.matches("[0-9]+")) {
			String query = "SELECT n.id,n.name,n.content,n.posted_timestamp,n.expires_timestamp,u.username FROM newsitems n INNER JOIN users u ON (u.id = n.posted_by_user_id) WHERE n.id = '" + arguments + "'"; 
			
			DBConnectionService service = DBConnectionService.getInstance();
			DBConnection conn = service.getDBConnection();
			java.sql.ResultSet rs = conn.executeQueryWithRS(query);
			try {
				if(rs.next()) {
					StringBuilder str = new StringBuilder(300);
					SimpleDateFormat f = new SimpleDateFormat("EEE, MMM d");
					str.append(String.format("%4d",rs.getInt(1)) + " (" + f.format(rs.getDate(4)) + ") " + rs.getString(2) + "\n\n");
					f = new SimpleDateFormat("EEE MMM d, HH:mm z yyyy");
					java.util.Map<String,String> map = userSession.getUser().getUserVars().getVariables();
					String tzone = "";
					if (map.containsKey("tzone")) tzone = map.get("tzone");
					if (tzone.equals("SERVER")) tzone = TimeZoneUtils.getAbbreviation(TimeZone.getDefault());
					f.setTimeZone(TimeZoneUtils.getTimeZone(tzone));
					str.append(rs.getString(3) + "\n\nPosted by " + rs.getString(6) + (rs.getDate(5)!= null?" (Expires: " + f.format(rs.getDate(5)) + ")":"") + ".");
					userSession.send(str.toString());
					return;
				} else {
					userSession.send("That news item does not exist, or has expired.");
					return;
				}
			} catch(java.sql.SQLException e) { morphy.Morphy.getInstance().onError("SQLException while trying to retrieve news item #" + arguments,e); }
				
		} else if (arguments.equals("") || arguments.equals("all") || arguments.matches("^[0-9]+-[0-9]+$")) {
			int limit = 10;
			if (arguments.equals("")) limit = 10;
			if (arguments.equals("all")) limit = 9999;
			String query = "SELECT id,name,posted_timestamp FROM newsitems "; 
			
			if (arguments.matches("^[0-9]+-[0-9]+$")) { 
				String[] arr = arguments.split("-"); 
				query += "WHERE id BETWEEN " + arr[0] + " AND " + arr[1] + " "; 
			}
			query += "ORDER BY id ASC LIMIT " + limit;
			
			SimpleDateFormat f = new SimpleDateFormat("EEE, MMM d");
			
			DBConnectionService service = DBConnectionService.getInstance();
			DBConnection conn = service.getDBConnection();
			java.sql.ResultSet rs = conn.executeQueryWithRS(query);
			try {
				StringBuilder str = new StringBuilder(300);
				while(rs.next()) {
					str.append(String.format("%4d",rs.getInt(1)) + " (" + f.format(rs.getDate(3)) + ") " + rs.getString(2) + "\n");
				}
				userSession.send(str.toString());
			} catch(java.sql.SQLException e) { morphy.Morphy.getInstance().onError("SQLException while trying to retrieve news items. query: "+query,e); }
		} else {
			userSession.send(getContext().getUsage());
			return;
		}
	}
}
