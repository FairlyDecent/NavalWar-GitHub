package nw.game.utils.interfaces;

import nw.game.utils.multiplayer.NWTurn;

import com.badlogic.gdx.utils.Array;

public interface GooglePlayServices {

	/** Sign in to Google Play Services */
	public void signIn();
	/** Sign out of Google Play Services */
	public void signOut();
	/** Returns true if the player has signed in to Google Play Services */
	public boolean isSignedIn();
	
	/** Returns an array containing all the active games of the connected player */
	public Array<NWTurn> getActiveGames();
}
