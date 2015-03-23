package nw.game.screens;

import nw.game.utils.NWUtils;

public class MainMenuScreen extends NWScreen {

	protected void init() {
		System.out.println(NWUtils.gps.getActiveGames());
	}

	protected void render() {
	}

	protected void clear() {
	}
}
