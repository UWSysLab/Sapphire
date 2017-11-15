package sapphire.appexamples.wordscramblewithfriends.app;

import static sapphire.runtime.Sapphire.new_;
import sapphire.app.SapphireObject;


public class ScrambleManager implements SapphireObject {
	private UserManager  userManager;
	private TableManager tableManager;
	
	public ScrambleManager() {
		userManager  = (UserManager) new_(UserManager.class);
		tableManager = (TableManager) new_(TableManager.class, userManager);
	}
	
	public UserManager getUserManager() {
		return userManager;
	}
	
	public TableManager getTableManager() {
		return tableManager;
	}
}
