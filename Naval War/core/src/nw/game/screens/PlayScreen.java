package nw.game.screens;

import nw.game.screens.stages.PlayStage;
import nw.game.utils.NWConfig;
import nw.game.utils.multiplayer.NWMpUtils;
import nw.game.utils.multiplayer.NWTurn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;


public class PlayScreen extends NWScreen {

	/** Stage containing all gameplay elements */
	private PlayStage playStage;
	
	private Label turn;
	private LabelStyle style;
	
	private NWTurn turnData;
	
	protected void init() {
		turnData = NWMpUtils.gps.getActiveGameTurn();
		
		playStage = new PlayStage(batch);
		
		style = new LabelStyle();
		style.font = new BitmapFont();
		style.font.setScale(2);
		style.fontColor = Color.GREEN;
		
		turn = new Label("" + turnData.turnCounter, style);
		turn.setPosition(NWConfig.WIDTH / 2, NWConfig.HEIGHT / 2, Align.center);
		uiStage.addActor(turn);
	}

	protected void render() {
		/*playStage.act();
		playStage.draw();*/
	}

	protected void clear() {
		playStage.dispose();
	}
}
