package Screens;

import Game.Game;
import Objects2d.PolygonalCollision;
import Objects2d.RectCollision;
import Scenes.Hud;
import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class PlayScreen implements Screen {
    private final Game game;
    private final OrthographicCamera camera;
    private final Viewport gamePort;
    private final Hud hud;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    public int frameRate = 0;
    public int updateCount = 0;
    //physics world
    private final World world;
    private final Box2DDebugRenderer b2dr;
    private final Player player;
    private ArrayList<Player> otherPlayers = new ArrayList<>();
    private ArrayList<String> otherPlayersNames = new ArrayList<>();

    //textures
    private final TextureAtlas atlas;

    public PlayScreen(Game game, String playerName) throws ExecutionException, InterruptedException {
        this.game = game;
        atlas = new TextureAtlas("characters/bowGirl/bow-girl-movement.pack");
        //camera and gamePort for whole world
        camera = new OrthographicCamera();
        gamePort = new FitViewport(((float) Game.V_WIDTH)/ Game.PPM, ((float) Game.V_HEIGHT)/ Game.PPM,camera);
        this.resize(Game.V_WIDTH, Game.V_HEIGHT);
        //hud
        hud = new Hud(game.batch);
        //map
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/castleMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1.0f/ Game.PPM);
        //set camera to center of map
        camera.position.set(map.getProperties().get("width", Integer.class)*16f/ Game.PPM,map.getProperties().get("height", Integer.class)*16f/ Game.PPM,0);
        //physics world
        world = new World(new Vector2(0,0),true);
        b2dr = new Box2DDebugRenderer();

        generateStaticCollisions();

        player = new Player(this);
        player.setName(playerName);
        Game.fS.collection("users").document(player.getName()).set(player.getMap());

        DocumentSnapshot players = Game.fS.collection("servers").document("server").get().get();
        ArrayList<String> playerNames = new ArrayList<>();
        if (players.exists()) {
            playerNames = (ArrayList<String>) players.getData().get("players");
            for(String name: playerNames){
                DocumentSnapshot tempPlayer = Game.fS.collection("users").document(name).get().get();
                if(!name.equals(player.getName())) {
                    otherPlayersNames.add(name);
                    otherPlayers.add(new Player(this, (HashMap<String, Object>) Objects.requireNonNull(tempPlayer.getData())));
                }
            }
            if(!playerNames.contains(player.getName())) playerNames.add(player.getName());
            Game.fS.collection("servers").document("server").update("players",playerNames);
        } else {
            System.out.println("No such document!");
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        try {
            update(delta);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //tiledMap render
        int[] layers = {0,1,3};
        renderer.render(layers);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Player pl: otherPlayers){
            pl.draw(game.batch);
        }
        game.batch.end();

        layers = new int[]{2, 4};
        renderer.render(layers);

        //render physics world (boundaries)
        b2dr.render(world,camera.combined);

        //draw Hud (do not draw what hud camera sees)
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
    }

    public void update(float dt) throws ExecutionException, InterruptedException {
        handleInput(dt);
        playerCorrectMovement();

        world.step(1/60f,6,2);

        //if server players list changed, change otherPlayers list; if some of users whose names are in server list
        //changed, update their maps.
        updateCount++;
        if(updateCount==0){
            DocumentSnapshot players = Game.fS.collection("servers").document("server").get().get();
            ArrayList<String> playerNames = new ArrayList<>();
            if (players.exists()) {
                playerNames = (ArrayList<String>) players.getData().get("players");
                for(String name: playerNames){
                    DocumentSnapshot tempPlayer = Game.fS.collection("users").document(name).get().get();
                    if(!name.equals(player.getName())){
                        if(!otherPlayersNames.contains("name")) otherPlayers.add(new Player(this, (HashMap<String, Object>) Objects.requireNonNull(tempPlayer.getData())));
                        //else otherPlayers
                    }
                }
                if(!playerNames.contains(player.getName())) playerNames.add(player.getName());
                Game.fS.collection("servers").document("server").update("players",playerNames);
            } else {
                System.out.println("No such document!");
            }
        } else if(updateCount>=600) updateCount =0;

        playerRestrictFromBorders();
        player.update(dt);
        for(Player pl: otherPlayers){
            pl.update(dt);
        }
        handleCamera();

        camera.update();
        renderer.setView(camera);
        frameRate = Gdx.graphics.getFramesPerSecond();
        hud.fpsLabel.setText(String.format("FPS: %02d",frameRate));
        //System.out.println(frameRate);
    }

    private void handleInput(float dt){
        //handle arrow keys and shift
        player.b2Body.setLinearVelocity(0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            player.b2Body.setLinearVelocity(1,0);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            player.b2Body.setLinearVelocity(-1+player.b2Body.getLinearVelocity().x,0);
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            player.b2Body.setLinearVelocity(player.b2Body.getLinearVelocity().x,1);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            player.b2Body.setLinearVelocity(player.b2Body.getLinearVelocity().x,-1+player.b2Body.getLinearVelocity().y);
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            player.velocity =10;
        } else {
            player.velocity =5;
        }
        if(Gdx.input.justTouched()){
            System.out.println(player.b2Body.getPosition());
            sendMessage();
        }
    }

    private void sendMessage() {
        //Game.fS.collection("users").document("UENO").update(player.getMap());
    }

    private void playerCorrectMovement(){
        player.b2Body.setLinearVelocity(player.b2Body.getLinearVelocity().nor());
        player.b2Body.setLinearVelocity(player.velocity *player.b2Body.getLinearVelocity().x,player.velocity *player.b2Body.getLinearVelocity().y);
    }

    private void playerRestrictFromBorders(){
        player.b2Body.setTransform(Math.min(player.b2Body.getPosition().x,map.getProperties().get("width", Integer.class)*32f/ Game.PPM-8/ Game.PPM),
                Math.min(player.b2Body.getPosition().y,map.getProperties().get("height", Integer.class)*32f/ Game.PPM-16/ Game.PPM),0);
        player.b2Body.setTransform(Math.max(player.b2Body.getPosition().x,0+8/ Game.PPM),
                Math.max(player.b2Body.getPosition().y,0+16/ Game.PPM),0);
    }

    private void handleCamera(){
        camera.position.x=player.b2Body.getPosition().x;
        camera.position.y=player.b2Body.getPosition().y;
        camera.position.x=Math.min(camera.position.x,map.getProperties().get("width", Integer.class)*32f/ Game.PPM-camera.viewportWidth/2);
        camera.position.y=Math.min(camera.position.y,map.getProperties().get("height", Integer.class)*32f/ Game.PPM-camera.viewportHeight/2);
        camera.position.x=Math.max(camera.position.x,0+camera.viewportWidth/2);
        camera.position.y=Math.max(camera.position.y,0+camera.viewportHeight/2);
    }

    private void generateStaticCollisions(){
        for(MapObject obj: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new RectCollision(obj,world);
        }
        for(MapObject obj: map.getLayers().get(5).getObjects().getByType(PolygonMapObject.class)){
            new PolygonalCollision(obj,world);
        }
    }

    public World getWorld() {
        return world;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public Player getPlayer(){
        return this.player;
    }
}
