package sapphire.appexamples.wordscramblewithfriends.device.scramble.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sapphire.app.AndroidSapphireActivity;
import sapphire.appexamples.wordscramblewithfriends.app.Game;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.R;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.ScrambleConstants;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.ScrambleConstants.Events;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.StateSingleton;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.util.RoundStats;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.util.ScrambleUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameActivity extends AndroidSapphireActivity {
	
	// Keys for passing game variables from TableActivity to this activity
	public static final String GAME_BUNDLE = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.GAME_BUNDLE";
	public static final String GAME_OBJECT = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.GAME_OBJECT";
	public static final String OPPONENT_NS = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.OPPONENT_NS";
	public static final String PLAYER_NUM = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.PLAYER_NUM";
	public static final String OPPONENT_UNAME = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.OPPONENT_UNAME";
	
	// Keys for handling player events
	private static final String PLAYER_EVENT_KEY = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.PLAYER_EVENT_KEY";
	private static final String PLAYER_WORD_KEY = "sapphire.appexamples.wordscramblewithfriends." +
			"device.scramble.activities.gameactivity.PLAYER_WORD_KEY";
	
	// Handler constants
	private static final int LETTERS_READY = 0;
	private static final int START_TURN = 1;
	private static final int END_TURN = 2;
	
	// Player numbers
	public static final int FIRST_PLAYER = 1;
	public static final int SECOND_PLAYER = 2;
	
	private static Handler handler;
	
	//private ScrambleMessage newMessage;
	
	private enum ExitAction {
		LEAVE, QUIT;
	}
	
	// Game object and state
	private Game game;
	private int playerNum;
	private String opponentUname;
	private String scrambleLetters;
	private RoundStats lastRoundStats;
	
	// UI components
	private TextView userTurnHint;
	private TextView scrambleLettersTxt;
	private TextView roundTxt;
	private TextView userTxt;
	private TextView opponentTxt;
	private TextView userScoreTxt;
	private TextView opponentScoreTxt;
	private Button buttonPlay;
	private Button buttonPass;
	private EditText scrambleInput;
	
	// UI Strings
	private String userTurnText;
	private String opponentTurnText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scramble);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case GameActivity.LETTERS_READY:
						setScrambleLetters();
						break;
					case GameActivity.START_TURN:
						handleRoundStats(GameActivity.START_TURN);
						break;
					case GameActivity.END_TURN:
						handleRoundStats(GameActivity.END_TURN);
						break;
					default:
						System.out.println("unknown message=" + msg.what);
						break;
				}
			}
		};
		
		// Scramble game layout components
		userTurnHint = (TextView) this.findViewById(R.id.text_turn);
		scrambleLettersTxt = (TextView) this.findViewById(R.id.scramble_letters);
		roundTxt = (TextView) this.findViewById(R.id.round);
		userTxt = (TextView) this.findViewById(R.id.player1);
		opponentTxt = (TextView) this.findViewById(R.id.player2);
		userScoreTxt = (TextView) this.findViewById(R.id.player1_score);
		opponentScoreTxt = (TextView) this.findViewById(R.id.player2_score);
		scrambleInput = (EditText) this.findViewById(R.id.input_letters);
		
		buttonPlay = (Button) this.findViewById(R.id.play_button);
		buttonPlay.setOnClickListener(new PlayButtonListener());
		buttonPass = (Button) this.findViewById(R.id.pass_button);
		buttonPass.setOnClickListener(new PassButtonListener());
		
		// Set items passed in from TableActivity
		Intent i = getIntent();
		if (!i.hasExtra(GameActivity.GAME_BUNDLE)) {
			throw new IllegalStateException("Game bundle is null");
		}
		Bundle b = i.getBundleExtra(GameActivity.GAME_BUNDLE);
		game = (Game) b.get(GameActivity.GAME_OBJECT);
		playerNum = b.getInt(GameActivity.PLAYER_NUM);
		opponentUname = b.getString(GameActivity.OPPONENT_UNAME);
		lastRoundStats = null;
		
		// Set view text for first round
		roundTxt.setText("Round 1");
		userTxt.setText(StateSingleton.getInstance().getUsername() + ":");
		opponentTxt.setText(opponentUname + ":");
		userScoreTxt.setText("0");
		opponentScoreTxt.setText("0");
		userTurnText = StateSingleton.getInstance().getUsername() + "'s turn!";
		opponentTurnText = opponentUname + "'s turn!";
		
		new GetScrambleLettersTask().execute(null, null);
		System.out.println("setup task is finished!");
		
		// First player (host) registers the dictionary
		if (playerNum == GameActivity.FIRST_PLAYER) {
			new SetupDictionaryTask().execute(null, null);
			enableDisableView(true);
		} else {
			enableDisableView(false);
			new WaitForOpponentTask().execute(null, null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.game_menu, menu);
	    return true;
	}
	
	@Override
	public void onDestroy() {
		game = null;
		handler = null;
		opponentUname = null;
		scrambleLetters = null;
		lastRoundStats = null;
		userTurnHint = null;
		scrambleLettersTxt = null;
		roundTxt = null;
		userTxt = null;
		opponentTxt = null;
		userScoreTxt = null;
		opponentScoreTxt = null;
		buttonPlay = null;
		buttonPass = null;
		scrambleInput = null;
		userTurnText = null;
		opponentTurnText = null;
		super.onDestroy();
	}
	
	/**
	 * Handler for the options menu.
	 * 
	 * @param item the selected item on the menu
	 * @return true on success
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.leave_game:
	            leaveGameDialog();
	            return true;
	        case R.id.quit:
	        	quitGameDialog();
	        	return true;
	        case R.id.help:
	            helpDialog();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// Exits the current game. Restarts the player options activity if 'leave' is selected, or
	// restarts the login activity if 'quit' is selected.
	private void exitGame(GameActivity.ExitAction action) {
		StateSingleton.getInstance().setIsPlayingGame(false);
		game.quitGame(playerNum);
		switch(action) {
			case LEAVE:
				Intent i_leave = new Intent(getApplicationContext(), PlayerOptionsActivity.class);
				startActivity(i_leave);
				finish();
				break;
			case QUIT:
				Intent i_quit = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i_quit);
				finish();
				break;
			default:
				finish();
		}
	}
	
	// Handles the event from the last play.
	private void handleRoundStats(int startOrEndTurn) {
		boolean startUserTurn = startOrEndTurn == GameActivity.START_TURN;
		String lastUname = (startUserTurn) ? 
				opponentUname : StateSingleton.getInstance().getUsername();
		switch (lastRoundStats.getEvent()) {
			case GAME_OVER:
				// TODO: end game
				break;
			case NOT_PLAYER_TURN:
				// TODO: reset game
				break;
			case NEW_ROUND:
				int newRound = lastRoundStats.getRound();
				showNewRoundDialog(newRound);
				new GetScrambleLettersTask().execute(null, null);
				showPassDialog(lastUname, lastRoundStats);
				if (startUserTurn)
					enableDisableView(true);
				else
					endTurn();
				roundTxt.setText("Round " + Integer.toString(newRound));
				break;
			case PLAY:
				showPlayDialog(lastUname, lastRoundStats);
				userScoreTxt.setText(Integer.toString(lastRoundStats.getTotalPoints()));
				if (startUserTurn)
					enableDisableView(true);
				else
					endTurn();
				break;
			case PASS:
				showPassDialog(lastUname, lastRoundStats);
				if (startUserTurn)
					enableDisableView(true);
				else
					endTurn();
				break;
			case QUIT:
				if (startUserTurn)
					opponentQuitDialog();
				break;
			default:
				throw new IllegalArgumentException("Unknown event: " + lastRoundStats.getEvent());
		}
		lastRoundStats = null;	// reset
	}
	
	// Resets the input word text box and starts the task of waiting for the opponent to finish.S
	private void endTurn() {
		scrambleInput.setText("");
		new WaitForOpponentTask().execute(null, null);
	}
	
	// Resets the scramble letters. Called at the start of each round.
	private void setScrambleLetters() {
		scrambleLettersTxt.setText(scrambleLetters);
		scrambleLettersTxt.setEnabled(true);
	}
	
	// Enables the view when it is 'this' players' turn.
	private void enableDisableView(boolean enabled) {
	    if (enabled) {
	    	userTurnHint.setText(userTurnText);
	    } else {
	    	userTurnHint.setText(opponentTurnText);
	    }
	    buttonPlay.setEnabled(enabled);
	    buttonPass.setEnabled(enabled);
	    scrambleInput.setHint(R.string.edit_message_hint);
	    scrambleInput.setEnabled(enabled);
	}
	
	private class PlayButtonListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
				enableDisableView(false);
				Bundle b = new Bundle();
				b.putInt(PLAYER_EVENT_KEY, ScrambleConstants.Events.PLAY.ordinal());
				b.putString(PLAYER_WORD_KEY, scrambleInput.getText().toString());
				new PlayerEventTask().execute(b);
			} else {
				ScrambleUtil.createToast(getApplicationContext(), getString(R.string.no_connection));
			}
		}
	}
	
	private class PassButtonListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
				enableDisableView(false);
				Bundle b = new Bundle();
				b.putInt(PLAYER_EVENT_KEY, ScrambleConstants.Events.PASS.ordinal());
				new PlayerEventTask().execute(b);
			} else {
				ScrambleUtil.createToast(getApplicationContext(), getString(R.string.no_connection));
			}
		}
	}
	
	////////////////////////////////////////// Dialogs ////////////////////////////////////////////
	
	private void leaveGameDialog() {
		String message = "Are you sure you want to leave the game?";
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					exitGame(GameActivity.ExitAction.LEAVE);
				}
			})
			.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void quitGameDialog() {
		String message = "Are you sure you want to quit?";
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					exitGame(GameActivity.ExitAction.QUIT);
				}
			})
			.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void helpDialog() {
		String message = "Help!!!";
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void opponentQuitDialog() {
		String message = opponentUname + " has quit the game :(";
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					exitGame(GameActivity.ExitAction.LEAVE);
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void gameOverDialog() {
		String message = "Game over... ";
		int userScore = Integer.parseInt(userScoreTxt.getText().toString());
		int opponentScore = Integer.parseInt(opponentScoreTxt.getText().toString());
		if (userScore > opponentScore) {
			message += "You win!!!\n\nFinalScores:\n\t";
		} else {
			message += opponentUname + " wins!!!\n\nFinalScores:\n\t";
		}
		message += StateSingleton.getInstance().getUsername() + ": " + userScore +
				"\n\t" + opponentUname + ": " + opponentScore;
		
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					exitGame(GameActivity.ExitAction.LEAVE);
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void showNewRoundDialog(int round) {
		String message = "Starting round " + round + "...";
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void showPlayDialog(String who, RoundStats rs) {
		String message = who + " played: " + rs.getWordPlayed() +
				"\n\tPoints earned: " + rs.getRoundPoints() +
				"\n\tTotal points: " + rs.getTotalPoints();
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	private void showPassDialog(String who, RoundStats rs) {
		String message = who + " passed.\n\tTotalPoints: " + rs.getTotalPoints();
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(message)
			.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		AlertDialog ad = adBuilder.create();
		ad.show();
	}
	
	////////////////////////////////////////// Asynchronous Tasks /////////////////////////////////
	
	// Registers the dictionary with the current game. Called once at the start of each game.
	private class SetupDictionaryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Map<String, List<String>> wordMap = ScrambleUtil.createDictionary(getApplicationContext());
			game.setDictionary((HashMap<String, List<String>>) wordMap);
			return null;
		}
	}
	
	// Gets the new scramble letters at the start of each round.
	private class GetScrambleLettersTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... args) {
			scrambleLetters = game.getScrambleLetters();
			if (scrambleLetters == null) {
				throw new IllegalStateException("Scramble letters are null");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void arg) {
			handler.sendEmptyMessage(GameActivity.LETTERS_READY);
		}
	}
	
	// Pulls the game object to see if it is 'this' players' turn yet.
	private class WaitForOpponentTask extends AsyncTask<Void, Void, RoundStats> {

		@Override
		protected RoundStats doInBackground(Void... params) {
			try {
				while (game.getPlayerTurn() != playerNum)
					Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return game.getLastRoundStats(); 
		}
		
		@Override
		protected void onPostExecute(RoundStats lastRoundStats) {
			GameActivity.this.lastRoundStats = lastRoundStats;
			handler.sendEmptyMessage(GameActivity.START_TURN);
		}
	}
	
	
	
	// Handles the player event (either pass or play).
	private class PlayerEventTask extends AsyncTask<Bundle, Void, RoundStats> {

		@Override
		protected RoundStats doInBackground(Bundle... args) {
			RoundStats rs;
			ScrambleConstants.Events event = Events.values()[((Integer) args[0].getInt(PLAYER_EVENT_KEY))];
			switch(event) {
				case PLAY:
					String wordPlayed = args[0].getString(PLAYER_WORD_KEY);
					if (wordPlayed == null)
						throw new IllegalStateException("Play event does not contain the played word");
					rs = game.play(playerNum, wordPlayed);
					break;
				case PASS:
					rs = game.pass(playerNum);
					break;
				default:
					throw new IllegalStateException("Unrecognized event: " + event);
			}
			return rs;
		}
		
		@Override
		protected void onPostExecute(RoundStats rs) {
			lastRoundStats = rs;
			handler.sendEmptyMessage(GameActivity.END_TURN);
		}
	}
}
