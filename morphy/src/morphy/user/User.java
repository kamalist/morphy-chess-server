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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
	public static final int MAX_LIST_SIZE = 50;

	protected String userName;
	protected UserLevel userLevel;
	protected PlayerType playerType;
	protected UserVars userVars = new UserVars(this);
	protected Formula formula;
	
	private Map<PersonalList,List<String>> personalLists;
	
	public User() {
		setUserInfoLists(new HashMap<UserInfoList,List<String>>());
		for(UserInfoList u : UserInfoList.values()) {
			getUserInfoLists().put(u,new ArrayList<String>());
		}
		setLists(new HashMap<PersonalList,List<String>>());
		for(PersonalList u : PersonalList.values()) {
			getLists().put(u,new ArrayList<String>());
		}
	}
	
	void setLists(Map<PersonalList, List<String>> lists) {
		this.personalLists = lists;
	}
	public Map<PersonalList, List<String>> getLists() {
		return personalLists;
	}

	private Map<UserInfoList,List<String>> userInfoLists;
	
	void setUserInfoLists(Map<UserInfoList,List<String>> userInfoLists) {
		this.userInfoLists = userInfoLists;
	}
	
	public Map<UserInfoList,List<String>> getUserInfoLists() {
		return userInfoLists;
	}

	/**
	 * Returns if userName is on list.
	 * @param list
	 * @param userName
	 * @return
	 */
	public boolean isOnList(PersonalList list,String userName) {
		List<String> myList = getLists().get(list);
		if (myList == null) return false;
		if (myList.contains(userName)) return true;
		return false;
	}


	public PlayerType getPlayerType() {
		return playerType;
	}

	public UserLevel getUserLevel() {
		return userLevel;
	}

	public String getUserName() {
		return userName;
	}

	public UserVars getUserVars() {
		return userVars;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}

	public void setUserLevel(UserLevel userLevel) {
		this.userLevel = userLevel;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserVars(UserVars userVars) {
		this.userVars = userVars;
	}
}
