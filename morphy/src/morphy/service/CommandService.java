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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import morphy.command.AbortCommand;
import morphy.command.AcceptCommand;
import morphy.command.AddListCommand;
import morphy.command.AddPlayerCommand;
import morphy.command.AdminCommand;
import morphy.command.AllObserversCommand;
import morphy.command.AnnounceCommand;
import morphy.command.AnnunregCommand;
import morphy.command.BclockCommand;
import morphy.command.BnameCommand;
import morphy.command.BratingCommand;
import morphy.command.BugWhoCommand;
import morphy.command.ClearmessagesCommand;
import morphy.command.Command;
import morphy.command.DateCommand;
import morphy.command.ExamineCommand;
import morphy.command.FingerCommand;
import morphy.command.GamesCommand;
import morphy.command.HandlesCommand;
import morphy.command.HelpCommand;
import morphy.command.ISetCommand;
import morphy.command.IVariablesCommand;
import morphy.command.InchannelCommand;
import morphy.command.ItShoutCommand;
import morphy.command.LLoginsCommand;
import morphy.command.LoginsCommand;
import morphy.command.MatchCommand;
import morphy.command.MessageCommand;
import morphy.command.MessagesCommand;
import morphy.command.MexamineCommand;
import morphy.command.MovesCommand;
import morphy.command.NewsCommand;
import morphy.command.NukeCommand;
import morphy.command.ObserveCommand;
import morphy.command.PartnerCommand;
import morphy.command.PendingCommand;
import morphy.command.QtellCommand;
import morphy.command.QuitCommand;
import morphy.command.RemoveListCommand;
import morphy.command.SRCommand;
import morphy.command.SetCommand;
import morphy.command.ShoutCommand;
import morphy.command.ShowListCommand;
import morphy.command.ShutdownCommand;
import morphy.command.SummonCommand;
import morphy.command.TellCommand;
import morphy.command.UnexamineCommand;
import morphy.command.UptimeCommand;
import morphy.command.VariablesCommand;
import morphy.command.WclockCommand;
import morphy.command.WhoCommand;
import morphy.command.WithdrawCommand;
import morphy.command.WnameCommand;
import morphy.command.WratingCommand;
import morphy.command.ZNotifyCommand;
import morphy.game.ExaminedGame;
import morphy.game.Game;
import morphy.user.SocketChannelUserSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import board.Board;
import board.IllegalMoveException;
import board.WrongColorToMoveException;
import board.printer.Style12Printer;

public class CommandService implements Service {
	protected static Log LOG = LogFactory.getLog(CommandService.class);
	protected static Pattern listAliasPattern = Pattern
			.compile("(\\+|-|=)(fm|im|gm|wfm|wim|wgm|blind|teams|computer|" +
					"tm|ca|sr|td|censor|gnotify|noplay|" +
					"notify|channel|idlenotify)");
	
	private static final Class<?>[] socketCommandsClasses = {
		// please try to maintain this list in alphabetical order
		
		AbortCommand.class,
		AcceptCommand.class,
		
//	 	AddCensorCommand.class,
//	 	AddGnotifyCommand.class,
	 	AddListCommand.class,
//	 	AddNopartnerCommand.class,
//		AddNoplayCommand.class,
//		AddNotifyCommand.class,
	 	AddPlayerCommand.class,
//		AddRemoteCommand.class,
		AdminCommand.class,
		AllObserversCommand.class,
		AnnounceCommand.class,
		AnnunregCommand.class,
	
		BclockCommand.class,
		BnameCommand.class,
		BratingCommand.class,
		BugWhoCommand.class,
		
		ClearmessagesCommand.class,
		
		DateCommand.class,
		
		ExamineCommand.class,
		
		FingerCommand.class,
		
		GamesCommand.class,
		
		HandlesCommand.class,
		HelpCommand.class,
		
		InchannelCommand.class,
		ISetCommand.class,
		ItShoutCommand.class,
		IVariablesCommand.class,
		
		LLoginsCommand.class,
		LoginsCommand.class,
		
		MatchCommand.class,
		MessageCommand.class,
		MessagesCommand.class,
		MexamineCommand.class,
		MovesCommand.class,
		
		NewsCommand.class,
		NukeCommand.class,
		
		ObserveCommand.class,
		
		PartnerCommand.class,
		PendingCommand.class,
		
		QtellCommand.class,
		QuitCommand.class,
		
		RemoveListCommand.class,
		
		SetCommand.class,
		ShoutCommand.class,
		ShowListCommand.class,
		ShutdownCommand.class,
		SRCommand.class,
		SummonCommand.class,
		
		TellCommand.class,
		
		UnexamineCommand.class,
		UptimeCommand.class,
		
		VariablesCommand.class,
		
		WclockCommand.class,
		WhoCommand.class,
		WithdrawCommand.class,
		WnameCommand.class,
		WratingCommand.class,
		
		ZNotifyCommand.class
	};

	protected List<Command> commands = new ArrayList<Command>(100);
	protected Map<String, Command> firstWordToCommandMap = new TreeMap<String, Command>();

	private static final CommandService singletonInstance = new CommandService();

	public static CommandService getInstance() {
		return singletonInstance;
	}

	private CommandService() {
		long startTime = System.currentTimeMillis();

		for (Class<?> clazz : socketCommandsClasses) {
			try {
				Command command = (Command) clazz.newInstance();
				commands.add(command);
			} catch (Throwable t) {
				if (LOG.isErrorEnabled())
					LOG.error("Error inializing SocketCommand: "
								+ clazz.getName(), t);
			}
		}
		Collections.sort(commands);

		for (Command command : commands) {
			String name = command.getContext().getName().toLowerCase();
			firstWordToCommandMap.put(command.getContext().getName(), command);
			for (String alias : command.getContext().getAliases()) {
				firstWordToCommandMap.put(alias, command);
			}
			if (name.length() > 1) {
				for (int i = command.getContext().getName().length() - 2; i >= 0; i--) {
					String shortcut = command.getContext().getName()
							.toLowerCase().substring(0, i);
					boolean isUnique = true;
					for (Command commandToCheck : commands) {
						if (commandToCheck != command) {
							String nameToCompare = commandToCheck.getContext()
									.getName().toLowerCase();
							if (StringUtils.startsWithIgnoreCase(nameToCompare,
									shortcut)) {
								isUnique = false;
								break;
							}
						}
					}
					if (isUnique) {
						firstWordToCommandMap.put(shortcut, command);
					} else {
						break;
					}
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Initialized Command Service " + commands.size()
					+ " commands " + firstWordToCommandMap.values().size()
					+ " mappings " + (System.currentTimeMillis() - startTime)
					+ "ms");
		}
		
		//debug();
		
	}
	
	public void processCommandAndCheckAliases(String command,SocketChannelUserSession userSession) {
		command = command.trim();
		
		if (Board.isValidSAN(command)) {
			morphy.game.GameInterface g = GameService.getInstance().map.get(userSession);
			
			//if ((SocketChannelUserSession)userSession).isPlaying()
			
			if (g != null) {
				try {
					if (g instanceof Game) {
						Game gg = (Game)g;
						gg.getBoard().move(gg.getWhite().equals(userSession),command);
						gg.getBoard().getLatestMove().setPrinter(new Style12Printer());
						gg.touchLastMoveMadeTime();
						gg.processMoveUpdate(true);
					}
					if (g instanceof ExaminedGame) {
						ExaminedGame gg = (ExaminedGame)g;
						gg.getBoard().move(gg.isWhitesMove(),command);
						gg.getBoard().getLatestMove().setPrinter(new Style12Printer());
						gg.setWhitesMove(!gg.isWhitesMove());
						gg.processMoveUpdate(true);
					}
				} catch(WrongColorToMoveException e) { userSession.send("It is not your move."); }
				catch(IllegalMoveException e) { userSession.send("Illegal move (" + command + ")."); }
			} else {
				userSession.send("You are not playing or examining a game.");
				
			}
			return;
		}
		
		String keyword = null;
		String content = null;
		
		int spaceIndex = command.indexOf(' ');
		if (spaceIndex == -1) {
			keyword = command.toLowerCase();
			content = "";
		} else {
			keyword = command.substring(0, spaceIndex).toLowerCase();
			content = command.substring(spaceIndex + 1);
		}
		
		Matcher m = listAliasPattern.matcher(keyword);
		if (m.matches()) {
			String what = regexHelper(m.group(1));
			String whatlist = m.group(2);
			String s = what + " " + whatlist + " " + content;
			processCommand(s,userSession);
		} else if (keyword.equals("=") && content.equals("")) { 
			processCommand("showlist",userSession);
		} else if (keyword.equals(".")) {
			morphy.user.UserSession sess = userSession.getLastPersonToldTo();
			if (sess != null) {
				processCommand("tell " + sess.getUser().getUserName() + " " + content,userSession);
			} else { userSession.send("I don't know who to say that to."); return; }
		} else if (keyword.equals(",")) {
			morphy.channel.Channel c = userSession.getLastChannelToldTo();
			if (c != null) {
				processCommand("tell " + c.getNumber() + " " + content,userSession);
			} else { userSession.send("I don't know who to say that to."); return; }
		} else {
			processCommand(command,userSession);
		}
	}

	/**
	 * Assists processCommandAndCheckAliases method.
	 */
	private String regexHelper(String s) {
		if (s.equals("+"))
			return "addlist";
		if (s.equals("="))
			return "showlist";
		if (s.equals("-"))
			return "removelist";
		return null;
	}
	
	public void dispose() {
		commands.clear();
		firstWordToCommandMap.clear();

	}

	public Command getCommand(String keyword) {
		keyword = keyword.toLowerCase();
		return firstWordToCommandMap.get(keyword);
	}

	public Command[] getCommands() {
		return commands.toArray(new Command[commands.size()]);
	}

	public void processCommand(String command,
			SocketChannelUserSession userSession) {
		command = command.trim();
		String keyword = null;
		String content = null;

		int spaceIndex = command.indexOf(' ');
		if (spaceIndex == -1) {
			keyword = command.toLowerCase();
			content = "";
		} else {
			keyword = command.substring(0, spaceIndex).toLowerCase();
			content = command.substring(spaceIndex + 1);
		}

		Command socketCommand = firstWordToCommandMap.get(keyword);

		if (socketCommand == null) {
			userSession.send(keyword + ": Command not found.");
		} else if (socketCommand.willProcess(userSession)) {
			if (keyword.equals("q") || keyword.equals("qu")) {
				userSession.send("" + UserService.getInstance().getTags(userSession.getUser().getUserName()) + 
						" tells you: The command 'quit' cannot be abbreviated.");
				return;
			} else {
				socketCommand.process(content, userSession);
			}
		} else {
			userSession.send(keyword + ": Command not found.");
		}
	}
	
	public void debug() {
		java.util.Set<String> keys = firstWordToCommandMap.keySet();
		for(String s : keys) {
			System.out.println(s);
		}
	}
}
