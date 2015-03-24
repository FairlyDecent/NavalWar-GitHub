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
	
	public NWTurn getActiveGameTurn() {
		System.out.println(TAG + ": getActiveGameTurn()");
		return new NWTurn();
	}
	
	public Array<NWTurn> getActiveGames() {
		System.out.println(TAG + ": getActiveGames()");
		return new Array<NWTurn>();
	}

	public void createQuickMatch() {
		System.out.println(TAG + ": startQuickMatch()");
	}

	public void endTurn() {
		System.out.println(TAG + ": endTurn()");
	}

	public void checkInbox() {
		System.out.println(TAG + ": checkInbox()");
	}
}
