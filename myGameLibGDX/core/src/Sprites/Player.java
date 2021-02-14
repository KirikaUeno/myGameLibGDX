package Sprites;

import Game.PlayField;
import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite {
    private final World world;
    public Body b2Body;
    public float velocity =2;

    public enum State {WALKING,STANDING,RUNNING}
    public State currentState;
    public State previousState;
    private final TextureRegion bowGirlStandLeft;
    private final TextureRegion bowGirlStandDown;
    private final TextureRegion bowGirlStandUp;
    private final TextureRegion bowGirlStandUpLeft;
    private final TextureRegion bowGirlStandDownLeft;
    private final Animation<TextureRegion> bowGirlWalkLeft;
    private final Animation<TextureRegion> bowGirlWalkDown;
    private final Animation<TextureRegion> bowGirlWalkUp;
    private final Animation<TextureRegion> bowGirlWalkUpLeft;
    private final Animation<TextureRegion> bowGirlWalkDownLeft;
    private final Animation<TextureRegion> bowGirlRunLeft;
    private final Animation<TextureRegion> bowGirlRunDown;
    private final Animation<TextureRegion> bowGirlRunUp;
    private final Animation<TextureRegion> bowGirlRunUpLeft;
    private final Animation<TextureRegion> bowGirlRunDownLeft;
    private float stateTimer;
    private enum Direction {UP,LEFT,DOWN,RIGHT,UP_LEFT,UP_RIGHT,DOWN_LEFT,DOWN_RIGHT}
    private boolean isLeft;
    private Direction direction;
    private Direction previousDirection;

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
        setBounds(0,0,16/PlayField.PPM,32/PlayField.PPM);
    }

    public void definePlayer(){
        BodyDef bDef = new BodyDef();
        bDef.position.set(350/ PlayField.PPM,100/PlayField.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2Body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        Vector2 ve = new Vector2(0,-8/PlayField.PPM);
        shape.setAsBox(8/PlayField.PPM,8/PlayField.PPM,ve,0);
        fDef.shape=shape;
        fDef.friction=0;
        b2Body.createFixture(fDef);
        System.out.println(fDef.friction);
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
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region = bowGirlStandDown;
        switch (currentState){
            case WALKING:
                direction=getDirection();
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
                direction=getDirection();
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
}
