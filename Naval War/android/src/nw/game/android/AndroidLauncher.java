package nw.game.android;

import nw.game.NavalWar;
import nw.game.utils.interfaces.GooglePlayServices;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements GooglePlayServices {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new NavalWar(this), config);
	}

	public void signIn() {
		
	}

	public void signOut() {
		
	}

	public void unlockAchievement(int achievementId) {
		
	}
}
