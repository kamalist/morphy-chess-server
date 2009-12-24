/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008,2009  http://code.google.com/p/morphy-chess-server/
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

import org.apache.commons.lang.StringUtils;

import morphy.service.CommandService;
import morphy.user.UserSession;
import morphy.utils.MorphyStringUtils;

public class HelpCommand extends AbstractCommand {
	public HelpCommand() {
		super("help");
	}

	public void process(String arguments, UserSession userSession) {
		String argument = arguments.trim();

		if (argument.length() == 0) {
			StringBuilder result = new StringBuilder(1000);
			result.append("Help is avalailable on the following commands:\n");
			result
					.append("(Type help commandName for help on the command)\n\n");
			int counter = 0;
			Command[] commands = CommandService.getInstance().getCommands();
			for (Command command : commands) {
				result.append(StringUtils.rightPad(command.getContext()
						.getName(), 15));
				counter++;
				if (counter == 5) {
					result.append("\n");
					counter = 0;
				}
			}
			userSession.send(result.toString());
		} else {
			Command command = CommandService.getInstance().getCommand(argument);
			if (command == null) {
				userSession.send("No help avaliable for: " + argument);
			} else {
				StringBuilder builder = new StringBuilder(200);
				builder.append("Help for " + command.getContext().getName()
						+ "\n");
				builder.append("Usage: " + command.getContext().getUsage()
						+ "\n");
				builder.append("Level: " + command.getContext().getUserLevel()
						+ "\n");
				builder.append("Descriptiopn:\n");

				StringBuilder postBuilder = new StringBuilder(200);
				postBuilder.append("\nAliases: "
						+ MorphyStringUtils.toDelimitedString(command
								.getContext().getAliases(), " ") + "\n");
				postBuilder.append("See Also: "
						+ MorphyStringUtils.toDelimitedString(command
								.getContext().getSeeAlso(), " ") + "\n");
				postBuilder.append("Last Modofied By: "
						+ command.getContext().getLastModifiedBy() + " on "
						+ command.getContext().getLastModifiedBy());

				userSession.send(MorphyStringUtils.replaceNewlines(builder
						.toString())
						+ command.getContext().getHelp()
						+ MorphyStringUtils.replaceNewlines(postBuilder
								.toString()));
			}
		}

	}
}