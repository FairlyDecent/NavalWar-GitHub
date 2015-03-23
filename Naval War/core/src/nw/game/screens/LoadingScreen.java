package nw.game.screens;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import nw.game.utils.NWUtils;

public class LoadingScreen extends NWScreen {

	protected void init() {
		loadAssets();
	}
	
	private void loadAssets() {
		// TODO: Load assets
	}

	protected void render() {
		if (NWUtils.RESOURCES.update()) {
			uiStage.addAction(Actions.sequence(Actions.fadeOut(1.0f), Actions.run(new Runnable() {
				public void run() {
					NWUtils.setScreen(new MainMenuScreen());
				}
			})));
		}
	}

	protected void clear() {
		
	}
}
