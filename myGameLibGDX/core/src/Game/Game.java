package Game;

import Screens.PlayScreen;
import Screens.WelcomeScreen;
import Sprites.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Game extends com.badlogic.gdx.Game{
	public static final int V_WIDTH = 1280/2;
	public static final int V_HEIGHT = 704/2;
	public static final float PPM = 16;
	public SpriteBatch batch;
	private PlayScreen playScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		WelcomeScreen welcomeScreen = new WelcomeScreen(this);
		setScreen(welcomeScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		//remove player from the list
		batch.dispose();
	}

	public void setPlayScreen(PlayScreen screen){
		this.playScreen=screen;
	}
}
