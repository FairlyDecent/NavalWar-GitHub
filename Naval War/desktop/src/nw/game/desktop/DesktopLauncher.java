package nw.game.desktop;

import nw.game.NavalWar;
import nw.game.utils.interfaces.DesktopGPS;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 640;
		config.width = config.height * 2 / 3;
		new LwjglApplication(new NavalWar(new DesktopGPS()), config);
	}
}
