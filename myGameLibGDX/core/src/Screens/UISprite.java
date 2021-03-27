package Screens;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UISprite extends Sprite {
    WelcomeScreen screen;
    public TextureRegion cursor;
    public TextureRegion background;
    public TextureRegion selection;

    public UISprite(WelcomeScreen screen){
        super(screen.getAtlas().findRegion("textField"));
        this.screen=screen;

        cursor = new TextureRegion(getTexture(),800,0,32,32);
        background = new TextureRegion(getTexture(),400,0,400,100);
        selection = new TextureRegion(getTexture(),0,0,400,100);
    }
}
