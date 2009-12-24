package morphy.service;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import morphy.Morphy;
import morphy.properties.PreferenceKeys;
import morphy.service.ScreenService.Screen;
import morphy.user.PlayerTitle;
import morphy.user.PlayerType;
import morphy.user.SocketChannelUserSession;
import morphy.user.User;
import morphy.user.UserLevel;
import morphy.utils.BufferUtils;
import morphy.utils.MorphyStringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketConnectionService implements Service {
	protected static Log LOG = LogFactory.getLog(SocketConnectionService.class);
	private static final SocketConnectionService singletonInstance = new SocketConnectionService();
	protected ServerSocketChannel serverSocketChannel;
	protected Selector serverSocketSelector;
	protected int maxCommunicationSizeBytes;
	protected Thread selectionThread = null;
	protected Map<Socket, SocketChannelUserSession> socketToSession = new HashMap<Socket, SocketChannelUserSession>();

	public void removeUserSession(SocketChannelUserSession session) {
		disposeSocketChannel(session.getChannel());
	}

	protected void disposeSocketChannel(SocketChannel channel) {
		if (channel.isConnected()) {
			try {
				channel.close();
			} catch (Throwable t) {
			}
		}
		socketToSession.remove(channel.socket());
	}

	protected Runnable selectSocketRunnable = new Runnable() {
		public void run() {
			try {
				while (true) {
					if (Morphy.getInstance().isShutdown()) {
						return;
					}
					serverSocketSelector.select();
					Set<SelectionKey> keys = serverSocketSelector
							.selectedKeys();

					if (LOG.isInfoEnabled()) {
						LOG.info("Selected " + keys.size() + " keys.");
					}

					Iterator<SelectionKey> i = keys.iterator();

					while (i.hasNext()) {
						SelectionKey key = i.next();
						i.remove();

						if (key.isAcceptable()) {
							final SocketChannel channel = serverSocketChannel
									.accept();
							channel.configureBlocking(false);
							channel.register(serverSocketSelector,
									SelectionKey.OP_READ);

							ThreadService.getInstance().run(new Runnable() {
								public void run() {
									onNewChannel(channel);
								}
							});
						}
						if (key.isReadable()) {
							final SocketChannel channel = (SocketChannel) key
									.channel();
							ThreadService.getInstance().run(new Runnable() {
								public void run() {
									onNewInput(channel);
								}
							});
						}
					}
				}
			} catch (Throwable t) {
				Morphy.getInstance().onError(
						"Error reading selector in SocketConnectionService", t);
			}
		}
	};

	private void onNewInput(SocketChannel channel) {
		try {
			if (channel.isConnected()) {
				SocketChannelUserSession session = socketToSession.get(channel
						.socket());

				if (session == null) {
					Morphy
							.getInstance()
							.onError(
									"Received a read on a socket not being managed. This is likely a bug.");
					disposeSocketChannel(channel);
				} else {
					synchronized (session.getInputBuffer()) {
						String message = readMessage(channel);
						if (message == null) {
							session.disconnect();
						} else if (message.length() > 0) {
							if (LOG.isInfoEnabled()) {
								LOG.info("Read:  "
										+ session.getUser().getUserName() + " "
										+ message);
							}
							session.getInputBuffer().append(message);

							int carrageReturnIndex = -1;
							while ((carrageReturnIndex = session
									.getInputBuffer().indexOf("\n")) != -1) {
								String command = session.getInputBuffer()
										.substring(0, carrageReturnIndex)
										.trim();
								session.getInputBuffer().delete(0,
										carrageReturnIndex + 1);

								if (!session.hasLoggedIn()) {
									handleLoginPromptText(session, command);
								} else {
									CommandService.getInstance()
											.processCommand(command, session);
								}
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error reading socket channel or processing command ", t);
		}
	}

	protected String readMessage(SocketChannel channel) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(maxCommunicationSizeBytes);
			int charsRead = channel.read(buffer);
			if (charsRead == -1) {
				return null;
			} else if (charsRead > 0) {
				buffer.flip();
				Charset charset = Charset
						.forName(PreferenceService
								.getInstance()
								.getString(
										PreferenceKeys.SocketConnectionServiceCharEncoding));
				CharsetDecoder decoder = charset.newDecoder();
				CharBuffer charBuffer = decoder.decode(buffer);
				return charBuffer.toString();
			} else {
				return "";
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error reading SocketChannel "
							+ channel.socket().getLocalAddress(), t);
			return null;
		}
	}

	protected void handleLoginPromptText(SocketChannelUserSession userSession,
			String message) {
		if (message.trim().matches("\\w{3,15}")) {
			String name = message;
			if (UserService.getInstance().isLoggedIn(name)) {
				sendWithoutPrompt("User " + name
						+ " matches someone already logged in. Good bye.",
						userSession);
				userSession.disconnect();
			} else {
				userSession.getUser().setUserName(name);
				userSession.getUser().setPlayerType(PlayerType.Human);
				userSession.getUser().setTitles(new PlayerTitle[0]);
				userSession.getUser().setUserLevel(UserLevel.Player);
				UserService.getInstance().addLoggedInUser(userSession);
				userSession.setHasLoggedIn(true);
				StringBuilder loginMessage = new StringBuilder(1000);
				loginMessage.append(MorphyStringUtils
						.replaceNewlines("**** Starting FICS session as "
								+ name + " ****\n"));
				loginMessage.append(ScreenService.getInstance().getScreen(
						Screen.SuccessfulLogin));
				userSession.send(loginMessage.toString());
				UserService.getInstance().sendAnnouncement(
						name + " has logged in.");
			}
		} else {
			sendWithoutPrompt("Invalid user name: " + message + " Good Bye",
					userSession);
			userSession.disconnect();
		}
	}

	protected void sendWithoutPrompt(String message,
			SocketChannelUserSession session) {
		try {
			if (session.isConnected()) {
				ByteBuffer buffer = BufferUtils.createBuffer(MorphyStringUtils
						.replaceNewlines(message));
				session.getChannel().write(buffer);
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Tried to send message to a logged off user "
							+ session.getUser().getUserName() + " " + message);
				}
				session.disconnect();
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error sending message to user "
							+ session.getUser().getUserName() + " " + message,
					t);
			session.disconnect();
		}
	}

	private void onNewChannel(SocketChannel channel) {
		try {
			SocketChannelUserSession session = new SocketChannelUserSession(
					new User(), channel);
			socketToSession.put(channel.socket(), session);

			ByteBuffer buffer = BufferUtils.createBuffer(ScreenService
					.getInstance().getScreen(Screen.Login));
			channel.write(buffer);

			if (LOG.isInfoEnabled()) {
				LOG.info("Received socket connection "
						+ channel.socket().getInetAddress());
			}
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error writing to SocketChannel "
							+ channel.socket().getInetAddress(), t);
			disposeSocketChannel(channel);
		}
	}

	private SocketConnectionService() {
		try {
			maxCommunicationSizeBytes = PreferenceService
					.getInstance()
					.getInt(
							PreferenceKeys.SocketConnectionServiceMaxCommunicationBytes);
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel
					.socket()
					.bind(
							new java.net.InetSocketAddress(
									PreferenceService
											.getInstance()
											.getInt(
													PreferenceKeys.SocketConnectionServicePorts)));
			serverSocketSelector = Selector.open();
			serverSocketChannel.register(serverSocketSelector,
					SelectionKey.OP_ACCEPT);

			selectionThread = new Thread(selectSocketRunnable);
			selectionThread.setPriority(Thread.MAX_PRIORITY);
			selectionThread.start();

			LOG.info("Initialized Socket Connection Service host:"
					+ serverSocketChannel.socket().getInetAddress() + " "
					+ serverSocketChannel.socket().getLocalPort());
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error initializing SocketConnectionService", t);
		}
	}

	public static SocketConnectionService getInstance() {
		return singletonInstance;
	}

	public void dispose() {
		try {
			serverSocketChannel.close();
		} catch (Throwable t) {
			Morphy.getInstance().onError(
					"Error disposing SocketConnectionService", t);
		}
	}
}
