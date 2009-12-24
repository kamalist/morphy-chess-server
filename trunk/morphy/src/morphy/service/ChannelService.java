package morphy.service;

public class ChannelService implements Service{
	private static final ChannelService singletonInstance = new ChannelService();

	private ChannelService() {

	}

	public static ChannelService getInstance() {
		return singletonInstance;
	}

	public void dispose() {

	}
}
