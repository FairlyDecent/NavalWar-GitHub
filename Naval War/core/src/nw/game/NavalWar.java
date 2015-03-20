package nw.game;

import nw.game.screens.LoadingScreen;
import nw.game.utils.NWUtils;
import nw.game.utils.interfaces.GooglePlayServices;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class NavalWar extends Game {
	
	public NavalWar(GooglePlayServices googlePlayServices) {
		NWUtils.gps = googlePlayServices;
	}
	
	public void create () {
		setScreen(new LoadingScreen());
	}

	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		getScreen().render(Gdx.graphics.getDeltaTime());
	}
}
