package morphy.command;

import morphy.user.UserSession;

public interface Command extends Comparable<Command> {
	public CommandContext getContext();

	/**
	 * Returns true if the command will be processed. This can be used to check
	 * for UserLevel,PlayerType, and other things.
	 * 
	 * @param userSession
	 *            The user session.
	 * @return The result.
	 */
	public boolean willProcess(UserSession userSession);

	/**
	 * Processes the command.
	 * 
	 * @param arguments
	 *            The arguments, should be an empty string if there are none.
	 * @param userSession
	 *            The SocketChannelUserSession of the user executing the
	 *            command.
	 */
	public void process(String arguments, UserSession userSession);
}
