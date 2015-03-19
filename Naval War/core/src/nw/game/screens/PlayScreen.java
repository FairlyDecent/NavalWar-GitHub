package nw.game.screens;

import nw.game.utils.NWConfig;
import nw.game.utils.NWUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class PlayScreen extends NWScreen {

	/** Shape renderer used to draw game outlines */
	private ShapeRenderer shapes;
	
	/** Number of columns on the screen */
	private int columnNumber;
	/** Number of rows on the screen */
	private int rowNumber;
	
	protected void init() {
		shapes = new ShapeRenderer();
		shapes.setAutoShapeType(true);
		shapes.setColor(Color.GREEN);
		
		NWUtils.FONT.setScale(2.0f);
		NWUtils.FONT.setColor(Color.GREEN);
		
		columnNumber = 8;
		System.out.println(columnNumber);
		rowNumber = columnNumber * 2 - 1;
	}

	protected void render() {
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
			NWUtils.FONT.draw(batch, String.valueOf(i - 1), NWConfig.WIDTH * (2 * i - 1) / (columnNumber * 2) - NWUtils.FONT.getBounds(String.valueOf(i - 2)).width / 2,
					NWConfig.HEIGHT * (rowNumber * 2 - 1) / (rowNumber * 2) + NWUtils.FONT.getBounds(String.valueOf(i - 1)).height / 2);
		}
		
		// Chars (row)
		for (int i = 1; i < rowNumber; i++) {
			char str = Character.toChars('A' + rowNumber - 2 - i + 1)[0];
			NWUtils.FONT.draw(batch, String.valueOf(str), NWConfig.WIDTH / (columnNumber * 2) - NWUtils.FONT.getBounds(String.valueOf(str)).width / 2,
					NWConfig.HEIGHT * (i * 2 - 1) / (rowNumber * 2) + NWUtils.FONT.getBounds(String.valueOf(str)).height / 2);
		}
		batch.end();
	}

	protected void clear() {
		shapes.dispose();
	}
}
