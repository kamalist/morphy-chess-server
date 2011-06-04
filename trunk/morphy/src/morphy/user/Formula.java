/*
 *   Morphy Open Source Chess Server
 *   Copyright (C) 2008-2010  http://code.google.com/p/morphy-chess-server/
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
package morphy.user;

import morphy.game.MatchParams;

public class Formula {
	protected String[] validKeywords = { "abuser", "assessdraw", "assessloss",
			"assesswin", "black", "blitz", "bughouse", "computer",
			"crazyhouse", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9",
			"inc", "lightning", "losers", "maxtime", "mymaxtime", "myrating",
			"nocolor", "nonstandard", "private", "ratingdiff", "rating",
			"rated", "registered", "suicide", "standard", "time", "timeseal",
			"unrated", "untimed", "white", "wild" };
	
	/*public static boolean isValidFormula(String formula) {
		// if any word outside of formula is not in validKeywords, (except comment) it is a bad formula.
		// if two variants are &&'d together (blitz && bughouse) it is invalid.
		// comment symbol is #.
		// also look for stupid things like rated && !rated.
		// things like !> is not valid. <= should be used instead.
		// if formula starts with #, or always will evaluate to true (e.g. '1') allow anything.
		// if formula is something that will always evaluate to false (e.g. rating>9999) disallow everything.
		
		
		return false;
	}*/
	
	public boolean matches(MatchParams params) {
		return false;
	}

	public String formula;

	public Formula() {

	}

	public Formula(String formula) {
		this.formula = formula;
	}
}
