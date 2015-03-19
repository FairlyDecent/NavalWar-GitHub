package nw.game.utils;

import com.badlogic.gdx.Gdx;

public class NWConfig {
	
	/** Width of the device's screen */
	public static final float WIDTH = Gdx.graphics.getWidth();
	/** Height of the device's screen */
	public static final float HEIGHT = Gdx.graphics.getHeight();
	/** Scale applied to assets to fit the device's screen */
	public static final float SCALE = (WIDTH / HEIGHT == 9.0f / 16.0f) ? HEIGHT / 2280.0f : WIDTH / 1440.0f;
	
	/** Title of the game */
	public static final String TITLE = "Naval War";
	/** Current version state of the game */
	public static final String VERSION_STATE = "Alpha";
	/** Current version number of the game */
	public static final String VERSION_NUMBER = "0.0";
}
