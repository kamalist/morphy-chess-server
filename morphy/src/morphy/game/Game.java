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
package morphy.game;

import java.util.ArrayList;
import java.util.List;

import morphy.user.UserSession;

public class Game {
	private UserSession white;
	private UserSession black;
	private int time;
	private int increment;
	
	List<UserSession> observers;
	
	public Game() {
		observers = new ArrayList<UserSession>();
	}
	
	public Game(UserSession white,UserSession black,int time,int increment) {
		this();
		setWhite(white);
		setBlack(black);
		setTime(time);
		setIncrement(increment);
	}
	
	public void addObserver(UserSession observer) {
		observers.add(observer);
	}
	
	public UserSession[] getObservers() {
		return observers.toArray(new UserSession[observers.size()]);
	}

	protected void setWhite(UserSession white) {
		this.white = white;
	}

	public UserSession getWhite() {
		return white;
	}

	protected void setBlack(UserSession black) {
		this.black = black;
	}

	public UserSession getBlack() {
		return black;
	}

	protected void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}

	protected void setIncrement(int increment) {
		this.increment = increment;
	}

	public int getIncrement() {
		return increment;
	}
}
