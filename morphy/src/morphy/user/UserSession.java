package morphy.user;

public interface UserSession {
	public long getLoginTime();

	public long getIdleTimeMillis();

	public User getUser();

	public void send(String message);

	public void disconnect();

	public boolean isConnected();

	public Boolean getBoolean(UserSessionKey key);

	public Integer getInt(UserSessionKey key);

	public String getString(UserSessionKey key);

	public Object get(UserSessionKey key);

	public void put(UserSessionKey key, Object object);
}
