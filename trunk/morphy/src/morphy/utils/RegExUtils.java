package morphy.utils;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegExUtils {
	private static final Log LOG = LogFactory.getLog(RegExUtils.class);

	public static Pattern getPattern(String regularExpression) {
		return Pattern.compile(regularExpression, Pattern.MULTILINE
				| Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	public static boolean matches(Pattern pattern, String stringToTest) {
		try {
			return pattern.matcher(stringToTest).matches();
		} catch (Throwable t) {
			LOG.warn("matches threw exception. regex=" + pattern.pattern()
					+ " test=" + stringToTest, t);
			return false;
		}
	}

	public static boolean matches(String regularExpression, String stringToTest) {
		try {
			return getPattern(regularExpression).matcher(stringToTest)
					.matches();
		} catch (Throwable t) {
			LOG.warn("matches threw exception. regex=" + regularExpression
					+ " test=" + stringToTest, t);
			return false;
		}
	}
}
