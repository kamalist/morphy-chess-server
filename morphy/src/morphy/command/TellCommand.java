package morphy.command;

import morphy.service.UserService;
import morphy.user.UserSession;

public class TellCommand extends AbstractCommand {
	public TellCommand() {
		super("tell");
	}

	public void process(String arguments, UserSession userSession) {
		int spaceIndex = arguments.indexOf(' ');
		if (spaceIndex == -1) {
			userSession.send(getContext().getUsage());
		} else {
			String userName = arguments.substring(0, spaceIndex);
			String message = arguments.substring(spaceIndex + 1, arguments
					.length());
			UserSession personToTell = UserService.getInstance()
					.getUserSession(userName);
			if (personToTell == null) {
				userSession.send("User " + userName + " is not logged in.");
			} else {
				personToTell.send(userSession.getUser().getUserName()
						+ " tells you: " + message);
				userSession.send("(told "
						+ personToTell.getUser().getUserName() + ")");
			}
		}
	}
}
