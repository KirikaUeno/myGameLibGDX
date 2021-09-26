package Screens;

import Game.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class WelcomeScreen implements Screen {
    private Viewport welcomePort;
    private Stage stage;
    private Game game;
    private TextField nameInput;
    private final TextureAtlas atlas;
    private String playerName = "";

    public WelcomeScreen(Game game){
        this.game=game;
        atlas = new TextureAtlas("ui/ui.pack");

        welcomePort=new FitViewport(Game.V_WIDTH,Game.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(welcomePort,game.batch);

        UISprite ui = new UISprite(this);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.BLACK);

        TextureRegionDrawable cursor = new TextureRegionDrawable(ui.cursor);
        TextureRegionDrawable selection = new TextureRegionDrawable(ui.selection);
        TextureRegionDrawable background = new TextureRegionDrawable(ui.background);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(new BitmapFont(),Color.WHITE, cursor,selection,background);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        final Label welcomeLabel = new Label("Welcome to the 'game by SanaFan', "+playerName+"!\nPress LKM",font);
        nameInput = new TextField("", textFieldStyle);
        //table.add(nameInput).expandX();
        table.add(welcomeLabel).expandX();
        welcomeLabel.setVisible(false);

        stage.addActor(table);

        Gdx.input.getTextInput(new Input.TextInputListener() {
            @Override
            public void input(String text) {
                playerName = text;
                welcomeLabel.setText("Welcome to the 'game by SanaFan', "+playerName+"!\nPress LKM");
                welcomeLabel.setVisible(true);
            }

            @Override
            public void canceled() {

            }
        },"Character name","","your nickname");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        try {
            update();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    private void update() throws ExecutionException, InterruptedException {
        if(Gdx.input.justTouched()){
            if(!(playerName.equals(""))) {
                System.out.println("starting the game!\nyour name: " + playerName);
                PlayScreen screen = null;
                try {
                    screen = new PlayScreen(game,playerName);
                } catch (IOException e) {
                    System.out.println("no server connection!");
                }
                game.setPlayScreen(screen);
                game.setScreen(screen);
            }else{
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        playerName = text;
                    }

                    @Override
                    public void canceled() {

                    }
                },"Character name","","this field may not be empty!");
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
}
