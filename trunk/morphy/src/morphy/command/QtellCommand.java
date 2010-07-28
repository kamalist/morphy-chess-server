package morphy.command;

import morphy.service.ServerListManagerService;
import morphy.service.UserService;
import morphy.user.UserLevel;
import morphy.user.UserSession;
import morphy.utils.john.ServerList;

public class QtellCommand extends AbstractCommand {

	public QtellCommand() {
		super("qtell");
	}

	public void process(String arguments, UserSession userSession) {
		ServerListManagerService service = ServerListManagerService.getInstance();
		
		int whiteSpacePos = arguments.indexOf(" ");
		if (whiteSpacePos == -1) {
			userSession.send(getContext().getUsage());
			return;
		}
		
		if (!service.isOnAnyList(new ServerList[] { service.getList("TD"),service.getList("admin") },
				userSession.getUser().getUserName())) {
					userSession.send("Only TD programs and admins are allowed to use this command.");
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
				+ ((sendTo.isConnected()) ? "0" : "1"));
	}

}
