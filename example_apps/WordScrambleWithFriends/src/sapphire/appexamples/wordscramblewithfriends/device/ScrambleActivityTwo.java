package sapphire.appexamples.wordscramblewithfriends.device;

import static sapphire.runtime.Sapphire.new_;
import sapphire.app.SapphireActivity;
import sapphire.app.SapphireObject;
import sapphire.appexamples.wordscramblewithfriends.app.NotificationService;
import sapphire.appexamples.wordscramblewithfriends.app.ScrambleManager;
import sapphire.appexamples.wordscramblewithfriends.app.TableManager;
import sapphire.appexamples.wordscramblewithfriends.app.User;
import sapphire.appexamples.wordscramblewithfriends.app.UserManager;
import android.app.Activity;

public class ScrambleActivityTwo extends Activity implements SapphireActivity {
	public void onCreate(SapphireObject appEntryPoint) {
		ScrambleManager sm = (ScrambleManager) appEntryPoint;
		UserManager um = sm.getUserManager();
		TableManager tm = sm.getTableManager();
		try {
//			User dana = um.addUser("Dana", "Dana");
//			NotificationService ns = (NotificationService) new_(NotificationService.class);
//			dana.setNotificationService(ns);
//			User kie = um.addUser("Kie", "Kie");
//			tm.hostTable("Dana");
//			
//			while (ns.noMessages()) {
//				Thread.sleep(5000);
//			}
//			
//			System.out.println("Dana got the message!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
