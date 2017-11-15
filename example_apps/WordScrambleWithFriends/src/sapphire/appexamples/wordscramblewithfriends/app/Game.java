package sapphire.appexamples.wordscramblewithfriends.app;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import sapphire.app.SapphireObject;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.ScrambleConstants;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.util.RoundStats;

@SuppressWarnings("serial")
public class Game implements SapphireObject {
	private static final int MAX_ROUNDS = 10;
	private static final Random GEN = new Random();
	private static final int FIRST_PLAYER = 1;
	private static final int SECOND_PLAYER = 2;
	
	private Map<String, List<String>> wordMap;

	/* Player info */
	private final String player1Name;
	private final String player2Name;
	private int player1Score;
	private int player2Score;

	/* Game info */
	private final int gameId;
	private int playerTurn;
	private int currentRound;
	private ScrambleConstants.Events lastPlay;
	private Round round;
	private RoundStats lastRoundStats;
	
	public Game(String player1Name, String player2Name, Integer gameId) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.player1Score = 0;
		this.player2Score = 0;
		this.gameId = gameId;
		this.playerTurn = Game.FIRST_PLAYER;
		this.currentRound = 1;
		this.lastPlay = null;
		this.round = new Round();
		this.lastRoundStats = null;
	}
	
	public void setDictionary(HashMap<String, List<String>> wordMap) {
		if (wordMap == null) {
			throw new IllegalArgumentException("Dictionary cannot be null");
		}
		this.wordMap = wordMap;
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public int getRound() {
		return currentRound;
	}
	
	public int getPlayerTurn() {
		return playerTurn;
	}
	
	public String getFirstPlayerName() {
		return player1Name;
	}
	
	public String getSecondPlayerName() {
		return player2Name;
	}
	
	public RoundStats getLastRoundStats() {
		return lastRoundStats;
	}
	
	public RoundStats quitGame(int playerNum) {
		lastPlay = ScrambleConstants.Events.QUIT;
		int nextPlayerTurn = (playerNum == Game.FIRST_PLAYER) ? playerTurn + 1 : playerTurn - 1;
		playerTurn = nextPlayerTurn;
		lastRoundStats = new RoundStats(lastPlay, null, 0, 0, currentRound);
		return lastRoundStats;
	}
	
	private RoundStats gameOver(int nextPlayerTurn) {
		System.out.println("Game over");
		playerTurn = nextPlayerTurn;
		lastRoundStats = new RoundStats(ScrambleConstants.Events.GAME_OVER, null, 0, 0, currentRound);
		return lastRoundStats;
	}
	
	private RoundStats notPlayerTurn() {
		lastRoundStats = new RoundStats(ScrambleConstants.Events.NOT_PLAYER_TURN, null, 0, 0, currentRound);
		return lastRoundStats;
	}
	
	public RoundStats play(Integer playerNum, String word) {
		int nextPlayerTurn = (playerNum == Game.FIRST_PLAYER) ? playerTurn + 1 : playerTurn - 1;

		// End of game
		if (currentRound > MAX_ROUNDS) {
			return gameOver(nextPlayerTurn);
		}
		// Error if we receive this message
		if (playerNum != playerTurn) {
			return notPlayerTurn();
		}
		lastPlay = ScrambleConstants.Events.PLAY;
		int points = round.checkWord(word);
		int score = 0;
		if (playerNum == Game.FIRST_PLAYER) {
			player1Score += points;
			score = player1Score;
		} else {
			player2Score += points;
			score = player2Score;
		}
		System.out.println("Player " + playerNum + " played! Their score is now " + score);
		playerTurn = nextPlayerTurn;
		lastRoundStats = new RoundStats(ScrambleConstants.Events.PLAY, word, points, score, currentRound);
		return lastRoundStats;
	}
	
	public RoundStats pass(Integer playerNum) {
		int nextPlayerTurn = (playerNum == Game.FIRST_PLAYER) ? playerTurn + 1 : playerTurn - 1;
		if (currentRound > MAX_ROUNDS) {
			return gameOver(nextPlayerTurn); 
		} else if (playerNum != playerTurn) {
			return notPlayerTurn();
		} else {
			ScrambleConstants.Events event = null;
			if (lastPlay != null && lastPlay.equals(ScrambleConstants.Events.PASS)) {
				if (++currentRound > MAX_ROUNDS) {
					return gameOver(nextPlayerTurn);
				}
				round = new Round();
				event = ScrambleConstants.Events.NEW_ROUND;
				lastPlay = null;
			} else {
				event = ScrambleConstants.Events.PASS;
				lastPlay = ScrambleConstants.Events.PASS;
			}
			System.out.println("Player " + playerNum + " passed");
			int score = (playerNum == Game.FIRST_PLAYER) ? player1Score : player2Score;
			playerTurn = nextPlayerTurn;
			lastRoundStats = new RoundStats(event, null, 0, score, currentRound);
			return lastRoundStats;
		}
	}
	
	public String getScrambleLetters() {
		return round.scrambleWord;
	}

	public class Round implements Serializable {
		private static final int NUM_LETTERS = 8;
		private static final int ALPHA_LEN = 26;
		private static final int MIN_WORD_LEN = 3;
		private static final int DIST_LEN = 58;
		private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
		private static final String LETTER_DISTRIBUTION="aaabccdddeeeffggghhiiijklllmmnnnoooppqrrrssstttuuuvvwwxyyz";
		
		private final Set<String> playedWords = new HashSet<String>();
		private final int[] scrambleLetters = new int[ALPHA_LEN];
		private String scrambleWord;
		
		public Round() {
			scrambleWord = "";
			// Generate a random char sequence of size numLetters
			for (int i = 0; i < NUM_LETTERS; ++i) {
				int nextCharIdx = GEN.nextInt(DIST_LEN);
				char nextChar = LETTER_DISTRIBUTION.charAt(nextCharIdx);
				int charIdx = ALPHABET.indexOf(nextChar);
				scrambleLetters[charIdx]++;
				scrambleWord += nextChar;
			}
			System.out.println("scrambleWord = " + scrambleWord);
			System.out.println("scrambleLetters = " + arrayToString(scrambleLetters));
		}
		
		public int checkWord(String word) {
			System.out.println("word = " + word);
			int wordLen = word.length();
			// Check for valid word length
			if (wordLen < MIN_WORD_LEN || wordLen > NUM_LETTERS)
				return 0;
			
			// Check that the letters are valid
			int[] scrambleCheck = new int[ALPHA_LEN];
			System.arraycopy(scrambleLetters, 0, scrambleCheck, 0, ALPHA_LEN);
			for (int i = 0; i < wordLen; ++i) {
				if (--scrambleCheck[ALPHABET.indexOf(word.charAt(i))] < 0) {
					return 0;
				}
			}
			char[] sortedWordArr = word.toCharArray();
			Arrays.sort(sortedWordArr);
			String sortedWord = String.copyValueOf(sortedWordArr);
			System.out.println("sorted word = " + sortedWord);
			List<String> validWords = wordMap.get(sortedWord);
			if (validWords == null)
				return 0;
			for (String validWord : validWords) {
				if (word.equals(validWord) && !playedWords.contains(word)) {
					playedWords.add(word);
					return wordLen;
				}
			}
			return 0;
		}
	}

	private static String arrayToString(int[] arr) {
		String s = "{";
		int arrLen = arr.length;
		for (int i = 0; i < arrLen - 1; ++i) {
			s += arr[i] + ", ";
		}
		s += arr[arrLen - 1] + "}";
		return s;
	}
}