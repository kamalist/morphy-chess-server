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

import morphy.game.request.Request;
import morphy.service.RequestService;
import morphy.user.UserSession;

public class AcceptCommand extends AbstractCommand {
	public AcceptCommand() {
		super("accept");
	}

	public void process(String arguments, UserSession userSession) {
		RequestService rs = RequestService.getInstance();
		List<Request> list = rs.getRequestsTo(userSession);
		int num = list.size();
		
		if (num >= 2) {
			userSession.send("There is more than one pending offer.\nType \"pending\" to see the list of offers.\nType \"accept n\" to accept an offer.");
			return;
		}
		
		if (num == 0) {
			userSession.send("There are no offers to accept.");
			return;
		}
		
		if (num == 1) {
			// 
			list.get(0).acceptAction();
		}
	}
}
