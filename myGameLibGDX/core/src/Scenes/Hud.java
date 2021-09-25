package Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import Game.Game;

public class Hud implements Disposable {
    public Stage stage;
    public Label fpsLabel;

    public Hud(SpriteBatch sb){
        Viewport hudPort = new FitViewport(Game.V_WIDTH, Game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(hudPort,sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        fpsLabel = new Label(String.format("FPS: %03d",0), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.add(fpsLabel).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
