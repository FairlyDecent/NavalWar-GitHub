package nw.game.android;

import nw.game.NavalWar;
import nw.game.utils.interfaces.GooglePlayServices;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.UpdateMatchResult;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "MainActivity";
	
	/** GameHelper instance */
	private GameHelper gameHelper;
	
	/** Alert Dialog instance */
	private AlertDialog alertDialog;

	/** True when in the process of resolving a connection failure */
	private boolean resolvingConnectionFailure = false;
	/** True when in a turn */
	private boolean isDoingTurn = false;
	
	/** Current match data */
	private NWTurn turnData;
	
	/** Current turn-based match */
	private TurnBasedMatch turnBasedMatch;
	/** Current turn-based match we're in! Null if not loaded */
	private TurnBasedMatch currentMatch;
	
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
			resolvingConnectionFailure = false;
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
	
	public void onQuickMatchStarted() {
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);
		TurnBasedMatchConfig config = TurnBasedMatchConfig.builder().setAutoMatchCriteria(autoMatchCriteria).build();
		
		ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
			public void onResult(InitiateMatchResult result) {
				processResult(result);
			}
		};
		
		Games.TurnBasedMultiplayer.createMatch(gameHelper.getApiClient(), config).setResultCallback(cb);
	}
	
	public void onNormalMatchStarted() {
		Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(gameHelper.getApiClient(), 1, 1, true);
		startActivityForResult(intent, 9002);
	}

	public void unlockAchievement(int achievementId) {
		if (isSignedIn()) {
			/* ... */
		}
	}
	
	/** Called when connected to Google Services */
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "onConnected(): Connection successful");
		
		if (connectionHint != null) {
			turnBasedMatch = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
			
			if (turnBasedMatch != null) {
				if (gameHelper.getApiClient() == null || !gameHelper.getApiClient().isConnected()) {
					Log.d(TAG, "Warning: Accessing TurnBasedMatch when not connected");
				}
			}
			
			updateMatch(turnBasedMatch);
			return;
		}
	}
	
	/** Called when conneciton to Google Services is suspended */
	public void onConnectionSuspended(int connectionHint) {
		Log.d(TAG, "onConnectionSuspended(): Trying to reconnect.");
		gameHelper.getApiClient().connect();
	}
	
	/** Called when connection to Google Services is failed */
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed(): attempting to resolve");
		
		if (resolvingConnectionFailure) {
			Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
			return;
		}
		
		resolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, gameHelper.getApiClient(), connectionResult, 9001, "There was an issue with sign in.  Please try again later.");
	}
	
	public void updateMatch(TurnBasedMatch match) {
		currentMatch = match;
		
		int status = match.getStatus();
		int turnStatus = match.getTurnStatus();
		
		switch (status) {
		case TurnBasedMatch.MATCH_STATUS_CANCELED:
			showWarning("Canceled!", "This match was canceled!");
			return;
		case TurnBasedMatch.MATCH_STATUS_EXPIRED:
			showWarning("Expired!", "This match has expired!");
			return;
		case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
			showWarning("Waiting for auto-match...", "We're still waiting for an auto-match partner.");
			return;
		case TurnBasedMatch.MATCH_STATUS_COMPLETE:
			if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
				showWarning("Complete!", "This match is over; someone finished it, and so did you! There is nothing to be done.");
				break;
			}
			// TODO: Note that in this state, I must still call "Finish" myself,
            // so I allow this to continue.
            showWarning("Complete!",
                    "This game is over; someone finished it!  You can only finish it now.");
		}
		
		// OK, it's active. Check turn status.
		switch (turnStatus) {
		case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
			turnData = NWTurn.unpersist(match.getData());
			// TODO: setGameplayUI();
			return;
		case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
			// TODO: Show current situation
			showWarning("Alas...", "It's not your turn.");
			break;
		case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
			showWarning("Good initiative!", "Still waiting for invitations.\n\nBe patient!");
		}
		
		turnData = null;
	}
	
	public void startMatch(TurnBasedMatch match) {
		turnData = new NWTurn();
		// Basic turn data stuff
		turnData.turnCounter = 1;
		
		currentMatch = match;
		
		String playerId = Games.Players.getCurrentPlayerId(gameHelper.getApiClient());
		String myParticipantId = match.getParticipantId(playerId);
		
		Games.TurnBasedMultiplayer.takeTurn(gameHelper.getApiClient(), match.getMatchId(), turnData.persist(), myParticipantId).setResultCallback(
				new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
					public void onResult(UpdateMatchResult result) {
						processResult(result);
					}
				});
	}
	
	public void askForRematch() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Do you want to rematch?");
		
		alertDialogBuilder.setCancelable(false).setPositiveButton("Sure! Rematch!",  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				rematch();
			}
		}).setNegativeButton("No.", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		alertDialogBuilder.show();
	}
	
	public void rematch() {
		ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
			public void onResult(InitiateMatchResult result) {
				processResult(result);
			}
		};
		
		Games.TurnBasedMultiplayer.rematch(gameHelper.getApiClient(), currentMatch.getMatchId()).setResultCallback(cb);
	}
	
	private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
		if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
			return;
		}
		
		isDoingTurn = false;
		
		showWarning("Match", "This match is canceled. All other players will have their game ended.");
	}
	
	private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
		TurnBasedMatch match = result.getMatch();
		
		if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
			return;
		}
		
		if (match.getData() != null) {
			updateMatch(match);
			return;
		}
		
		startMatch(match);
	}
	
	private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
		TurnBasedMatch match = result.getMatch();
		
		if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
			return;
		}
		
		isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
		showWarning("Left", "You've left this match.");
	}
	
	private void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
		TurnBasedMatch match = result.getMatch();
		
		if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
			return;
		}
		
		if (match.canRematch()) askForRematch();
		
		isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
		if (isDoingTurn) {
			updateMatch(match);
			return;
		}
	}
	
	/** Returns false if something went wrong */
	private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
		switch (statusCode) {
		case GamesStatusCodes.STATUS_OK:
			return true;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
			Toast.makeText(this,  "Stored action for later.  (Please remove this toast before release.)", Toast.LENGTH_SHORT).show();
			return true;
		case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
			showErrorMessage(match, statusCode, "All players must be trusted game testers.");
		case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
			showErrorMessage(match, statusCode, "This rematch has already been started!");
			break;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
            showErrorMessage(match, statusCode, "Network error: Operation failed");
            break;
        case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
            showErrorMessage(match, statusCode, "Games client reconnect required");
            break;
        case GamesStatusCodes.STATUS_INTERNAL_ERROR:
            showErrorMessage(match, statusCode, "Internal error");
            break;
        case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
            showErrorMessage(match, statusCode, "This match is incative.");
            break;
        case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
            showErrorMessage(match, statusCode, "This match ha locally-modified data. This operation cannot be performed until the match is sent to the server.");
            break;
        default:
            showErrorMessage(match, statusCode, "Unexpected status");
            Log.d(TAG, "Did not have warning or string to deal with: "
                    + statusCode);
		}
		
		return false;
	}
	
	public void showErrorMessage(TurnBasedMatch match, int statusCode, String msg) {
		showWarning("Warning", msg);
	}
	
	public void showWarning(String title, String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder.setTitle(title).setMessage(msg);
		
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK",  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
