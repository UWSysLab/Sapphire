package sapphire.appexamples.wordscramblewithfriends.device.scramble.global;

import sapphire.appexamples.wordscramblewithfriends.app.LocationService;
import sapphire.appexamples.wordscramblewithfriends.app.NotificationService;
import sapphire.appexamples.wordscramblewithfriends.app.ScrambleManager;
import sapphire.appexamples.wordscramblewithfriends.app.TableManager;
import sapphire.appexamples.wordscramblewithfriends.app.User;
import sapphire.appexamples.wordscramblewithfriends.app.UserManager;

public final class StateSingleton {
	private static final StateSingleton instance = new StateSingleton();
	
	private ScrambleManager sm = null;
	private UserManager um = null;
	private TableManager tm = null;
	private NotificationService ns = null;
	private User user = null;
	private String username;
	private boolean hasTable = false;
	private boolean isPlayingGame = false;
	private LocationService ls = null;
	
	public static StateSingleton getInstance() {
		return instance;
	}
	
	public ScrambleManager getScrambleManager() {
		return sm;
	}
	
	public void setScrambleManager(ScrambleManager sm) {
		if (sm == null) {
			throw new IllegalArgumentException("ScrambleManager is null");
		}
		this.sm = sm;
	}
	
	public UserManager getUserManager() {
		return um;
	}
	
	public void setUserManager(UserManager um) {
		if (um == null) {
			throw new IllegalArgumentException("UserManager is null");
		}
		this.um = um;
	}
	
	public TableManager getTableManager() {
		return tm;
	}
	
	public void setTableManager(TableManager tm) {
		if (tm == null) {
			throw new IllegalArgumentException("TableManager is null");
		}
		this.tm = tm;
	}
	
	public NotificationService getNotificationService() {
		return ns;
	}
	
	public void setNotificationService(NotificationService ns) {
		if (ns == null) {
			throw new IllegalArgumentException("NotificationService is null");
		}
		this.ns = ns;
	}
	
	public LocationService getlocationService() {
		return ls;
	}
	
	public void setLocationService(LocationService ls) {
		if (ls == null) {
			throw new IllegalArgumentException("LocationService is null");
		}
		this.ls = ls;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User is null");
		}
		this.user = user;
		this.username = user.getUserInfo().getUsername();
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean hasTable() {
		return hasTable;
	}
	
	public void setHasTable(Boolean hasTable) {
		if (hasTable == null) {
			throw new IllegalArgumentException("hasTable is null");
		}
		this.hasTable = hasTable;
	}
	
	public boolean isPlayingGame() {
		return isPlayingGame;
	}
	
	public void setIsPlayingGame(Boolean isPlayingGame) {
		if (isPlayingGame == null) {
			throw new IllegalArgumentException("isPlayingGame is null");
		}
		this.isPlayingGame = isPlayingGame;
	}
	
//	public Table getTable() {
//		return table;
//	}
//	
//	public void setTable(Table table) {
//		if (table == null) {
//			throw new IllegalArgumentException("Table is null");
//		}
//		this.table = table;
//	}
//	
//	public void resetTable() {
//		table = null;
//	}
//	
//	public Game getGame() {
//		return game;
//	}
//	
//	public void setGame(Game game) {
//		if (game == null) {
//			throw new IllegalArgumentException("Game is null");
//		}
//		this.game = game;
//	}
//	
//	public void resetGame() {
//		game = null;
//	}
}
