package Game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import Game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "game by SanaFan";
		config.vSyncEnabled = true;
		config.useGL30 = true;
		config.width = 1280/2;
		config.height = 704/2;
		//config.backgroundFPS=10;
		config.pauseWhenMinimized = true;
		config.addIcon("images/icon32.png", Files.FileType.Internal);
		new LwjglApplication(new Game(), config);
	}
}
