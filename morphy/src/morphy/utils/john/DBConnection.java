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
package morphy.utils.john;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import morphy.service.UserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBConnection {
	protected static Log LOG = LogFactory.getLog(UserService.class);
	
	private enum DBType { MySQL,Derby; }
	
	private DBType type;
	private Statement statement;
	
	public DBConnection() {
		try {
			DBType type = DBType.MySQL;
			this.type = type;
			
			String driver = "";
			if (type == DBType.MySQL) driver = "com.mysql.jdbc.Driver";
			if (type == DBType.Derby) driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver).newInstance();
			
			String connectionString = "";
			if (type == DBType.MySQL) connectionString = "jdbc:mysql://localhost/morphyics?user=root&password=abcdef";
			if (type == DBType.Derby) connectionString = "jdbc:derby:MorphyICSDB;";
			Connection conn = DriverManager.getConnection(connectionString);
			
			Statement s = conn.createStatement();
			this.statement = s;
		} catch(Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Statement getStatement() {
		return statement;
	}
	
	/**
	 * Shorthand for getStatement().execute(query).
	 */
	public boolean executeQuery(String query) {
		try {
			return statement.execute(query);
		} catch(SQLException se) {
			if (LOG.isErrorEnabled()) {
				LOG.error(se);
			}
			return false;
		}
	}
	
	public void closeConnection() {
		try { 
			if (type == DBType.Derby)
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch(Exception e) {
			// shutting down derby always throws an exception, even if sucessful.
			if (LOG.isInfoEnabled()) {
				LOG.info("Derby engine shutdown successful.");
			}
		}
		
		try {
			if (type == DBType.MySQL)
				statement.close();
		} catch(SQLException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e);
			}
		}
	}
}
