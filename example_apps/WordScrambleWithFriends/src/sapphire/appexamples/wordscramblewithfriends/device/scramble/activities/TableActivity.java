package sapphire.appexamples.wordscramblewithfriends.device.scramble.activities;

import static sapphire.runtime.Sapphire.new_;
import sapphire.app.AndroidSapphireActivity;
import sapphire.appexamples.wordscramblewithfriends.app.Game;
import sapphire.appexamples.wordscramblewithfriends.app.LocationService;
import sapphire.appexamples.wordscramblewithfriends.app.NotificationService;
import sapphire.appexamples.wordscramblewithfriends.app.Table;
import sapphire.appexamples.wordscramblewithfriends.app.TableManager;
import sapphire.appexamples.wordscramblewithfriends.app.UserManager;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.R;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.ScrambleConstants;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.StateSingleton;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.util.ScrambleMessage;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;


public class TableActivity extends AndroidSapphireActivity {
	public static final int JOIN_TABLE = 0;
	public static final int HOST_TABLE = 1;
	
	public static final String HOST_JOIN_KEY = "sapphire.appexamples.wordscramblewithfriends" +
			".device.scramble.activities.TableActivity.HOST_OR_JOIN";
	
	private ProgressDialog hourGlass;
	private AlertDialog noTableDialog;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		hourGlass = new ProgressDialog(this);
		hourGlass.setMessage("Waiting for available table");
		hourGlass.setIndeterminate(true);
		hourGlass.setCancelable(false);
		
		noTableDialog = getNoTablesAvailableDialog();

		int hostOrJoin = getIntent().getIntExtra(HOST_JOIN_KEY, -1);
		System.out.println("host or join = " + hostOrJoin);
		new GetTableObjectTask().execute(hostOrJoin);
	}
	
	@Override
	public void onPause() {
		if (hourGlass != null) {
            hourGlass.dismiss();
        }
		super.onPause();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();

	    if(noTableDialog != null)
	        noTableDialog.dismiss();
	}
	
	private void startGame(Context context, Bundle bundle) {
		Intent intent = new Intent(context, GameActivity.class);
		intent.putExtra(GameActivity.GAME_BUNDLE, bundle);
		startActivity(intent);
		finish();
	}
	
	private AlertDialog getNoTablesAvailableDialog() {
		AlertDialog alert = null;
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder
			.setMessage(R.string.no_table_available_message)
			.setPositiveButton(R.string.host_table_option, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					new GetTableObjectTask().execute(TableActivity.HOST_TABLE);
				}
			})
			.setNeutralButton(R.string.try_again_option, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					new GetTableObjectTask().execute(TableActivity.JOIN_TABLE);
				}
			})
			.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(getApplicationContext(), PlayerOptionsActivity.class);
					startActivity(intent);
					finish();
				}
			});
		return adBuilder.create();
	}
	
	private class GetTableObjectTask extends AsyncTask<Integer, Void, Bundle> {
		private int playerNum;
		private Game game;
		private String opponentUname;
		private NotificationService nsOpponent;
		
		public GetTableObjectTask() {
			playerNum = 0;
			game = null;
			nsOpponent = null;
		}
		
		@Override
		protected Bundle doInBackground(Integer...params) {
			if (!StateSingleton.getInstance().hasTable() && !StateSingleton.getInstance().isPlayingGame()) {
				LocationService ls = (LocationService) new_(LocationService.class);
				ls.initialize(getApplicationContext());
				
				// Work around for location service object
				Location location = ls.getLocation();
				StateSingleton.getInstance().setLocationService(ls);
				TableManager tm = StateSingleton.getInstance().getTableManager();

				// Join table if one is available
				if (params[0] == TableActivity.JOIN_TABLE && tm.tableAvailable()) {		
					joinTable(tm, StateSingleton.getInstance().getUsername(), location);
					
				// If there are no available tables, ask user to host table instead
				} else if (params[0] == TableActivity.JOIN_TABLE) {
					return null;

				// Host table
				} else {
					hostTable(tm, StateSingleton.getInstance().getUsername(), location);
				}
				// Set location service to be inactive while user is playing the game
				ls.deactivateLocationService();
				StateSingleton.getInstance().setHasTable(false);
				StateSingleton.getInstance().setIsPlayingGame(true);
				// TODO: make sure one user does not call this twice for the same table
				
				
				Bundle b = new Bundle();
				b.putInt(GameActivity.PLAYER_NUM, playerNum);
				b.putString(GameActivity.OPPONENT_UNAME, opponentUname);
				b.putSerializable(GameActivity.GAME_OBJECT, game);
				b.putSerializable(GameActivity.OPPONENT_NS, nsOpponent);
				return b;
			} else {
				// TODO: return user to active game or table
				return null;
			}
		}
		
		private void hostTable(TableManager tm, String username, Location location) {
			playerNum = GameActivity.FIRST_PLAYER;
			NotificationService ns = StateSingleton.getInstance().getNotificationService();
			Table t = tm.hostTable(username, location.getLatitude(), location.getLongitude());
			StateSingleton.getInstance().setHasTable(true);
			System.out.println("host");
			try {
				while (ns.noMessages())
					Thread.sleep(5000);

				// Message should contain guest's notification service reference
				ScrambleMessage nsOppMsg = ns.pollMessage();
				nsOpponent = (NotificationService) nsOppMsg.getMessageObjects()[0];
				if (nsOppMsg.getMessage() != ScrambleConstants.OPPONENT_NOTIFICATION_REF) {
					System.out.println("Unexpected message received: " + nsOppMsg.getMessage());
				}
				if (nsOpponent == null) {
					throw new IllegalStateException("Opponent's notification service object is null");
				}
				opponentUname = t.getGuestname();
				game = (Game) new_(Game.class, t.getHostname(), opponentUname, t.getTableId());
				tm.leaveTable(t);
				if (game == null) {
					throw new IllegalStateException("Game object is null");
				}
				// TODO: send game notification to opponent and start
				ScrambleMessage sendGameObj = new ScrambleMessage(ScrambleConstants.GAME_READY, game);
				nsOpponent.notify(sendGameObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void joinTable(TableManager tm, String username, Location location) {
			System.out.println("join");
			playerNum = GameActivity.SECOND_PLAYER;
			NotificationService ns = StateSingleton.getInstance().getNotificationService();
			Table t = tm.joinTable(username, location.getLatitude(), location.getLongitude());
			StateSingleton.getInstance().setHasTable(true);
			
			// Get opponent's notification service reference
			UserManager um = StateSingleton.getInstance().getUserManager();
			opponentUname = t.getHostname();
			nsOpponent = um.getUser(opponentUname).getNotificationService();
			if (nsOpponent == null) {
				throw new IllegalStateException("Opponent's notification service object is null");
			}
			
			// Notify opponent and send them my NS reference
			ScrambleMessage msg2 = new ScrambleMessage(ScrambleConstants.OPPONENT_NOTIFICATION_REF, StateSingleton.getInstance().getNotificationService());
			nsOpponent.notify(msg2);
			try {
				while (ns.noMessages())
					Thread.sleep(5000);
				
				// This message should be from opponent saying that the game is ready
				ScrambleMessage gameReady = ns.pollMessage();
				if (gameReady.getMessage() != ScrambleConstants.GAME_READY) {
					// TODO: restart?
					System.out.println("received unexpected message = " + gameReady.getMessage());
				} else {
					game = (Game) gameReady.getMessageObjects()[0];
					if (game == null) {
						throw new IllegalStateException("Game object is null");
					}
				}
				tm.leaveTable(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			hourGlass.show();
		}
		
		@Override
        protected void onPostExecute(Bundle bundle) {
            if (hourGlass != null) {
                hourGlass.dismiss();
            }
            if (bundle != null)
            	startGame(getApplicationContext(), bundle);
            TableActivity.this.noTableDialog.show();
        }

        @Override
        protected void onCancelled() {
            if (hourGlass != null) {
                hourGlass.dismiss();
            }
            if (noTableDialog != null) {
            	noTableDialog.dismiss();
            }
        }
	}
}
