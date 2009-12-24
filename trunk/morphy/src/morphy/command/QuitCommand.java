package morphy.command;

import morphy.user.UserSession;

public class QuitCommand extends AbstractCommand {
	public QuitCommand() {
		super("quit");
	}

	public void process(String arguments, UserSession userSession) {
		userSession.send("Logging you out.");
		userSession.disconnect();
	}

}