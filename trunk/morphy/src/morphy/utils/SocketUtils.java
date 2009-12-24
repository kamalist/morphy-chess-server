package morphy.utils;

import java.net.Socket;

public class SocketUtils {

	public static String getIpAddress(Socket socket) {
		// In the future this will produce a better ip address string.
		return socket.getInetAddress().toString();
	}

}
