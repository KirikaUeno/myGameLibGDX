package Game;

import Screens.PlayScreen;
import Screens.WelcomeScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Game extends com.badlogic.gdx.Game{
	public static final int V_WIDTH = 1280/2;
	public static final int V_HEIGHT = 704/2;
	public static final float PPM = 16;
	public SpriteBatch batch;
	public static Firestore fS;
	private PlayScreen playScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();

		FileInputStream serviceAccount = null;
		FirebaseOptions options = null;
		try { serviceAccount = new FileInputStream("serviceAccount.json"); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		try { assert serviceAccount != null;
			options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://game-by-sanafan-default-rtdb.firebaseio.com")
					.build(); }
		catch (IOException e) { e.printStackTrace(); }

		assert options != null;
		FirebaseApp.initializeApp(options);

		fS = FirestoreClient.getFirestore();

		WelcomeScreen welcomeScreen = new WelcomeScreen(this);
		setScreen(welcomeScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		DocumentSnapshot players = null;
		try {
			players = Game.fS.collection("servers").document("server").get().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		ArrayList<String> playerNames = new ArrayList<>();
		assert players != null;
		if (players.exists()) {
			playerNames = (ArrayList<String>) players.getData().get("players");
			for(String name: playerNames){
				DocumentSnapshot tempPlayer = null;
				try {
					tempPlayer = Game.fS.collection("users").document(name).get().get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				if(name.equals(playScreen.getPlayer().getName())) playerNames.remove(name);
			}
			HashMap<String,ArrayList<String>> map = new HashMap();
			map.put("players",playerNames);
			Game.fS.collection("servers").document("server").set(map);
		} else {
			System.out.println("No such document!");
		}
		batch.dispose();
	}

	public void setPlayScreen(PlayScreen screen){
		this.playScreen=screen;
	}
}
