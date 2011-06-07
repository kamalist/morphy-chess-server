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
import morphy.user.SocketChannelUserSession;
import morphy.user.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GameService implements Service {
	protected static Log LOG = LogFactory.getLog(GameService.class);
	private static final GameService singletonInstance = new GameService();
	
	protected int mostConcurrentGames = 0;
	public HashMap<UserSession,Game> map = new HashMap<UserSession,Game>();
	protected List<Game> games;
	
	public static GameService getInstance() {
		return singletonInstance;
	}
	
	private String generateGin(Game g,boolean gameStart) {
		if (gameStart) {
			return "{Game " + g.getGameNumber() + " (" + g.getWhite().getUser().getUserName() + " vs. " + g.getBlack().getUser().getUserName() + ") Creating " + (g.isRated()?"rated":"unrated") + " " + g.getVariant().name() + " match.}";
		} else {
			//{Game 1 (johnthegreat vs. GuestZJBY) Game aborted by mutual agreement} *
			return "{Game " + g.getGameNumber() + " (" + g.getWhite().getUser().getUserName() + " vs. " + g.getBlack().getUser().getUserName() + ") " + g.getReason() + "} " + g.getResult();
		}
	}
	
	private void sendGin(Game g,boolean gameStart) {
		UserService s = UserService.getInstance();
		UserSession[] arr = s.fetchAllUsersWithVariable("gin","1");
		String line = generateGin(g,gameStart);
		for(UserSession sess : arr) {
			sess.send(line);
		}
	}
	
	public void endGame(Game g) {
		sendGin(g,false);
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
		g.setTimeGameStarted(System.currentTimeMillis());
		g.setWhiteClock(g.getTime() * (60*1000));
		g.setBlackClock(g.getTime() * (60*1000));
		
		map.put(g.getWhite(),g);
		map.put(g.getBlack(),g);
	
		String line = "Creating: " + g.getWhite().getUser().getUserName() + " (----) " + g.getBlack().getUser().getUserName() + " (----) " + (g.isRated()?"rated":"unrated") + " " + g.getVariant().name() + " " + g.getTime() + " " + g.getIncrement();

		String tmpLine = g.getBlack().getUser().getUserName() + " accepts the match offer.\n\n"+line+"\n"+generateGin(g,true)+"\n";
		if (g.getWhite().getUser().getUserVars().getIVariables().get("gameinfo").equals("1")) {
			tmpLine += g.generateGameInfoLine();
		}
		tmpLine += "\n"+g.getWhite().getUser().getUserVars().getStyle().print(white,g);
		g.getWhite().send(tmpLine);
		
		tmpLine = "You accept the match offer from " + g.getWhite().getUser().getUserName()+".\n\n"+line+"\n"+generateGin(g,true)+"\n";
		if (g.getBlack().getUser().getUserVars().getIVariables().get("gameinfo").equals("1")) {
			tmpLine += g.generateGameInfoLine();
		}
		tmpLine += "\n"+g.getBlack().getUser().getUserVars().getStyle().print(black,g);
		g.getBlack().send(tmpLine);
		
		
		g.processMoveUpdate(false);
		
		((SocketChannelUserSession)g.getWhite()).setPlaying(true);
		((SocketChannelUserSession)g.getBlack()).setPlaying(true);
		
		sendGin(g,true);
		games.add(g);
		if (games.size() > mostConcurrentGames) {
			mostConcurrentGames = games.size();
		}
		
		return g;
	}
	
	
	
	public List<Game> getGames() {
		return games;
	}
	
	/** O(N) performance */
	public Game findGameById(int id) {
		for(Game g : games) {
			if (g.getGameNumber() == id)
				return g;
		}
		return null;
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
		return games.size();
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
