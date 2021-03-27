package Objects2d;

import Game.Game;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PolygonalCollision {
    public PolygonalCollision(MapObject object, World world){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        Polygon pol = ((PolygonMapObject)object).getPolygon();
        pol.setScale(1/ Game.PPM,1/ Game.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(pol.getX()/ Game.PPM,(pol.getY()/ Game.PPM));
        body = world.createBody(bdef);
        Vector2[] vertices = new Vector2[pol.getVertices().length/2];
        for(int i = 0; i <pol.getVertices().length/2;i++){
            vertices[vertices.length-i-1] = new Vector2(pol.getVertices()[2*i]/ Game.PPM,pol.getVertices()[2*i+1]/ Game.PPM);
        }
        shape.set(vertices);
        fdef.shape = shape;
        body.createFixture(fdef);
    }
}
