package com.vacster.castleflood;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DDefinition {
	
	private FixtureDef fixtureDef;
	private BodyDef bodyDef;
	private World world;
	private PolygonShape shape;
	private Sprite sprite;
	private float width, height;
	
	public Box2DDefinition(float[] values, PolygonShape shape, Sprite sprite, World world) {
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		fixtureDef.density = values[4];
		fixtureDef.friction = values[5];
		fixtureDef.restitution = values[6];
		fixtureDef.shape = shape;
		
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(values[2], values[3]));
		
		this.shape = shape;
		this.sprite = new Sprite(sprite);
		this.sprite.setSize(width,  height);
		this.width = values[0];
		this.height = values[1];
		this.world = world;
	}
	
	public BodyDef getBodyDef() {
		return bodyDef;
	}
	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}
	public PolygonShape getShape() {
		return shape;
	}
	public World getWorld() {
		return world;
	}
	public float getHeight() {
		return height;
	}
	public float getWidth() {
		return width;
	}
	public Sprite getSprite() {
		return sprite;
	}
}
