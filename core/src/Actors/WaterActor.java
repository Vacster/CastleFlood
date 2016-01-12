package Actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class WaterActor extends Image{
	
	private Body body;//public tmp
	
	public WaterActor(float posX, float posY, FixtureDef fixtureDef, BodyDef bodyDef, CircleShape shape, Sprite sprite, World world) {
		bodyDef.position.set(posX, posY);
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		SpriteDrawable draw = new SpriteDrawable(sprite);
		setDrawable(draw);
		setHeight(sprite.getHeight());
		setWidth(sprite.getWidth());
		body.applyLinearImpulse(body.getWorldCenter(), body.getWorldCenter(), true);
	}
	
	@Override
	public void act(float delta) {
		setPosition(body.getWorldCenter().x-8, body.getWorldCenter().y-4f);//hack
		super.act(delta);
	}

}
