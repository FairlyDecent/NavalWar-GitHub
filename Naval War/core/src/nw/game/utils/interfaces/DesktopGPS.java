package nw.game.utils.interfaces;

import nw.game.utils.multiplayer.NWTurn;

import com.badlogic.gdx.utils.Array;

public class DesktopGPS implements GooglePlayServices {

	private static final String TAG = "DesktopGPS";
	
	public void signIn() {
		System.out.println(TAG + ": signIn()");
	}

	public void signOut() {
		System.out.println(TAG + ": signOut()");
	}

	public boolean isSignedIn() {
		System.out.println(TAG + ": isSignedIn()");
		return false;
	}
	
	public Array<NWTurn> getActiveGames() {
		System.out.println(TAG + ": getActiveGames()");
		return null;
	}
}
