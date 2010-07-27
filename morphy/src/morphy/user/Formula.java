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

public class Formula {
	protected String[] validKeywords = { "bughouse", "rating", "time",
			"increment", "myrating", "crazyhouse", "blitz", "lightning",
			"standard", "timeseal", "registered", "abuser" };
	
	
	public static boolean isValidFormula(String formula) {
		// if any word outside of formula is not in validKeywords, (except comment) it is a bad formula.
		// if two variants are &&'d together (blitz && bughouse) it is invalid.
		// comment symbol is #.
		
		return false;
	}

	public String formula;

	public Formula() {

	}

	public Formula(String formula) {
		this.formula = formula;
	}

	/**
	 * Parameters to be determined.
	 */
	public boolean matches() {
		return true;
	}
}
