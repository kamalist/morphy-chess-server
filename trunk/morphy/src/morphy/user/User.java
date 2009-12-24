package morphy.user;

public class User {
	protected String userName;
	protected UserLevel userLevel;
	protected PlayerType playerType;
	protected PlayerTitle[] titles = new PlayerTitle[0];
	protected UserVars userVars = new UserVars();

	public User() {

	}

	public UserVars getUserVars() {
		return userVars;
	}

	public void setUserVars(UserVars userVars) {
		this.userVars = userVars;
	}

	public UserLevel getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(UserLevel userLevel) {
		this.userLevel = userLevel;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}

	public PlayerTitle[] getTitles() {
		return titles;
	}

	public void setTitles(PlayerTitle[] titles) {
		this.titles = titles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
