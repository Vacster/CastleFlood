package Actors;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Wall extends Actor{//ew

	public Body body;
	
	public Wall(float posX, float posY, float width, float height, World world) {
		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		final Body body;
		
		bodyDef.position.set(posX, posY);
		bodyDef.type = BodyType.KinematicBody;
		
		shape.setAsBox(width, height);
		body = world.createBody(bodyDef);
		body.createFixture(shape, 1.0f);
		this.body = body;
	}
}
