/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008,2009  http://code.google.com/p/morphy-chess-server/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package morphy.channel;

import java.util.Arrays;

import morphy.user.PlayerTitle;
import morphy.user.User;
import morphy.user.UserLevel;

public class Channel implements Comparable<Channel> {
	protected int number;
	protected String name;
	protected String description;
	protected UserLevel level;
	protected PlayerTitle[] titles;

	public Channel(int number, String name, String description,
			UserLevel level, PlayerTitle[] titles) {
		this.number = number;
		this.name = name;
		this.description = description;
		this.level = level;
		this.titles = titles;
		Arrays.sort(titles);
	}

	public boolean hasAccess(User user) {
		boolean result = false;
		if (level == null && titles == null) {
			result = true;
		} else {
			if (level != null) {
				result = user.getUserLevel().ordinal() >= level.ordinal();
			}
			if (!result && titles != null && user.getTitles() != null
					&& user.getTitles().length > 0) {
				for (PlayerTitle title : user.getTitles()) {
					if (Arrays.binarySearch(titles, title) != -1) {
						result = true;
						break;
					}
				}

			}
		}
		return result;
	}

	public int compareTo(Channel channel) {
		return new Integer(number).compareTo(new Integer(channel.number));
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
