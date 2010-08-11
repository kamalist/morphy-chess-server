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

import morphy.command.AddCensorCommand;
import morphy.command.AddGnotifyCommand;
import morphy.command.AddListCommand;
import morphy.command.AddNopartnerCommand;
import morphy.command.AddNoplayCommand;
import morphy.command.AddNotifyCommand;
import morphy.command.AddPlayerCommand;
import morphy.command.AddRemoteCommand;
import morphy.command.AdminCommand;
import morphy.command.Command;
import morphy.command.DateCommand;
import morphy.command.FingerCommand;
import morphy.command.HelpCommand;
import morphy.command.ISetCommand;
import morphy.command.IVariablesCommand;
import morphy.command.InchannelCommand;
import morphy.command.ItShoutCommand;
import morphy.command.NukeCommand;
import morphy.command.QtellCommand;
import morphy.command.QuitCommand;
import morphy.command.RemoveListCommand;
import morphy.command.SetCommand;
import morphy.command.ShoutCommand;
import morphy.command.ShowListCommand;
import morphy.command.TellCommand;
import morphy.command.VariablesCommand;
import morphy.command.WhoCommand;
import morphy.command.ZNotifyCommand;
import morphy.user.SocketChannelUserSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandService implements Service {
	protected static Log LOG = LogFactory.getLog(CommandService.class);
	
	private static final Class<?>[] socketCommandsClasses = { 
	 	AddCensorCommand.class,
	 	AddGnotifyCommand.class,
	 	AddListCommand.class,
	 	AddNopartnerCommand.class,
		AddNoplayCommand.class,
		AddNotifyCommand.class,
		AddPlayerCommand.class,
		AddRemoteCommand.class,
		AdminCommand.class,
	
		DateCommand.class,
		
		FingerCommand.class,
		
		HelpCommand.class,
		
		InchannelCommand.class,
		ISetCommand.class,
		ItShoutCommand.class,
		IVariablesCommand.class,
		
		NukeCommand.class,
		
		QtellCommand.class,
		QuitCommand.class,
		
		RemoveListCommand.class,
		
		SetCommand.class,
		ShoutCommand.class,
		ShowListCommand.class,
		
		TellCommand.class,
		
		VariablesCommand.class,
		
		WhoCommand.class,
		
		ZNotifyCommand.class
	};

	protected List<Command> commands = new ArrayList<Command>(100);
	protected Map<String, Command> firstWordToCommandMap = new TreeMap<String, Command>();

	private static final CommandService singletonInstance = new CommandService();

	public static CommandService getInstance() {
		return singletonInstance;
	}

	@SuppressWarnings("unchecked")
	private CommandService() {
		long startTime = System.currentTimeMillis();

		for (Class clazz : socketCommandsClasses) {
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
//			if (keyword.matches("^[qui]$")) {
			if (keyword.equals("q") || keyword.equals("qu")) {
				userSession.send("" + UserService.getInstance().getTags(userSession.getUser().getUserName()) + 
						" tells you: The command 'quit' cannot be abbreviated.");
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
