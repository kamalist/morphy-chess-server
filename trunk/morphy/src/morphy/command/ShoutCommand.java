package morphy.command;

import morphy.service.UserService;
import morphy.user.UserSession;

public class ShoutCommand extends AbstractCommand {
	public ShoutCommand() {
		super("shout");
	}

	public void process(String arguments, UserSession userSession) {
		UserSession[] sessions = UserService.getInstance().getLoggedInUsers();
		String shoutedMessage = "(shouted to " + sessions.length + " players)";
		boolean sendShoutedMessage = false;
		for (UserSession session : sessions) {
			if (session.getUser().getUserVars().isShoutOn()) {
				if (session == userSession) {
					session.send(userSession.getUser().getUserName()
							+ " shouts: " + arguments + "\n" + shoutedMessage);
					sendShoutedMessage = true;
				} else {
					session.send(userSession.getUser().getUserName()
							+ " shouts: " + arguments);
				}
			}
		}

		if (!sendShoutedMessage) {
			userSession.send(shoutedMessage);
		}
	}
}
