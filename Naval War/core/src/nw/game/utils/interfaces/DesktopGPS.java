package nw.game.utils.interfaces;

public class DesktopGPS implements GooglePlayServices {

	public void signIn() {
		System.out.println("signIn()");
	}

	public void signOut() {
		System.out.println("signOut()");
	}

	public void unlockAchievement(int achievementId) {
		System.out.println("unlockAchievement(" + achievementId + ")");
	}

	public boolean isSignedIn() {
		System.out.println("isSignedIn()");
		return false;
	}

	public void onQuickMatchStarted() {
		System.out.println("onQuickMatchStarted()");
	}

	public void onNormalMatchStarted() {
		System.out.println("onNormalMatchStarted()");
	}
}
