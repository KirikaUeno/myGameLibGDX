package Game;

import Screens.PlayScreen;
import Screens.WelcomeScreen;
import Sprites.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
		Process process;
		String[] cmd = {"curl","localhost:8080/removeplayer?name="+playScreen.getPlayer().getName()};
		try {
			process = Runtime.getRuntime().exec(cmd);
			InputStream stdout = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
			String line1;
			while ((line1 = reader.readLine()) != null) {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		batch.dispose();
	}

	public void setPlayScreen(PlayScreen screen){
		this.playScreen=screen;
	}
}
