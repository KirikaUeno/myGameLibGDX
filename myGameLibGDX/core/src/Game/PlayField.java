package Game;

import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayField extends Game {
	public static final int V_WIDTH = 1280/2;
	public static final int V_HEIGHT = 704/2;
	public static final float PPM = 16;
	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		//this.resize(V_WIDTH,V_HEIGHT);

		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
