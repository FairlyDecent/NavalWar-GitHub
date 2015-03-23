package nw.game.android;

import nw.game.NavalWar;
import nw.game.utils.interfaces.GooglePlayServices;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {

	private static final String TAG = "MainActivity";
	
	/** GameHelper instance */
	private GameHelper gameHelper;
	
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		
		GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			public void onSignInSucceeded() { }
			public void onSignInFailed() { }
		};
		
		gameHelper.setup(gameHelperListener);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new NavalWar(this), config);
	}
	
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}
	
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	public void signIn() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (Exception e) {
			Gdx.app.log(TAG, "Log in failed: " + e.getMessage());
		}
	}

	public void signOut() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.signOut();
				}
			});
		} catch (Exception e) {
			Gdx.app.log(TAG, "Log out failed: " + e.getMessage());
		}
	}

	public boolean isSignedIn() {
		return gameHelper.isSignedIn();
	}

	public void unlockAchievement(int achievementId) {
		if (isSignedIn()) {
			/* ... */
		}
	}
}
