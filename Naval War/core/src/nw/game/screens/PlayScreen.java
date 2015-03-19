package nw.game.screens;

import nw.game.screens.stages.PlayStage;


public class PlayScreen extends NWScreen {

	/** Stage containing all gameplay elements */
	private PlayStage playStage;
	
	protected void init() {
		playStage = new PlayStage(batch);
	}

	protected void render() {
		playStage.act();
		playStage.draw();
	}

	protected void clear() {
		playStage.dispose();
	}
}
