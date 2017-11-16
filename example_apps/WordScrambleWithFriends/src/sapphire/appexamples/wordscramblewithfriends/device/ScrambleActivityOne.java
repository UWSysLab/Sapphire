package sapphire.appexamples.wordscramblewithfriends.device;

import static sapphire.runtime.Sapphire.new_;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sapphire.app.SapphireActivity;
import sapphire.app.SapphireObject;
import sapphire.appexamples.wordscramblewithfriends.app.Game;
import sapphire.appexamples.wordscramblewithfriends.app.ScrambleManager;
import sapphire.appexamples.wordscramblewithfriends.app.TableManager;
import sapphire.appexamples.wordscramblewithfriends.app.User;
import sapphire.appexamples.wordscramblewithfriends.app.UserManager;

public class ScrambleActivityOne extends Thread implements SapphireActivity {
	private static final int NUM_LETTERS = 8;
	private static final Lock lock = new ReentrantLock();
	private static final Condition waitingOnPlayer = lock.newCondition();
	private static final Random gen = new Random();
	private static final String[] events = {"PLAY", "PLAY", "PLAY", "PLAY", "PASS", "PASS", "PASS", "PASS", "PLAY"};
	
	private static TableManager tm;
	// private static GameManager gm;
	private static UserManager um;
	private static boolean gameFinalized = false;
	private static boolean endGame = false;
	
	
//	public void run() {
//		if (Thread.currentThread().getName().equals("me")) {
//			me();
//		} else {
//			peer();
//		}
//	}
//	
//	private void me() {
//		try {
//			/* This user will host a table */
//			User player = um.addUser("Erika", "Erika");
//
//			Table t = hostTable(player);
//			System.out.println("Successfully hosting table!");
//			/* Initialize the game once peer has joined */
//			Game g = initializeGame(t, player);
//			if (g == null) {
//				return;
//			}
//			System.out.println("Player 1 initialized the game!");
//			
//			/* Wait for player 2 to get a copy of the game object */
//			lock.lock();
//			try {
//				while (!gameFinalized)
//					waitingOnPlayer.await();
//			} finally {
//				lock.unlock();
//			}
//			
//			while (play(g, 1) >= 0);
//			endGame = true;
//			System.out.println("Player 1 is finished playing the game!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void peer() {
//		try {
//			/* The peer will join the table */
//			User player = um.addUser("Kie", "Kie");
//			if (!joinTable(player))
//				return;
//			System.out.println("Successfully joined a table!");
//			/* Wait for me to initialize the game */
//			Game g = finalizeGame(player);
//			if (g == null) {
//				return;
//			}
//			System.out.println("Player 2 finalized the game!");
//			while (play(g, 2) >= 0);
//			endGame = true;
//			System.out.println("Player 2 is finished playing the game!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private int play(Game g, int playerNum) throws InterruptedException {
//		int score = -1;
//		int nextMoveIndex = gen.nextInt(events.length);
//		lock.lock();
//		try {
//			while (!endGame && g.getPlayerTurn() != playerNum)
//				waitingOnPlayer.await();
//			
//			/* Check if other player ended the game last turn */
//			if (endGame) {
//				System.out.println("Player " + playerNum + " has closed the game");
//				um.getUser(g.getFirstPlayerName()).reset();
//				um.getUser(g.getSecondPlayerName()).reset();
//				score = -1;
//			} else if (events[nextMoveIndex].equals("END")) {
//				System.out.println("Player " + playerNum + " has chosen to end the game");
//				endGame = true;
//				score = -1;
//			} else if (events[nextMoveIndex].equals("PLAY")) {
//				String scrambleLetters = g.getScrambleLetters();
//				
//				/* Simulate game play */
//				int guess = gen.nextInt(NUM_LETTERS);
//				String word = scrambleLetters.substring(0, guess);
//				score = g.play(playerNum, word);
//			} else {	// PASS
//				score = g.pass(playerNum);
//			}
//		} finally {
//			waitingOnPlayer.signal();
//			lock.unlock();
//		}
//		return score;
//	}
//	
//	private Game finalizeGame(User peer) throws InterruptedException {
//		Game g = null;
//		lock.lock();
//		try {
//			while (!peer.isPlayingGame())
//				waitingOnPlayer.await();
//			g = peer.getGame();
//			if (g == null)
//				System.out.println("Error finalizing game");
//			gameFinalized = true;
//		} finally {
//			waitingOnPlayer.signal();
//			lock.unlock();
//		}
//		return g;
//	}
//	
//	private Game initializeGame(Table t, User me) throws InterruptedException {
//		Game g = null;
//		lock.lock();
//		try {
//			while (!t.isFull())
//				waitingOnPlayer.await();
//			String peerName = t.getGuestName();
//			User peer = um.getUser(peerName);
//			g = (Game) new_(Game.class, t);
//			if (g == null || !me.setGame(g) || !peer.setGame(g)) {
//				System.out.println("Error initializing game");
//				return null;
//			}
//			System.out.println("Game" + g.getGameId() + " initialized between @" +
//					me.getUserInfo().getUsername() + " and @" +
//					peer.getUserInfo().getUsername());
//		} finally {
//			waitingOnPlayer.signal();
//			lock.unlock();
//		}
//		return g;
//	}
//
//	private boolean joinTable(User peer) throws InterruptedException {
//		lock.lock();
//		try {
//			while (!tm.tableAvailable())
//				waitingOnPlayer.await();
//			String peerName = peer.getUserInfo().getUsername();
//			if (tm.joinTable(peerName) == null) {
//				System.out.println("Error joining table");
//				lock.unlock();
//				return false;
//			}
//		} finally {
//			waitingOnPlayer.signal();
//			lock.unlock();
//		}
//		return true;
//	}
//	
//	private Table hostTable(User me) {
//		lock.lock();
//		Table t = null;
//		try {
//			String myName = me.getUserInfo().getUsername();
//			t = tm.hostTable(myName);
//			if (t == null) {
//				System.out.println("Error hosting table");
//				lock.unlock();
//				return null;
//			}
//		} finally {
//			waitingOnPlayer.signal();
//			lock.unlock();
//		}
//		return t;
//	}

	public void onCreate(SapphireObject appEntryPoint) {
		
		try {
            /* Get Scramble and User Manager */
			ScrambleManager sm = (ScrambleManager) appEntryPoint;
			//Thread me = new Thread(new ScrambleActivityOne());
//          Thread peer = new Thread(new ScrambleActivityOne());
//          me.setName("me");
//          peer.setName("peer");
//          me.start();
//          peer.start();
//          
//          me.join();
//          peer.join();
            um  = sm.getUserManager();
            tm = sm.getTableManager();
            //InputStream is = new FileInputStream(new File("/bigraid/users/danava04/words.txt"));
            //Game g = (Game) new_(Game.class, "a", "b", 12, is);
            //Context context = new MockContext();
           // LocationService ls = new LocationService(context);
//            
            User dana = um.addUser("Dana", "Dana");
			//NotificationService ns = (NotificationService) new_(NotificationService.class);
			//System.out.println("Dana's ns is null?" + (ns == null));
			//dana.setNotificationService(ns);
			User kie = um.addUser("Kie", "Kie");
//			Table t = tm.hostTable("Dana");
//			
//			while (ns.noMessages()) {
//				Thread.sleep(5000);
//			}
//			System.out.println("Dana got the message!");
//			
//			// this is Kie's notification manager
//			Message firstMsg = ns.pollMessage();
//			if (firstMsg.getMessage() != ScrambleConstants.OPPONENT_NOTIFICATION_REF) {
//				System.out.println("oh no!");
//			} else {
//				System.out.println("oh ok!");
//				NotificationService nsOpp = (NotificationService) firstMsg.getMessageObject();
//				System.out.println("nsOpp is null?" + (nsOpp == null));
//				Game g = (Game) new_(Game.class, t.getGuestName(), t.getHostName(), t.getTableId());
//				Message sendGameObj = new Message(ScrambleConstants.GAME_READY, g);
//				nsOpp.notify(sendGameObj);
//			}
			
            System.out.println("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
