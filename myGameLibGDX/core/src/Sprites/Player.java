package Sprites;

import Game.Game;
import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Player extends Sprite {
    private String name = "player";

    private final World world;
    public Body b2Body;
    public float velocity =2;

    public enum State {WALKING,STANDING,RUNNING}
    public State currentState;
    public State previousState;
    private TextureRegion bowGirlStandLeft;
    private TextureRegion bowGirlStandDown;
    private TextureRegion bowGirlStandUp;
    private TextureRegion bowGirlStandUpLeft;
    private TextureRegion bowGirlStandDownLeft;
    private Animation<TextureRegion> bowGirlWalkLeft;
    private Animation<TextureRegion> bowGirlWalkDown;
    private Animation<TextureRegion> bowGirlWalkUp;
    private Animation<TextureRegion> bowGirlWalkUpLeft;
    private Animation<TextureRegion> bowGirlWalkDownLeft;
    private Animation<TextureRegion> bowGirlRunLeft;
    private Animation<TextureRegion> bowGirlRunDown;
    private Animation<TextureRegion> bowGirlRunUp;
    private Animation<TextureRegion> bowGirlRunUpLeft;
    private Animation<TextureRegion> bowGirlRunDownLeft;
    private float stateTimer;
    private enum Direction {UP,LEFT,DOWN,RIGHT,UP_LEFT,UP_RIGHT,DOWN_LEFT,DOWN_RIGHT}
    private boolean isLeft;
    private Direction direction;
    private Direction previousDirection;
    private boolean isMainPlayer = true;

    public Player(PlayScreen screen){
        super(screen.getAtlas().findRegion("bowGirl"));
        this.world = screen.getWorld();
        definePlayer();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        direction = Direction.DOWN;
        previousDirection = direction;
        isLeft=true;

        defineAnimations();

        setBounds(0,0,16/ Game.PPM,32/ Game.PPM);
    }

    public Player(PlayScreen screen, HashMap<String,Object> map){
        super(screen.getAtlas().findRegion("bowGirl"));
        this.world = screen.getWorld();
        definePlayer();

        stateTimer = 0;
        isLeft=true;

        direction = Direction.valueOf((String)map.get("direction"));
        b2Body.setTransform(Float.parseFloat((String)map.get("posX")),Float.parseFloat((String)map.get("posY")),0);
        System.out.println((String)map.get("posX"));
        System.out.println((String)map.get("posY"));
        System.out.println(b2Body.getPosition().toString());
        currentState = State.valueOf((String)map.get("state"));
        previousState = currentState;
        previousDirection = direction;
        if(direction==Direction.DOWN_RIGHT||direction==Direction.UP_RIGHT||direction==Direction.RIGHT) isLeft=false;

        defineAnimations();

        setBounds(0,0,16/ Game.PPM,32/ Game.PPM);

        isMainPlayer = false;
    }

    private void definePlayer(){
        BodyDef bDef = new BodyDef();
        bDef.position.set(350/ Game.PPM,100/ Game.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2 ve = new Vector2(0,-8/ Game.PPM);
        shape.setAsBox(8/ Game.PPM,8/ Game.PPM,ve,0);
        fDef.shape=shape;
        fDef.friction=0;
        b2Body.createFixture(fDef);
    }

    private void defineAnimations(){
        Array<TextureRegion> frames = new Array<>();
        for(int i=0;i<3;i++){
            frames.add(new TextureRegion(getTexture(),i*16,0,16,32));
        }
        bowGirlWalkDown = new Animation<>(0.2f,frames);
        bowGirlRunDown = new Animation<>(0.1f,frames);
        frames.clear();
        for(int i=3;i<6;i++){
            frames.add(new TextureRegion(getTexture(),i*16,0,16,32));
        }
        bowGirlWalkDownLeft = new Animation<>(0.2f,frames);
        bowGirlRunDownLeft = new Animation<>(0.1f,frames);
        frames.clear();
        for(int i=0;i<3;i++){
            frames.add(new TextureRegion(getTexture(),i*16,32,16,32));
        }
        bowGirlWalkUp = new Animation<>(0.2f,frames);
        bowGirlRunUp = new Animation<>(0.1f,frames);
        frames.clear();
        for(int i=0;i<3;i++){
            frames.add(new TextureRegion(getTexture(),i*16,64,16,32));
        }
        bowGirlWalkLeft = new Animation<>(0.2f,frames);
        bowGirlRunLeft = new Animation<>(0.1f,frames);
        frames.clear();
        for(int i=3;i<6;i++){
            frames.add(new TextureRegion(getTexture(),i*16,64,16,32));
        }
        bowGirlWalkUpLeft = new Animation<>(0.2f,frames);
        bowGirlRunUpLeft = new Animation<>(0.1f,frames);
        frames.clear();

        bowGirlStandDown = new TextureRegion(getTexture(),16,0,16,32);
        bowGirlStandDownLeft = new TextureRegion(getTexture(),64,0,16,32);
        bowGirlStandUp = new TextureRegion(getTexture(),16,32,16,32);
        bowGirlStandLeft = new TextureRegion(getTexture(),16,64,16,32);
        bowGirlStandUpLeft = new TextureRegion(getTexture(),64,64,16,32);
    }

    public void update(float dt){
        setPosition(b2Body.getPosition().x-this.getWidth()/2,b2Body.getPosition().y-this.getHeight()/2);
        setRegion(getFrame(dt));

        if(currentState==previousState){
            stateTimer+=dt;
        } else {
            stateTimer=0;
        }
        previousState = currentState;
        previousDirection=direction;
        if(!isMainPlayer) System.out.println(b2Body.getPosition().toString());
    }

    public TextureRegion getFrame(float dt){
        if(isMainPlayer) currentState = getState();
        TextureRegion region = bowGirlStandDown;
        switch (currentState){
            case WALKING:
                if(isMainPlayer) direction=getDirection();
                switch (direction){
                    case UP:
                        region = bowGirlWalkUp.getKeyFrame(stateTimer,true);
                        break;
                    case DOWN:
                        region = bowGirlWalkDown.getKeyFrame(stateTimer,true);
                        break;
                    case LEFT:
                    case RIGHT:
                        region = bowGirlWalkLeft.getKeyFrame(stateTimer,true);
                        break;
                    case UP_LEFT:
                    case UP_RIGHT:
                        region = bowGirlWalkUpLeft.getKeyFrame(stateTimer,true);
                        break;
                    case DOWN_LEFT:
                    case DOWN_RIGHT:
                        region = bowGirlWalkDownLeft.getKeyFrame(stateTimer,true);
                        break;
                }
                break;
            case RUNNING:
                if(isMainPlayer) direction=getDirection();
                switch (direction){
                    case UP:
                        region = bowGirlRunUp.getKeyFrame(stateTimer,true);
                        break;
                    case DOWN:
                        region = bowGirlRunDown.getKeyFrame(stateTimer,true);
                        break;
                    case LEFT:
                    case RIGHT:
                        region = bowGirlRunLeft.getKeyFrame(stateTimer,true);
                        break;
                    case UP_LEFT:
                    case UP_RIGHT:
                        region = bowGirlRunUpLeft.getKeyFrame(stateTimer,true);
                        break;
                    case DOWN_LEFT:
                    case DOWN_RIGHT:
                        region = bowGirlRunDownLeft.getKeyFrame(stateTimer,true);
                        break;
                }
                break;
            case STANDING:
                switch (previousDirection){
                    case UP:
                        region = bowGirlStandUp;
                        break;
                    case DOWN:
                        region = bowGirlStandDown;
                        break;
                    case LEFT:
                    case RIGHT:
                        region = bowGirlStandLeft;
                        break;
                    case UP_LEFT:
                    case UP_RIGHT:
                        region = bowGirlStandUpLeft;
                        break;
                    case DOWN_LEFT:
                    case DOWN_RIGHT:
                        region = bowGirlStandDownLeft;
                        break;
                }
                break;
        }
        if(!isLeft && !region.isFlipX()) region.flip(true,false);
        else if(isLeft && region.isFlipX()) region.flip(true,false);
        return region;
    }

    private State getState() {
        if(b2Body.getLinearVelocity().x==0 && b2Body.getLinearVelocity().y==0){
            return State.STANDING;
        } else if(velocity<6){
            return State.WALKING;
        } else {
            return State.RUNNING;
        }
    }

    private Direction getDirection(){
        Vector2 v = b2Body.getLinearVelocity();
        this.isLeft= !(v.x > 0);
        if(v.x>0 && v.y==0) return Direction.RIGHT;
        else if(v.x<0 && v.y==0) return Direction.LEFT;
        else if(v.x==0 && v.y>0) return Direction.UP;
        else if(v.x==0 && v.y<0) return Direction.DOWN;
        else if(v.x>0 && v.y>0) return Direction.UP_RIGHT;
        else if(v.x>0 && v.y<0) return Direction.DOWN_RIGHT;
        else if(v.x<0 && v.y>0) return Direction.UP_LEFT;
        else if(v.x<0 && v.y<0) return Direction.DOWN_LEFT;
        else return Direction.DOWN;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public HashMap<String,Object> getMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("direction",this.direction);
        map.put("posX",b2Body.getPosition().x);
        map.put("posY",b2Body.getPosition().y);
        map.put("state",this.currentState);
        return map;
    }
}
