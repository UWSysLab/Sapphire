package sapphire.appexamples.wordscramblewithfriends.cloud;

import static sapphire.runtime.Sapphire.new_;
import sapphire.app.AppEntryPoint;
import sapphire.app.AppObjectNotCreatedException;
import sapphire.appexamples.wordscramblewithfriends.app.ScrambleManager;
import sapphire.common.AppObjectStub;

public class WordScrambleWithFriendsStart implements AppEntryPoint {

	@Override
	public AppObjectStub start() throws AppObjectNotCreatedException {
		return (AppObjectStub) new_(ScrambleManager.class);
	}
}
