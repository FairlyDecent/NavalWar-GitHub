package nw.game.android;

import java.util.ArrayList;

import nw.game.NavalWar;
import nw.game.utils.interfaces.GooglePlayServices;
import nw.game.utils.multiplayer.NWTurn;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.LoadMatchesResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.UpdateMatchResult;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {

	public static final String TAG = "MainActivity";
	
	private static final int RC_CHECK_INBOX = 9001;
	
	/** Alert dialog used to display warnings ({@link #showWarning(String, String)}) */
	private AlertDialog alertDialog;
	
	/** GameHelper instance */
	private GameHelper gameHelper;
	
	/** Current match data */
	private NWTurn turnData;
	
	/** Current match */
	private TurnBasedMatch match;
	
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
		
		if (requestCode == RC_CHECK_INBOX) {
			if (resultCode != Activity.RESULT_OK) return;
			
			TurnBasedMatch match = data.getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
			
			if (match != null) updateMatch(match);
			
			Log.d(TAG, "Match = " + match);
		}
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
	
	public void createQuickMatch() {
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 2, 0);
        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder().setAutoMatchCriteria(autoMatchCriteria).build();
        
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
			public void onResult(InitiateMatchResult result) {
				processResult(result);
			}
        };
        
        Games.TurnBasedMultiplayer.createMatch(gameHelper.getApiClient(), tbmc).setResultCallback(cb);
	}
	
	public void endTurn() {
		String nextParticipantId = getNextParticipantId();
		turnData.turnCounter++;
		
		Games.TurnBasedMultiplayer.takeTurn(gameHelper.getApiClient(), match.getMatchId(), turnData.persist(), nextParticipantId).setResultCallback(
				new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
					public void onResult(UpdateMatchResult result) {
						processResult(result);
					}
				});
		
		turnData = null;
	}
	
	public NWTurn getActiveGameTurn() {
		return (turnData != null) ? turnData : new NWTurn();
	}
	
	public void checkInbox() {
		Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(gameHelper.getApiClient());
		startActivityForResult(intent, RC_CHECK_INBOX);
	}
	
	public Array<NWTurn> getActiveGames() {
		int[] turnStatus = new int[]{TurnBasedMatch.MATCH_TURN_STATUS_INVITED, TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN, TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN};
		final Array<NWTurn> array = new Array<NWTurn>();
		Games.TurnBasedMultiplayer.loadMatchesByStatus(gameHelper.getApiClient(), turnStatus).setResultCallback(
				new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
					public void onResult(LoadMatchesResult result) {
						if (result.getMatches().hasData()) {
							if (result.getMatches().getCompletedMatches().getCount() > 0) {
								for (int i = 0; i < result.getMatches().getCompletedMatches().getCount(); i++) {
									array.add(NWTurn.unpersist(result.getMatches().getCompletedMatches().get(i).getData()));
								}
							}
							if (result.getMatches().getMyTurnMatches().getCount() > 0) {
								for (int i = 0; i < result.getMatches().getMyTurnMatches().getCount(); i++) {
									array.add(NWTurn.unpersist(result.getMatches().getMyTurnMatches().get(i).getData()));
								}
							}
							if (result.getMatches().getTheirTurnMatches().getCount() > 0) {
								for (int i = 0; i < result.getMatches().getTheirTurnMatches().getCount(); i++) {
									array.add(NWTurn.unpersist(result.getMatches().getTheirTurnMatches().get(i).getData()));
								}
							}
						}
					}
		});
		return array;
	}
	
	
	public String getNextParticipantId() {
		String playerId = Games.Players.getCurrentPlayerId(gameHelper.getApiClient());
		String myParticipantId = match.getParticipantId(playerId);
		
		ArrayList<String> participantIds = match.getParticipantIds();
		
		int desiredIndex = -1;
		for (int i = 0; i < participantIds.size(); i++) {
			if (participantIds.get(i).equals(myParticipantId)) {
				desiredIndex = i + 1;
			}
		}
		
		if (desiredIndex < participantIds.size()) return participantIds.get(desiredIndex);
		
		if (match.getAvailableAutoMatchSlots() <= 0) return participantIds.get(0);
		else return null;
	}
	
	/** Starts a match */
	public void startMatch(TurnBasedMatch match) {
		turnData = new NWTurn();
		this.match = match;
		
		String myParticipantId = match.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
		
		Games.TurnBasedMultiplayer.takeTurn(gameHelper.getApiClient(), match.getMatchId(), turnData.persist(),
				myParticipantId).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
					public void onResult(UpdateMatchResult result) {
						processResult(result);
					}
				});
	}

	/** Update a match */
	public void updateMatch(TurnBasedMatch match) {
		this.match = match;
		int status = match.getStatus();
		int turnStatus = match.getTurnStatus();
		
		switch (status) {
		case TurnBasedMatch.MATCH_STATUS_CANCELED:
			showWarning("Canceled!", "This game was canceled!");
			return;
		case TurnBasedMatch.MATCH_STATUS_EXPIRED:
			showWarning("Expired!", "This games is expired. So sad!");
			return;
		case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
			showWarning("Waiting for auto-match...", "We're still waiting for an automatch partner.");
			return;
		case TurnBasedMatch.MATCH_STATUS_COMPLETE:
			if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
				showWarning("Complete!", "This game is over; someone finished it! You can only finish it now!");
				break;
			}
			showWarning("Complete!", "This game is over; someone finished it! You can only finish it now!");
		}
		
		switch (turnStatus) {
		case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
			turnData = NWTurn.unpersist(match.getData());
			return;
		case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
			showWarning("Alas...", "It's not your turn.");
			break;
		case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
			showWarning("Good initiative!", "Still waiting for invitations.\n\nBe patient!");
		}
		
		turnData = null;
	}

	/** Process the result from a Callback */
	public void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
		TurnBasedMatch match = result.getMatch();

		if (!checkStatusCode(match, result.getStatus().getStatusCode()))
			return;

		if (match.getData() != null) {
			// This match was already started.
			updateMatch(match);
			return;
		}

		startMatch(match);
	}
	
	public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
		TurnBasedMatch match = result.getMatch();
		
		if (!checkStatusCode(match, result.getStatus().getStatusCode())) return;
		
		// if (match.canRematch()) askForRematch();
		
		if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
			updateMatch(match);
			return;
		}
	}

	/**
	 * Check the status of a Callback Returns false if something went wrong.
	 */
	public boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
		switch (statusCode) {
		case GamesStatusCodes.STATUS_OK:
			return true;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
			return true;
		case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
			showErrorMessage(match, statusCode, "status_multiplayer_error_not_trusted_tester");
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
			showErrorMessage(match, statusCode, "match_error_already_rematched");
			break;
		case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
			showErrorMessage(match, statusCode, "network_error_operation_failed");
			break;
		case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
			showErrorMessage(match, statusCode, "client_reconnect_required");
			break;
		case GamesStatusCodes.STATUS_INTERNAL_ERROR:
			showErrorMessage(match, statusCode, "internal_error");
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
			showErrorMessage(match, statusCode, "match_error_inactive_match");
			break;
		case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
			showErrorMessage(match, statusCode, "match_error_locally_modified");
			break;
		default:
			showErrorMessage(match, statusCode, "unexpected_status");
			Log.d(TAG, "Did not have warning or string to deal with: " + statusCode);
		}

		return false;
	}
	
	/** Calls {@link #showWarning(String, String)} */
	public void showErrorMessage(TurnBasedMatch match, int statusCode, String msg) {
		showWarning("Warning", msg);
	}
	
	/** Shows a warning on screen */
	public  void showWarning(String title, String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder.setTitle(title).setMessage(msg);
		
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
		});
		
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
