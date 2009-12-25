package morphy.command;

import morphy.channel.Channel;
import morphy.service.ChannelService;
import morphy.service.UserService;
import morphy.user.UserSession;

public class InchannelCommand extends AbstractCommand {

	public InchannelCommand() {
		super("Inchannel");
	}

	public void process(String arguments, UserSession userSession) {
		//int spaceIndex = arguments.indexOf(' ');
		//if (spaceIndex == -1) {
			//userSession.send(getContext().getUsage());
		if (arguments.equals("")) {
			userSession.send("inchannel [no params] has been removed.\nPlease use inchannel [name] or inchannel [number]");
		} else {
			//String userName = arguments.substring(0, spaceIndex);
			final String userName = arguments;
			
			if (userName.matches("[0-9]+")) {
				ChannelService channelService = ChannelService.getInstance();
				int number = Integer.parseInt(userName);
				Channel c = channelService.getChannel(number);
				if (c == null || number < Channel.MINIMUM || number > Channel.MAXIMUM)
					{ userSession.send("Bad channel number."); } 
				else { 
					int numUsers = c.getListeners().size();
					StringBuilder str = new StringBuilder(100);
					str.append("Channel " + c.getNumber() + " \"" + c.getName() + "\": ");
					for(int i=0;i<numUsers;i++) {
						str.append(c.getListeners().get(i).getUser().getUserName());
						if (i != numUsers+1) str.append(" ");
					}
					str.append("\n" + numUsers + " players are in channel " + c.getNumber() + ".");
					userSession.send(str.toString());
				}
			}
			else {
				StringBuilder str = new StringBuilder("50");
				str.append(userName + " is in the following channels:\n");
				UserService uS = UserService.getInstance();
				UserSession sess = uS.getUserSession(userName);
				if (sess == null) { userSession.send(userName + " is not logged in."); }
				final int numUsers = sess.getUser().getOnChannels().size();
				for(int i=0;i<numUsers;i++) {
					str.append(sess.getUser().getOnChannels().get(i));
					if (i != numUsers+1) str.append(" ");
				}
				userSession.send(str.toString());
			}
		}
	}
}
