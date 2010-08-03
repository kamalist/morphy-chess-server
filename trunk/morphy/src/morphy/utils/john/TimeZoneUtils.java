package morphy.utils.john;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TimeZoneUtils {
	private static Map<String, TimeZone> map;

	static {
		TimeZoneUtils.map = load();
	}

	public static String getAbbreviation(TimeZone tz) {
		return getAbbreviation(tz, new Date());
	}

	public static String getAbbreviation(TimeZone tz, Date d) {
		return tz.getDisplayName(tz.inDaylightTime(d), TimeZone.SHORT)
				.toUpperCase();
	}

	public static TimeZone getTimeZone(String abbrev) {
		Map<String, TimeZone> v = TimeZoneUtils.map;
		TimeZone tz = v.get(abbrev);
		return tz;
	}

	private static Map<String, TimeZone> load() {
		final Map<String, TimeZone> map = new HashMap<String, TimeZone>();
		final String[] arr = TimeZone.getAvailableIDs();
		final Date d = new Date();

		for (final String tmp : arr) {
			final TimeZone tz = TimeZone.getTimeZone(tmp);
			final String abbrev = tz.getDisplayName(tz.inDaylightTime(d),
					TimeZone.SHORT);

			if (map.containsKey(abbrev)) {
				continue;
			}

			map.put(abbrev, tz);
		}

		return map;
	}
}
