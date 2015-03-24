package nw.game.screens;

import nw.game.utils.NWConfig;
import nw.game.utils.NWUtils;
import nw.game.utils.multiplayer.NWMpUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen extends NWScreen {

	private TextButton start;
	private TextButtonStyle style;
	
	protected void init() {
		style = new TextButtonStyle();
		style.font = new BitmapFont();
		style.font.setScale(2);
		style.fontColor = Color.BLUE;
		
		start = new TextButton("Quick Game", style);
		start.setPosition(NWConfig.WIDTH / 2, NWConfig.HEIGHT / 2, Align.center);
		start.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				if (NWMpUtils.gps != null) {
					if (NWMpUtils.gps.isSignedIn()) {
						NWMpUtils.gps.createQuickMatch();
						NWUtils.setScreen(new PlayScreen());
					}
				}
			}
		});
		uiStage.addActor(start);
	}

	protected void render() {
	}

	protected void clear() {
	}
}
