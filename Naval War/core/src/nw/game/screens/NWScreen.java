package nw.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public abstract class NWScreen implements Screen {

	/** Stage containing all UI elements */
	protected Stage uiStage;
	
	/** Batch used to draw screen specific basics */
	protected Batch batch;
	
	/** Initializes the screen specific elements */
	protected abstract void init();
	/** Renders/updates the screen specific elements */
	protected abstract void render();
	/** Dispose of all screen specific elements */
	protected abstract void clear();
	
	public void show() {
		uiStage = new Stage();
		uiStage.addAction(Actions.sequence(Actions.alpha(0.0f), Actions.fadeIn(1.0f)));
		Gdx.input.setInputProcessor(uiStage);
		
		batch = new SpriteBatch();
		
		init();
	}

	public void render(float delta) {
		if (uiStage != null) {
			uiStage.act();
			uiStage.draw();
		}
		render();
	}
	
	/**
	 * Sets the uiStage to the given value
	 * @param uiStage
	 */
	public void setUiStage(Stage uiStage) {
		this.uiStage = uiStage;
	}
	
	/**
	 * Returns the stage containing all UI elements
	 * @return {@link #uiStage}
	 */
	public Stage getUiStage() {
		return uiStage;
	}

	/** Not yet implemented */
	public void resize(int width, int height) {
	}

	/** Not yet implemented */
	public void pause() {
	}

	/** Not yet implemented */
	public void resume() {
	}
	
	/** Not yet implemented */
	public void hide() {
	}

	public void dispose() {
		if (uiStage != null) uiStage.dispose();
		clear();
	}
}
