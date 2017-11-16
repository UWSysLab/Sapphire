package sapphire.appexamples.wordscramblewithfriends.device.scramble.global;

public class ScrambleConstants {

	///////////////// Game Events ///////////////////////
	public static final int OPPONENT_NOTIFICATION_REF = 3;
	public static final int GAME_READY = 4;
	public static final int EXIT = 5;
	
	///////////////// Player Events /////////////////////
	public enum Events {
		PLAY, PASS, NEW_ROUND, GAME_OVER, NOT_PLAYER_TURN, QUIT;
	}
}
