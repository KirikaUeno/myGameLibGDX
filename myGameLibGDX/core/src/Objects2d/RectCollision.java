package Objects2d;

import Game.PlayField;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class RectCollision {
    public RectCollision(MapObject object, World world){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        Rectangle rect = ((RectangleMapObject)object).getRectangle();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.getX()+rect.width/2)/ PlayField.PPM,(rect.getY()+rect.height/2)/PlayField.PPM);
        body = world.createBody(bdef);

        shape.setAsBox(rect.width/2/PlayField.PPM,rect.height/2/PlayField.PPM);
        fdef.shape = shape;
        body.createFixture(fdef);
    }

}
