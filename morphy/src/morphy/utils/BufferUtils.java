package morphy.utils;

import java.nio.ByteBuffer;

import morphy.Morphy;
import morphy.properties.PreferenceKeys;
import morphy.service.PreferenceService;

public class BufferUtils {
	public static ByteBuffer createBuffer(String message) {
		ByteBuffer buffer = ByteBuffer.allocate(message.length()*4);
		try {
			buffer
					.put(message
							.getBytes(PreferenceService
									.getInstance()
									.getString(
											PreferenceKeys.SocketConnectionServiceCharEncoding)));
		} catch (Throwable t) {
			Morphy.getInstance().onError("Error encoding message", t);
		}
		buffer.flip();
		return buffer;
	}
}
