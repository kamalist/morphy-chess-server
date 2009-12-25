package morphy.command;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.user.UserSession;

public class AddChannelCommand extends AbstractCommand {

	public AddChannelCommand() {
		super("AddChannel");
	}

	public void process(String arguments, UserSession userSession) {
		String[] args = arguments.split(" ");
		try {
			int chNum = Integer.parseInt(args[args.length-1]);
			ChannelService cS = ChannelService.getInstance();
			Channel c = cS.getChannel(chNum);
			if (c != null) {
				if (userSession.getUser().getOnChannels().contains(new Integer(chNum))) { 
					userSession.send("[" + chNum + "] is already on your channel list."); 
					return;
				}
				userSession.getUser().getOnChannels().add(new Integer(chNum));
				c.addListener(userSession);
				userSession.send("[" + c.getNumber() + "] added to your channel list.");
			}
		} catch (NumberFormatException e) {
			userSession.send("The channel to add must be a number between " + Channel.MINIMUM + " and " + Channel.MAXIMUM + ".");
		}
	}

}
