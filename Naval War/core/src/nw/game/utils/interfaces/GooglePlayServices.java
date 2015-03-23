package nw.game.utils.interfaces;

public interface GooglePlayServices {

	/** Sign in to Google Play Services */
	public void signIn();
	/** Sign out of Google Play Services */
	public void signOut();
	/** Returns true if the player has signed in to Google Play Services */
	public boolean isSignedIn();
	
	/** Unlocks the specified achievement for the signed in player */
	public void unlockAchievement(int achievementId);
}
