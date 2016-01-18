package com.vacster.castleflood.Stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.vacster.castleflood.CastleFlood;

import Actors.Background;
import Actors.Wall;

public class GameStage extends Stage implements GestureListener{

	private World world;
	private OrthographicCamera camera;
	@SuppressWarnings("unused")//lel
	private Box2DDebugRenderer dr;
	private Vector3 unprojecter = new Vector3();
	private Vector2 unprojecterHelper = new Vector2();
	private MouseJoint mouseJoint;
	private MouseJointDef mouseJointDef;
	private int width = 320, height = 180, positionIterations = 6, velocityIterations = 2;
	private float timeStep = 1f/60f;
	public Wall floor;
	public boolean paused = false, playing = false;
	public int defaultResources, currentResources;
	
	public GameStage(Skin skin, final CastleFlood game, SpriteBatch batch, World world, int level) {
		this.world = world;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);
		
		floor = newFloor();
		addActor(floor);//simplify
		addActor(new Wall(-30f, getHeight()/2, 2f, getHeight(), world));//simplify
		addActor(new Background(new Texture(Gdx.files.internal("runBackground.png"))));	//simplify
		getViewport().setCamera(camera);
		
		dr = new Box2DDebugRenderer();
		mouseJointDef = new MouseJointDef();
		mouseJointDef.bodyA = floor.body;
		mouseJointDef.collideConnected = true;//idk, but it was in the tutorial
		mouseJointDef.maxForce = 250000.0f;//fix if objects' density changes dramatically
	}
	
	public void restart(){
		getActors().clear();
		Array<Body> bodies = new Array<Body>();
		if(mouseJoint != null){
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		world.getBodies(bodies);
		for(Body bd : bodies)
			world.destroyBody(bd);

		currentResources = defaultResources;
		floor = newFloor();//simplify
		addActor(floor);//simplify
		addActor(new Wall(-30f, getHeight()/2, 2f, getHeight(), world));//simplify
		addActor(new Background(new Texture(Gdx.files.internal("runBackground.png"))));	//simplify
		camera.zoom = 1.0f;//hack
		checkBoundaries();
		mouseJointDef.bodyA = floor.body;
	}
	
	
	private Wall newFloor(){//hack
		return new Wall(getWidth()/2, 30f, getWidth(), 1f, world);
	}
	
	@Override
	public void act() {
		world.step(timeStep, velocityIterations, positionIterations);
		super.act();
	}
	
	@Override
	public void draw() {
		super.draw();
		//dr.render(world, camera.combined);
	}
	
	@Override
	public boolean zoom(float initialDistance, float distance) {
		if(paused || playing) return false;
		camera.zoom -= (distance-initialDistance)/10000;//hack?   
    	if(camera.zoom>=1.0f) camera.zoom=1.0f;//hack
    	if(camera.zoom<=0.3f) camera.zoom=0.3f;//hack
		checkBoundaries();
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if(paused || playing) return false;
		camera.translate(camera.zoom*(deltaX/2f), -camera.zoom*(deltaY/2f));
		checkBoundaries();
		return true;
	}
	
	private void checkBoundaries(){
		float halfViewPortWidth = (width/2)*camera.zoom,
				halfViewPortHeight = (height/2)*camera.zoom;
		if(getCamera().position.x-halfViewPortWidth < 0) getCamera().position.x = halfViewPortWidth;
		if(getCamera().position.y-halfViewPortHeight < 0) getCamera().position.y = halfViewPortHeight;
		if(getCamera().position.x+halfViewPortWidth > width) getCamera().position.x = width - halfViewPortWidth;
		if(getCamera().position.y+halfViewPortHeight > height) getCamera().position.y = height - halfViewPortHeight;
	}
	
	QueryCallback query = new QueryCallback(){
		@Override
		public boolean reportFixture(Fixture fixture) {
			if(!fixture.testPoint(unprojecter.x, unprojecter.y))
				return true;
		
			if(fixture.getBody() != floor.body){
				mouseJointDef.bodyB = fixture.getBody();
				mouseJointDef.target.set( unprojecter.x, unprojecter.y);//Previously: fixture.getBody().getPosition().x, fixture.getBody().getPosition().y
				mouseJoint = (MouseJoint) world.createJoint(mouseJointDef);
				return false;
			}
			return true;
		}
	};

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		if(paused || playing) return false;
		camera.unproject(unprojecter.set(x, y, 0));
		world.QueryAABB(query, unprojecter.x, unprojecter.y, unprojecter.x, unprojecter.y);
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(mouseJoint == null || paused || playing)
			return false;
		
		destroyMouseJoint();
		return true;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(mouseJoint == null)
			return false;

		camera.unproject(unprojecter.set(screenX, screenY, 0));
		mouseJoint.setTarget(unprojecterHelper.set(unprojecter.x, unprojecter.y));
		return true;
	}
	public void destroyMouseJoint() {
		if(mouseJoint != null){			
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	public void updateResources(int resources) {
		defaultResources = resources;
		currentResources = defaultResources;
	}
}
