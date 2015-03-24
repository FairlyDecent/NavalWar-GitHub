package nw.game.android;

import nw.game.NavalWar;
import nw.game.utils.interfaces.GooglePlayServices;
import nw.game.utils.multiplayer.NWTurn;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.LoadMatchesResult;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {

	private static final String TAG = "MainActivity";
	
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
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, 0);
        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder().setAutoMatchCriteria(autoMatchCriteria).build();

        // Start the match
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
            	TurnBasedMatch mMatch = result.getMatch();

                if (match.getData() != null) {
                    // This is a game that has already started, so I'll just start
                	//updateMatch(match);
                    return;
                }
                
                turnData = new NWTurn();
                match = mMatch;

                String playerId = Games.Players.getCurrentPlayerId(gameHelper.getApiClient());
                String myParticipantId = match.getParticipantId(playerId);

                Games.TurnBasedMultiplayer.takeTurn(gameHelper.getApiClient(), match.getMatchId(), turnData.persist(), myParticipantId).setResultCallback(
                        new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                            public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            	TurnBasedMatch mMatch = result.getMatch();

                                if (mMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                                	//updateMatch(mMatch);
                                    return;
                                }
                            }
                        });
            }
        };
        
        Games.TurnBasedMultiplayer.createMatch(gameHelper.getApiClient(), tbmc).setResultCallback(cb);
	}
	
	public NWTurn getActiveGameTurn() {
		return (turnData != null) ? turnData : new NWTurn();
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
	
}
