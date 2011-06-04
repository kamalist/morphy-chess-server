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

import morphy.game.Game;
import morphy.game.MatchParams;
import morphy.user.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GameService implements Service {
	protected static Log LOG = LogFactory.getLog(GameService.class);
	private static final GameService singletonInstance = new GameService();
	
	protected int mostConcurrentGames = 0;
	protected HashMap<UserSession,Game> map = new HashMap<UserSession,Game>();
	protected List<Game> games;
	
	public static GameService getInstance() {
		return singletonInstance;
	}
	
	private void sendGin(Game g) {
		UserService s = UserService.getInstance();
		UserSession[] arr = s.fetchAllUsersWithVariable("gin","1");
		for(UserSession sess : arr) {
			sess.send("{Game " + g.getGameNumber() + " (" + g.getWhite().getUser().getUserName() + " vs. " + g.getBlack().getUser().getUserName() + ") Creating " + (g.isRated()?"rated":"unrated") + " " + g.getVariant().name() + "  match.}");
		}
	}
	
	public Game createGame(UserSession white,UserSession black,MatchParams params) {
		Game g = new Game();
		g.setWhite(white);
		g.setBlack(black);
		g.setTime(params.getTime());
		g.setIncrement(params.getIncrement());
		g.setRated(params.isRated());
		g.setVariant(params.getVariant());
		g.setGameNumber(1);
		sendGin(g);
		return g;
	}
	
	public GameService() {
		games = new ArrayList<Game>();
		
		if (LOG.isInfoEnabled())
			LOG.info("Initialized GameService.");
	}
	
	/**
	 * Returns the next available (not taken) game number
	 * to be assigned to a board.
	 */
	public int getNextAvailableGameNumber() {
		return 0;
	}
	
	/** Returns the number of games currently being played. */
	public int getCurrentNumberOfGames() {
		return 0;
	}
	
	/**
	 * 
	 */
	public void addGame() {
		
	}
	
	/**
	 * 
	 */
	public void removeGame() {
		
	}
	
	/** Returns the most number of games played 
	 * at any given time on the server since loaded. */
	public int getHighestNumberOfGames() {
		return mostConcurrentGames;
	}

	public void dispose() {
		
		
		if (LOG.isInfoEnabled())
			LOG.info("GameService disposed.");
	}
	
	
}
