package morphy;

import java.io.File;

import morphy.service.ChannelService;
import morphy.service.CommandService;
import morphy.service.PreferenceService;
import morphy.service.Service;
import morphy.service.SocketConnectionService;
import morphy.service.ThreadService;
import morphy.service.UserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class Morphy {
	public static final String RESOURCES_DIR = "resources";
	public static final String COMMAND_FILES_DIR = "resources/commandFiles";
	public static final String SCREEN_FILES = "resources/screenFiles";
	public static final String USER_DIRECTORY = new File(System
			.getProperty("user.home")).getAbsolutePath()
			+ "/" + ".morphy";

	static {
		// Forces log4j to check for changes to its properties file and reload
		// them every 5 seconds.
		// This must always be called before any other code or it will not work.
		PropertyConfigurator.configureAndWatch(RESOURCES_DIR
				+ "/log4j.properties", 5000);
		System.err
				.println("Configured: " + RESOURCES_DIR + "/log4j.properties");
	}

	protected static Log LOG = LogFactory.getLog(Morphy.class);

	protected Service[] services;

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				getInstance().shutdown();
			}
		});

		getInstance().init();
	}

	private static Morphy singletonInstance = new Morphy();

	public static Morphy getInstance() {
		return singletonInstance;
	}

	private boolean isShutdown = false;

	private Morphy() {
	}

	private void init() {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initializing Morphy");
		}
		services = new Service[] { PreferenceService.getInstance(),
				ThreadService.getInstance(), CommandService.getInstance(),
				SocketConnectionService.getInstance(),
				ChannelService.getInstance(), UserService.getInstance() };
	}

	public boolean isShutdown() {
		return isShutdown;
	}

	public void shutdown() {
		if (!isShutdown) {
			LOG.info("Initiating shutdown.");
			isShutdown = true;
			for (int i = 0; i < services.length; i++) {
				try {
					services[i].dispose();
				} catch (Throwable t) {
					LOG.error("Error shutting down service", t);
				}
			}
			LOG.info("Shut down moprhy.");
		}
	}

	public void onError(String message) {
		LOG.error(message);
	}

	public void onError(Throwable t) {
		LOG.error("", t);
	}

	public void onError(String message, Throwable t) {
		LOG.error(message, t);
	}
}
