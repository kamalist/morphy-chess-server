package morphy.command;

import morphy.service.UserService;
import morphy.user.UserSession;

import org.apache.commons.lang.StringUtils;

public class WhoCommand extends AbstractCommand {
	public WhoCommand() {
		super("who");
	}

	public void process(String arguments, UserSession userSession) {
		UserSession[] users = UserService.getInstance().getLoggedInUsers();

		StringBuilder output = new StringBuilder(2000);
		output.append("List of users logged in:\n");
		int counter = 0;
		for (int i = 0; i < users.length; i++) {
			output.append(StringUtils.rightPad(
					users[i].getUser().getUserName(), 20));
			if (counter >= 4) {
				output.append("\n");
				counter = 0;
			}
		}
		output.append("\n(" + users.length + " users.)");
		userSession.send(output.toString());
	}
}