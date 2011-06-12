package morphy.game;

import board.Board;

public interface GameInterface {
	public Board getBoard();
	public int getGameNumber();
	public int getTime();
	public int getIncrement();
	public int getWhiteBoardStrength();
	public int getBlackBoardStrength();
	public int getWhiteClock();
	public int getBlackClock();
	public boolean isRated();
	public long getTimeGameStarted();
	public Variant getVariant();
	
	public void setGameNumber(int num);
	public void setTime(int time);
	public void setIncrement(int increment);
	public void setRated(boolean rated);
}
