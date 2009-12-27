package morphy.command;

import morphy.service.UserService;
import morphy.user.UserSession;

public class QtellCommand extends AbstractCommand {

	public QtellCommand() {
		super("qtell");
	}

	public void process(String arguments, UserSession userSession) {
		// if (userSession.getUser().getUserLevel() != UserLevel.Bot) {
		// userSession.send("Only TD programs and admins are allowed to use this command.");
		// return;
		// }

		int whiteSpacePos = arguments.indexOf(" ");
		if (whiteSpacePos == -1) {
			userSession.send(getContext().getUsage());
			return;
		}
		String userName = arguments.substring(0, whiteSpacePos);
		String message = arguments.substring(whiteSpacePos + 1, arguments
				.length() - 1);

		UserSession sendTo = UserService.getInstance().getUserSession(userName);
		if (sendTo != null) {
			sendTo.send(":" + message);
		}

		userSession.send("*qtell " + userName + " "
				+ ((sendTo.isConnected()) ? "1" : "0"));
	}

}
