package nw.game.screens.stages;

import nw.game.utils.NWConfig;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class PlayStage extends Stage {

	/** Shape renderer used to draw game outlines */
	private ShapeRenderer shapes;
	/** Batch used to draw basics */
	private Batch batch;
	
	/** Font used for testing purposes */
	private BitmapFont font;
	
	/** Number of columns on the screen */
	private int columnNumber;
	/** Number of rows on the screen */
	private int rowNumber;
	
	public PlayStage(Batch batch) {
		this.batch = batch;
		
		shapes = new ShapeRenderer();
		shapes.setAutoShapeType(true);
		shapes.setColor(Color.GREEN);
		
		font = new BitmapFont();
		font.setScale(2.0f);
		font.setColor(Color.GREEN);
		
		columnNumber = 8;
		rowNumber = columnNumber * 2 - 1;
	}
	
	public void act() {
		super.act();
	}
	
	public void draw() {
		super.draw();
		
		shapes.begin(ShapeType.Line);
		// Columns
		for (int i = 1; i < columnNumber; i++) {
			shapes.line(NWConfig.WIDTH * i / columnNumber, 0, NWConfig.WIDTH * i / columnNumber, NWConfig.HEIGHT - NWConfig.HEIGHT / rowNumber);
		}
		
		// Rows
		for (int i = 1; i < rowNumber; i++) {
			shapes.line(NWConfig.WIDTH / columnNumber, NWConfig.HEIGHT * i / rowNumber, NWConfig.WIDTH, NWConfig.HEIGHT * i / rowNumber);
		}
		shapes.end();

		batch.begin();
		// Numbers (col)
		for (int i = 2; i <= columnNumber; i++) {
			font.draw(batch, String.valueOf(i - 1), NWConfig.WIDTH * (2 * i - 1) / (columnNumber * 2) - font.getBounds(String.valueOf(i - 2)).width / 2,
					NWConfig.HEIGHT * (rowNumber * 2 - 1) / (rowNumber * 2) + font.getBounds(String.valueOf(i - 1)).height / 2);
		}
		
		// Chars (row)
		for (int i = 1; i < rowNumber; i++) {
			char str = Character.toChars('A' + rowNumber - 2 - i + 1)[0];
			font.draw(batch, String.valueOf(str), NWConfig.WIDTH / (columnNumber * 2) - font.getBounds(String.valueOf(str)).width / 2,
					NWConfig.HEIGHT * (i * 2 - 1) / (rowNumber * 2) + font.getBounds(String.valueOf(str)).height / 2);
		}
		batch.end();
	}
	
	public void dispose() {
		super.dispose();
		
		shapes.dispose();
	}
}
