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
package morphy.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import morphy.user.UserLevel;
import morphy.utils.john.ServerList;

public class ServerListManagerService implements Service {
	protected static Log LOG = LogFactory.getLog(ServerListManagerService.class);
	
	private static final ServerListManagerService singletonInstance = 
		new ServerListManagerService();
	
	public static ServerListManagerService getInstance() {
		return singletonInstance;
	}

	private List<ServerList> lists;
	private Map<ServerList,List<String>> elements;
	
	
	public Map<ServerList, List<String>> getElements() {
		return elements;
	}
	
	public List<ServerList> getLists() {
		return lists;
	}

	private ServerListManagerService() {
		initialize();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialized ServerListManagerService.");
		}
	}
	
	private void initialize() {
		lists = new ArrayList<ServerList>();
		
		lists.add(new ServerList("admin",UserLevel.HeadAdmin,"(*)"));
		lists.add(new ServerList("removedcom",UserLevel.SuperAdmin,""));
		lists.add(new ServerList("filter",UserLevel.SuperAdmin,""));
		lists.add(new ServerList("ban",UserLevel.Admin,""));
		lists.add(new ServerList("noteban",UserLevel.Admin,""));
		lists.add(new ServerList("abuser",UserLevel.Admin,""));
		lists.add(new ServerList("muzzle",UserLevel.Admin,""));
		lists.add(new ServerList("SR",UserLevel.Admin,"(SR)"));
		lists.add(new ServerList("TM",UserLevel.Admin,"(TM)"));
		lists.add(new ServerList("CA",UserLevel.Admin,"(CA)"));
		lists.add(new ServerList("FM",UserLevel.Admin,"(FM)"));
		lists.add(new ServerList("IM",UserLevel.Admin,"(IM)"));
		lists.add(new ServerList("GM",UserLevel.Admin,"(GM)"));
		lists.add(new ServerList("WFM",UserLevel.Admin,"(WFM)"));
		lists.add(new ServerList("WIM",UserLevel.Admin,"(WIM)"));
		lists.add(new ServerList("WGM",UserLevel.Admin,"(WGM)"));
		
		lists.add(new ServerList("Blind",UserLevel.Admin,"(B)"));
		lists.add(new ServerList("Computer",UserLevel.Admin,"(C)"));
		lists.add(new ServerList("Demo",UserLevel.Admin,"(D)"));
		lists.add(new ServerList("Team",UserLevel.Admin,"(T)"));
		lists.add(new ServerList("TD",UserLevel.SuperAdmin,"(TD)"));
		
		elements = new HashMap<ServerList,List<String>>();
		
		for(ServerList l : lists) {
			elements.put(l,new ArrayList<String>());
		}
	}
	
	public boolean isOnList(ServerList list,String username) {
		return listContainsIgnoreCase(elements.get(list),username);
	}
	
	/**
	 * Returns if param 'username' is on ANY of param 'lists'.
	 * @param lists
	 * @param username
	 * @return
	 */
	public boolean isOnAnyList(ServerList[] lists,String username) {
		boolean is = false;
		for(ServerList list : lists) {
			is = listContainsIgnoreCase(elements.get(list),username);
			if (is) return true;
		}
		return is;
	}
	
	public ServerList getList(String name) {
		for(ServerList list : lists) {
			if (list.getName().equalsIgnoreCase(name))
				return list;
		}
		return null;
	}
	
	private boolean listContainsIgnoreCase(List<String> list,String element) {
		for(String s : list) {
			if (s.equalsIgnoreCase(element))
				return true;
		}
		return false;
	}
	
	public void dispose() {
		if (lists != null)
			lists.clear();		
	}

}
