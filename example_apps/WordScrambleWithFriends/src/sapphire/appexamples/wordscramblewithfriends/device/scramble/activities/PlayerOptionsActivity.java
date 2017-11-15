package sapphire.appexamples.wordscramblewithfriends.device.scramble.activities;

import sapphire.app.AndroidSapphireActivity;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.R;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.R.id;
import sapphire.appexamples.wordscramblewithfriends.device.scramble.global.StateSingleton;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class PlayerOptionsActivity extends AndroidSapphireActivity {
	static final String BOOL_HOSTING_TABLE = "isHostingTable";
	
	private Button buttonJoinTable;
	private Button buttonHostTable;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.player_options);
		
		buttonJoinTable = (Button) this.findViewById(id.join_button);
		buttonHostTable = (Button) this.findViewById(id.host_button);
		buttonJoinTable.setEnabled(true);
		buttonHostTable.setEnabled(true);

		buttonJoinTable.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
					startTable(getApplicationContext(), TableActivity.JOIN_TABLE);
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		buttonHostTable.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
					startTable(getApplicationContext(), TableActivity.HOST_TABLE);
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void startTable(Context context, int joinOrHost) {
		Intent i = new Intent(context, TableActivity.class);
		i.putExtra(TableActivity.HOST_JOIN_KEY, joinOrHost);
		startActivity(i);
		finish();
	}
	
	@Override
	public void onDestroy() {
		buttonJoinTable = null;
		buttonHostTable = null;
		super.onDestroy();
	}
}
