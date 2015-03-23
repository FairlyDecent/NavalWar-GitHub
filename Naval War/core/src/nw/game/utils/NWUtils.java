package nw.game.utils;

import nw.game.screens.NWScreen;
import nw.game.utils.interfaces.GooglePlayServices;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class NWUtils {
	
	/** Google Play Services instance */
	public static GooglePlayServices gps;
	
	/** Asset manager for all game assets */
	public static final AssetManager RESOURCES = new AssetManager();
	
	/** Sets the screen while keeping the same UI stage */
	public static void setScreenWithUiStage(NWScreen nextScreen) {
		NWScreen currentScreen = (NWScreen) ((Game) Gdx.app.getApplicationListener()).getScreen();
		nextScreen.setUiStage(currentScreen.getUiStage());
		((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen);
		currentScreen.dispose();
	}
	
	/** Sets the screen */
	public static void setScreen(NWScreen nextScreen) {
		NWScreen currentScreen = (NWScreen) ((Game) Gdx.app.getApplicationListener()).getScreen();
		((Game) Gdx.app.getApplicationListener()).setScreen(nextScreen);
		currentScreen.dispose();
	}
}
