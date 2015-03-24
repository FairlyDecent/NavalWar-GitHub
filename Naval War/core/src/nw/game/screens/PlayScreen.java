package nw.game.screens;

import nw.game.screens.stages.PlayStage;
import nw.game.utils.NWConfig;
import nw.game.utils.NWUtils;
import nw.game.utils.multiplayer.NWMpUtils;
import nw.game.utils.multiplayer.NWTurn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class PlayScreen extends NWScreen {

	/** Stage containing all gameplay elements */
	private PlayStage playStage;
	
	private Label turn;
	private LabelStyle style;
	
	private TextButton end;
	private TextButtonStyle sstyle;
	
	private NWTurn turnData;
	
	protected void init() {
		turnData = NWMpUtils.gps.getActiveGameTurn();
		
		playStage = new PlayStage(batch);
		
		style = new LabelStyle();
		style.font = new BitmapFont();
		style.font.setScale(2);
		style.fontColor = Color.GREEN;
		
		turn = new Label("" + turnData.turnCounter, style);
		turn.setPosition(NWConfig.WIDTH, NWConfig.HEIGHT, Align.topRight);
		uiStage.addActor(turn);
		
		sstyle = new TextButtonStyle();
		sstyle.font = new BitmapFont();
		sstyle.font.setScale(2);
		sstyle.fontColor = Color.RED;
		
		end = new TextButton("END TURN", sstyle);
		end.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				NWMpUtils.gps.endTurn();
				NWUtils.setScreen(new MainMenuScreen());
			}
		});
		end.setPosition(NWConfig.WIDTH / 2, NWConfig.HEIGHT / 2, Align.center);
		uiStage.addActor(end);
	}

	protected void render() {
		/*playStage.act();
		playStage.draw();*/
	}

	protected void clear() {
		playStage.dispose();
	}
}
