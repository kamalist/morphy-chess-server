package morphy.command;

import morphy.user.UserSession;

public abstract class AbstractCommand implements Command {
	protected CommandContext context;

	public AbstractCommand(String helpFileName) {
		context = new CommandContext(helpFileName);
	}

	public int compareTo(Command o) {
		return context.getName().compareTo(o.getContext().getName());
	}

	public CommandContext getContext() {
		return context;
	}

	public void setContext(CommandContext context) {
		this.context = context;
	}

	/**
	 * Performs a user level check on the command.
	 */
	public boolean willProcess(UserSession userSession) {
		return userSession.getUser().getUserLevel().ordinal() >= context
				.getUserLevel().ordinal();
	}
}
