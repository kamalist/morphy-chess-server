package morphy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import morphy.Morphy;
import morphy.command.Command;
import morphy.command.HelpCommand;
import morphy.command.QuitCommand;
import morphy.command.ShoutCommand;
import morphy.command.TellCommand;
import morphy.command.WhoCommand;
import morphy.user.SocketChannelUserSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandService implements Service {
	protected static Log LOG = LogFactory.getLog(CommandService.class);

	@SuppressWarnings("unchecked")
	private static final Class[] socketCommandsClasses = { HelpCommand.class,
			QuitCommand.class, ShoutCommand.class, TellCommand.class,
			WhoCommand.class };

	protected List<Command> commands = new ArrayList<Command>(100);
	protected Map<String, Command> firstWordToCommandMap = new TreeMap<String, Command>();

	private static final CommandService singletonInstance = new CommandService();

	public static CommandService getInstance() {
		return singletonInstance;
	}

	public void dispose() {
		commands.clear();
		firstWordToCommandMap.clear();

	}

	@SuppressWarnings("unchecked")
	private CommandService() {
		long startTime = System.currentTimeMillis();

		for (Class clazz : socketCommandsClasses) {
			try {
				Command command = (Command) clazz.newInstance();
				commands.add(command);
			} catch (Throwable t) {
				Morphy.getInstance()
						.onError(
								"Error inializing SocketCommand: "
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
	}

	public Command[] getCommands() {
		return commands.toArray(new Command[0]);
	}

	public Command getCommand(String keyword) {
		keyword = keyword.toLowerCase();
		return firstWordToCommandMap.get(keyword);
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
			socketCommand.process(content, userSession);
		} else {
			userSession.send(keyword + ": Command not found.");
		}
	}
}
