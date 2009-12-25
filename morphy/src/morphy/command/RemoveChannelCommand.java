package morphy.command;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.user.UserSession;

public class RemoveChannelCommand extends AbstractCommand {

	public RemoveChannelCommand() {
		super("RemoveChannel");
	}

	public void process(String arguments, UserSession userSession) {
		String[] args = arguments.split(" ");
		try {
			int chNum = Integer.parseInt(args[args.length-1]);
			ChannelService cS = ChannelService.getInstance();
			Channel c = cS.getChannel(chNum);
			if (c != null) {
				if (!userSession.getUser().getOnChannels().contains(new Integer(chNum))) {
					userSession.send("[" + chNum + "] is not in your channel list."); 
					return;
				}
				c.removeListener(userSession);
				userSession.getUser().getOnChannels().remove(new Integer(chNum));
				userSession.send("[" + c.getNumber() + "] removed from your channel list.");
			}
		} catch (NumberFormatException e) {
			userSession.send("The channel to remove must be a number between " + Channel.MINIMUM + " and " + Channel.MAXIMUM + ".");
		}
	}

}
