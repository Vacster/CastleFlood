package com.vacster.castleflood.Stages;

import java.util.ArrayList;
import java.util.HashMap;	

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vacster.castleflood.Box2DDefinition;
import com.vacster.castleflood.CastleFlood;
import com.vacster.castleflood.LevelLoader;

import Actors.Background;
import Actors.Box2DObjectCreator;
import Actors.Lock;
import Actors.PauseMenu;
import Actors.WaterActor;

public class HUDStage extends Stage{


	private float locations[];
	private OrthographicCamera camera;
	private HashMap<String, Sprite> sprites;//could probably be <int, obj>
	private HashMap<String, PolygonShape> shapes;//could probably be <int, obj>
	private HashMap<String, Box2DDefinition> definitions;//could probably be <int, obj>
	private HashMap<String, Integer> items;//could probably be <int, obj>
	private ArrayList<String> creators;//if others change this must too
	private ArrayList<Box2DObjectCreator> creatorObjects;
	private ArrayList<WaterActor> water = new ArrayList<WaterActor>();
	private Table table;
	private String concreteBox  = "concreteBox", concreteTriangle = "concreteTriangle",//could be ints
			woodenBox = "woodenBox", goldBox = "goldBox", steelBox = "steelBox", rock = "rock",
			waterStr = "water", stick = "stick", bronze = "bronze", iron = "iron";
	private int currLocation = 0;
	private float fontScale = 0.15f;//hack
	private Group menuGroup, pauseGroup;
	private Skin skin;
	private CastleFlood CFGame;
	private GameStage gamestage;
	private World world;
	private FixtureDef waterFixtureDef;
	private BodyDef waterBodyDef;
	private CircleShape waterShape;
	private Sprite waterSpr;
	public boolean paused = false, playing = false;
	
	public HUDStage(Skin skin, final CastleFlood game, final World world, int currentLevel, final GameStage gameStage) {
		//optimization: only create 1 box2dobject as box and copy it for every new one
		this.skin = skin;
		this.world = world;
		CFGame = game;
		gamestage = gameStage;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 320, 180);
		getViewport().setCamera(camera);

		addActor(new Background(new Texture(Gdx.files.internal("hud.png"))));

		table = new Table();
		table.setWidth(getWidth());
		table.setHeight(getHeight());

		creators = new ArrayList<String>();
		creators.add(stick);
		creators.add(rock);
		creators.add(woodenBox);
		creators.add(bronze);
		creators.add(iron);
		creators.add(concreteBox);
		creators.add(steelBox);
		creators.add(goldBox);
		creators.add(concreteTriangle);

		locations = setLocations();
		initLevel(currentLevel);
		initSprites();
		initShapes();
		initWater();
		setDefinitions(world);
		initCreators(gameStage);

		addActor(table);
		//setDebugAll(true);

		initMenuButtons(skin, game);
		table.addActor(pauseGroup);
		table.setZIndex(1000);
	}
	
	private void initMenuButtons(Skin skin, final CastleFlood game){
		
		menuGroup = new Group();
		menuGroup.addActor(new PauseMenu(210f, 110f, sprites.get("pauseMenu")));//own class because... reasons!
		
		TextButton resumeButton = new TextButton("RESUME", skin);
		resumeButton.getLabel().setFontScale(fontScale);
		resumeButton.setSize(120, 30);
		resumeButton.setPosition(100, 90);
		resumeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				handlePause();
			}
		});
		menuGroup.addActor(resumeButton);
		
		ImageButton restartButton = new ImageButton(skin, "menurestart");
		restartButton.setSize(50, 30);
		restartButton.setPosition(100, 50);
		restartButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				restart();
			}
		});
		menuGroup.addActor(restartButton);
		
		ImageButton quitButton = new ImageButton(skin, "menuquit");
		quitButton.setSize(50, 30);
		quitButton.setPosition(170, 50);
		quitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gamestage.restart();//hack
				game.changeStage(STAGE.LEVELSELECT);
			}
		});
		menuGroup.addActor(quitButton);
		
		pauseGroup = new Group();
		
		final HUDStage thisStage = this;//lmao
		ImageButton pauseButton = new ImageButton(skin, "menupause");
		pauseButton.setSize(22f, 22f);
		pauseButton.setPosition(0f,  158f);//hack
		pauseButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				thisStage.handlePause();
			}
		});
		pauseGroup.addActor(pauseButton);
		
		ImageButton playButton = new ImageButton(skin, "menuplaywater");
		playButton.setSize(22f, 22f);
		playButton.setPosition(298, 158f);
		playButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!playing)createWater();
			}
		});
		pauseGroup.addActor(playButton);
	}
	
	private void initWater() {
		waterSpr = new Sprite(new Texture(Gdx.files.internal("water.png")));
		waterSpr.setSize(20f, 20f);//hack
		waterShape = new CircleShape();
		waterFixtureDef = new FixtureDef();
		waterBodyDef = new BodyDef();
		
		waterShape.setRadius(waterSpr.getHeight()/5);
		waterBodyDef.type = BodyType.DynamicBody;
		waterFixtureDef.density = 6f;
		waterFixtureDef.friction = 0.1f;
		waterFixtureDef.restitution = 0.8f;
		waterFixtureDef.shape = waterShape;
	}
	
	private void createWater() {
		playing = true;
		gamestage.playing = true;
		for(Box2DObjectCreator obj : creatorObjects)
			obj.playing = true;
		
		int y = items.get(waterStr);
		for(int x = 0; x < y; x++)
			water.add(new WaterActor(10f, x+40f, waterFixtureDef, waterBodyDef, waterShape, waterSpr, world));
		
		for(WaterActor wat : water)
			gamestage.addActor(wat);
	}
	
	private void restart(){
		getActors().clear();
		water.clear();
		creatorObjects.clear();
		addActor(new Background(new Texture(Gdx.files.internal("hud.png"))));
		
		playing = false;
		gamestage.playing = false;
		table = new Table();
		table.setWidth(getWidth());
		table.setHeight(getHeight());
		initCreators(gamestage);
		addActor(table);
		
		initMenuButtons(skin, CFGame);
		//setDebugAll(true);
		
		gamestage.restart();
		
		handlePause();
	}
	
	public void handlePause(){
		paused = !paused;
		gamestage.paused = paused;
		for(Box2DObjectCreator obj : creatorObjects)
			obj.paused = paused;
		if(paused){
			table.addActor(menuGroup);
			table.removeActor(pauseGroup);
		}else{
			table.removeActor(menuGroup);
			table.addActor(pauseGroup);
		}
	}
	
	private void initCreators(Stage gameStage){
		creatorObjects = new ArrayList<Box2DObjectCreator>();
		
		int x = 0;
		for(String str : creators)
			if(items.get(str) > 0)
				addCreator(str, gameStage, x++);
		
		for(int y = x; y < 7; y++)
			addActor(new Lock(locations[y], sprites.get("lock")));
	}
	
	private void addCreator(String name, Stage stage, int location){
		Sprite spr = sprites.get(name);//fuck, really gotta change this     vvvvvvvvvvvvvvvvvvvvvvvvvv 
		Box2DObjectCreator act = new Box2DObjectCreator(locations[location]+(name.equals(stick)?4f:0f), items.get(name), spr, stage, definitions.get(name), camera);
		act.setBounds(act.getX(), act.getY(), spr.getWidth(), spr.getHeight());
		creatorObjects.add(act);
		table.addActor(act);
	}

	private float[] setLocations(){
		float array[] = {27f, 66f, 106f, 147f, 187f, 227f, 267f};//hack
		return array;
	}
	
	private float[] setValues(String box){
		//{WIDTH, HEIGHT, X, Y, DENSITY, FRICTION, RESTITUTION}
		switch(box){//kinda hack-ish hardcoded values 
		case "stick":
			float[] tmp0 = {7.5f, 19f, locations[currLocation++]+10f, 150f, 5f, 0.6f, 0.0f};
			return tmp0;
		case "rock":
			float[] tmp1 = {12.5f, 12.5f, locations[currLocation++]+10f, 150f, 5f, 0.6f, 0.0f};
			return tmp1;
		case "woodenBox":
			float[] tmp2 = {10f, 10f, locations[currLocation++]+10f, 150f, 5f, 0.6f, 0.0f};
			return tmp2;
		case "bronze":
			float[] tmp3 = {10f, 10f, locations[currLocation++]+10f, 150f, 5f, 0.6f, 0.0f};
			return tmp3;
		case "iron":
			float[] tmp8 = {10f, 10f, locations[currLocation++]+10f, 150f, 5f, 0.6f, 0.0f};
			return tmp8;
		case "concreteBox"://fix this ugly shit
			float[] tmp4 = {10f, 10f, locations[currLocation++]+10f, 150f, 10f, 0.6f, 0.0f};  
			return tmp4;
		case "steelBox"://holy fuck is this disgusting
			float[] tmp5 = {10f, 10f, locations[currLocation++]+10f, 150f, 15f, 0.6f, 0.0f};
			return tmp5;
		case "goldBox"://kill me
			float[] tmp6 = {10f, 10f, locations[currLocation++]+10f, 150f, 20f, 0.6f, 0.0f};
			return tmp6;
		case "concreteTriangle"://end my misery please
			float[] tmp7 = {10f, 10f, locations[currLocation++]+10f, 150f, 15f, 0.8f, 0.0f};
			return tmp7;
		default://ouch
			return null;
		}
	}
	
	private void setDefinitions(World world){//SIMPLIFY BY AAAAA LOOOTTTT
		definitions = new HashMap<String, Box2DDefinition>();
		
		for(String str : creators)
			if(items.get(str)>0)
				definitions.put(str, new Box2DDefinition(setValues(str), shapes.get(str), sprites.get(str), world));
	}
	
	private void initLevel(int level){
		items = new HashMap<String, Integer>();
		LevelLoader loader = new LevelLoader(level);
		items.put(stick, loader.stickAmount);
		items.put(rock, loader.rockAmount);
		items.put(woodenBox, loader.woodBoxAmount);
		items.put(bronze, loader.bronzeBoxAmount);
		items.put(iron, loader.ironBoxAmount);
		items.put(concreteBox, loader.concreteBoxAmount);
		items.put(steelBox, loader.steelBoxAmount);
		items.put(goldBox, loader.goldBoxAmount);
		items.put(concreteTriangle, loader.concreteTriangleAmount);
		items.put(waterStr, loader.waterAmount);
	}
	
	private void initSprites(){//improve
		sprites = new HashMap<String, Sprite>();
		
		Sprite stickSpr = new Sprite(new Texture(Gdx.files.internal("stick_wood.png")));
		stickSpr.setSize(10f, 20f);
		
		Sprite rockSpr = new Sprite(new Texture(Gdx.files.internal("rock.png")));
		rockSpr.setSize(20f, 20f);
		
		Sprite woodBoxSpr = new Sprite(new Texture(Gdx.files.internal("block_wood.png")));
		woodBoxSpr.setSize(20f, 20f);
		
		Sprite bronzeSpr = new Sprite(new Texture(Gdx.files.internal("block_bronze.png")));
		bronzeSpr.setSize(20f, 20f);
		
		Sprite ironSpr = new Sprite(new Texture(Gdx.files.internal("block_iron.png")));
		ironSpr.setSize(20f, 20f);
		
		Sprite concreteBoxSpr = new Sprite(new Texture(Gdx.files.internal("block_concrete.png")));
		concreteBoxSpr.setSize(20f, 20f);
		
		Sprite steelBoxSpr = new Sprite(new Texture(Gdx.files.internal("block_steel.png")));
		steelBoxSpr.setSize(20f, 20f);
		
		Sprite goldBoxSpr = new Sprite(new Texture(Gdx.files.internal("block_gold.png")));
		goldBoxSpr.setSize(20f, 20f);
		
		Sprite concreteTriangleSpr = new Sprite(new Texture(Gdx.files.internal("triangle_concrete.png")));
		concreteTriangleSpr.setSize(20f, 20f);
		
		sprites.put("lock", new Sprite(new Texture(Gdx.files.internal("lock.png"))));
		sprites.put("pauseMenu", new Sprite(new Texture(Gdx.files.internal("pauseMenu.png"))));
		sprites.put(stick, stickSpr);
		sprites.put(rock, rockSpr);
		sprites.put(woodenBox, woodBoxSpr);
		sprites.put(bronze, bronzeSpr);
		sprites.put(iron, ironSpr);
		sprites.put(concreteBox, concreteBoxSpr);
		sprites.put(steelBox, steelBoxSpr);
		sprites.put(goldBox, goldBoxSpr);
		sprites.put(concreteTriangle, concreteTriangleSpr);
	}
	
	private void initShapes(){//?
		shapes = new HashMap<String, PolygonShape>();

		PolygonShape boxShape = new PolygonShape();
		PolygonShape triangleShape = new PolygonShape();
		PolygonShape rockShape = new PolygonShape();
		PolygonShape stickShape = new PolygonShape();
		
		boxShape.setAsBox(5f,  5f);
		
		Vector2[] triangleVertices = new Vector2[3];
		triangleVertices[0] = new Vector2(5f, -5f);
		triangleVertices[1] = new Vector2(-5f, -5f);
		triangleVertices[2] = new Vector2(-5f, 5f);//funny hack for weird problem
		triangleShape.set(triangleVertices);
		
		Vector2[] rockVertices = new Vector2[8];
		rockVertices[0] = new Vector2(-1.5f, 6f);
		rockVertices[1] = new Vector2(-6.25f, 1.9f);
		rockVertices[2] = new Vector2(-6.25f, -3.45f);//LOL
		rockVertices[3] = new Vector2(-4.75f, -6.25f);
		rockVertices[4] = new Vector2(4.7f, -6.25f);
		rockVertices[5] = new Vector2(6.25f, -3.75f);
		rockVertices[6] = new Vector2(6.25f, 1.80f);
		rockVertices[7] = new Vector2(5f, 4.60f);
		rockShape.set(rockVertices);

		stickShape.setAsBox(3.75f, 9.5f);//lol

		shapes.put(stick, stickShape);
		shapes.put(rock, rockShape);
		shapes.put(woodenBox, boxShape);
		shapes.put(bronze, boxShape);
		shapes.put(iron, boxShape);
		shapes.put(concreteBox, boxShape);
		shapes.put(steelBox, boxShape);
		shapes.put(goldBox, boxShape);
		shapes.put(concreteTriangle, triangleShape);
	}
}
