package sapphire.appexamples.wordscramblewithfriends.device.scramble.util;

import java.io.Serializable;

import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.ScrambleConstants;

public final class RoundStats implements Serializable {
	private final ScrambleConstants.Events event;
	private final String wordPlayed;
	private final int roundPoints;
	private final int totalPoints;
	private final int round;
	
	// Requires event to be non-null
	public RoundStats(ScrambleConstants.Events event, String wordPlayed, int roundPoints, int totalPoints, int round) {
		this.event = event;
		this.wordPlayed = wordPlayed;
		this.roundPoints = roundPoints;
		this.totalPoints = totalPoints;
		this.round = round;
	}
	
	public ScrambleConstants.Events getEvent() {
		return event;
	}
	
	public String getWordPlayed() {
		return wordPlayed;
	}
	
	public int getRoundPoints() {
		return roundPoints;
	}
	
	public int getTotalPoints() {
		return totalPoints;
	}
	
	public int getRound() {
		return round;
	}
}
