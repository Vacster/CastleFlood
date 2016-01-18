package Actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vacster.castleflood.Box2DDefinition;

public class Box2DObject extends Actor{
	
	private Body body;
	private Sprite sprite;
	
	public Box2DObject(final Box2DDefinition definition) {
		body = definition.getWorld().createBody(definition.getBodyDef());
		body.createFixture(definition.getFixtureDef());
		body.setBullet(false);
		sprite = new Sprite(definition.getSprite());
		sprite.setSize(definition.getWidth(), definition.getHeight());
		sprite.setCenter(body.getLocalCenter().x, body.getLocalCenter().y);
		sprite.setOriginCenter();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.draw(batch);
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void act(float delta) {
		sprite.setPosition(body.getPosition().x - (sprite.getWidth()/2), body.getPosition().y - (sprite.getHeight()/2));//expensive shit - fix somehow
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));//expensive...?
		super.act(delta);
	}
}
