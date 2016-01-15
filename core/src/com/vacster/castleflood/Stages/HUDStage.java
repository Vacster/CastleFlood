package com.vacster.castleflood.Stages;

import java.util.ArrayList;
import java.util.HashMap;	

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer.Random;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.vacster.castleflood.Box2DDefinition;
import com.vacster.castleflood.CastleFlood;
import com.vacster.castleflood.ItemLoader;
import com.vacster.castleflood.LevelLoader;

import Actors.Background;
import Actors.Box2DObjectCreator;
import Actors.Lock;
import Actors.PauseMenu;
import Actors.WaterActor;

public class HUDStage extends Stage{

	private OrthographicCamera camera;//changing this would be fucking horrible wow vvvvvv
	private HashMap<String, Sprite> sprites;//could probably be <int, obj>
	private HashMap<String, PolygonShape> shapes;//could probably be <int, obj>
	private HashMap<String, Box2DDefinition> definitions;//could probably be <int, obj>
	private HashMap<String, Boolean> items;//could probably be <int, obj>
	private ArrayList<String> creators;//if others change this must too
	private ArrayList<Box2DObjectCreator> creatorObjects;//all this ^^^^^^^^^^
	private ArrayList<WaterActor> water = new ArrayList<WaterActor>();
	private ItemLoader itemLoader;
	private Table table;
	private Group menuGroup, pauseGroup;
	private Skin skin;
	private CastleFlood CFGame;
	private GameStage gamestage;
	private World world;
	private FixtureDef waterFixtureDef;
	private BodyDef waterBodyDef;
	private CircleShape waterShape;
	private Sprite waterSpr;
	private Task createWaterTask;
	public boolean paused = false, playing = false;//state machine would be nice | can only be in one of three states, paused/playing/normal
	private int currLocation = 0, waterAmount = 0, levelLength = 60;
	public int currentCount;
	private float fontScale = 0.15f, numberFontScale = 0.3f, locations[];;//hack
	private String concreteBox  = "concreteBox", concreteTriangle = "concreteTriangle",//could be simplified.... somehow?
			woodenBox = "woodenBox", goldBox = "goldBox", steelBox = "steelBox", rock = "rock"
			, stick = "stick", bronze = "bronze", iron = "iron", rockPillar = "rockPillar";
	
	public HUDStage(Skin skin, final CastleFlood game, final World world, int currentLevel, final GameStage gameStage) {
		//optimization: only create 1 box2dobject as box and copy it for every new one
		this.skin = skin;
		this.world = world;
		CFGame = game;
		gamestage = gameStage;
		
		itemLoader = new ItemLoader();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 320, 180);
		
		getViewport().setCamera(camera);
		
		addActor(new Background(new Texture(Gdx.files.internal("hud.png"))));

		table = new Table();
		table.setWidth(getWidth());
		table.setHeight(getHeight());
		
		initStrings();
		locations = setLocations();
		initLevel(currentLevel);
		initSprites();
		initShapes();
		initWater();
		setDefinitions(world);
		initCreators(gameStage);
		
		addActor(table);
		//setDebugAll(true);

		final Timer time = new Timer();
		createWaterTask = new Task(){
			@Override
			public void run() {
				Gdx.app.log("Timer", Integer.toString(currentCount));
				if(!playing && !paused)
					currentCount--;
				if(currentCount == 50)//shitty tremble
					shake(time, 10);//fix god damn
				if(currentCount <= 0)
					createWater();
			}
		};
		setTimer();
		
		initMenuButtons(skin, game);
		table.addActor(pauseGroup);
		table.setZIndex(1000);//arbitrary number so it's always on top
	}
	
	private void shake(final Timer time, final int count){//Not convinced, new arraylist and new task always made
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		float randomY = MathUtils.random(5000, 10000);
		float randomX = MathUtils.random(-12000, 12000);
		for(Body b : bodies)
			if(b.getPosition().y < 60f)	
				b.applyLinearImpulse(randomX, randomY, b.getWorldCenter().x, b.getWorldCenter().y, true);
			
		time.scheduleTask(new Task(){
			@Override
			public void run() {
				if(count > 0)
					shake(time, reduceOne(count));//I still dont get this final stuff
			}
		}, 0.2f);
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
		
		Label label = new Label("Resources", skin){
			@Override
			public void draw(Batch batch, float parentAlpha) {
				setVisible(!playing);
				setText(Integer.toString(gamestage.currentResources));
				super.draw(batch, parentAlpha);
			}
		};
		label.setFontScale(numberFontScale);
		label.setPosition(45f, 142f);//hack
		label.setTouchable(Touchable.disabled);
		pauseGroup.addActor(label);
		
	}

	private void initStrings(){//kinda ugly / repetitive. let's make a json! lel. actually, that could work...? jsonvalue.getindex(int) ..?
		creators = new ArrayList<String>();//gonna need reference to the amount of strings in there... first line is amount!... seems ugly...?
		creators.add(stick);//                     jsonvalue.size exists/ what does it refer to exactly?
		creators.add(rock);//		JsonValue map = ...;
		creators.add(rockPillar);
		creators.add(woodenBox);//		 for (JsonValue entry = map.child; entry != null; entry = entry.next)
		creators.add(bronze); // supposedly more effective, but seems odd. entry = map.child...?
		creators.add(iron);
		creators.add(concreteBox);
		creators.add(steelBox);
		creators.add(goldBox);
		creators.add(concreteTriangle);
//	
	}
	
	private float[] setLocations(){
		float array[] = {27f, 66f, 106f, 147f, 187f, 227f, 267f};//hack
		return array;
	}
	
	private void initLevel(int level){
		items = new HashMap<String, Boolean>();
		LevelLoader loader = new LevelLoader(level, creators);
		for(int x = 0; x < creators.size(); x++){
			items.put(creators.get(x), loader.getAmount(x));
		}
		waterAmount = loader.getWater();
		gamestage.updateResources(loader.getResources());
	}
	
	private void initSprites(){
		sprites = new HashMap<String, Sprite>();
		sprites.put("lock", new Sprite(new Texture(Gdx.files.internal("lock.png"))));
		sprites.put("pauseMenu", new Sprite(new Texture(Gdx.files.internal("pauseMenu.png"))));
		for(int x = 0; x < creators.size(); x++){
			Sprite tmp = new Sprite(new Texture(Gdx.files.internal("sprites/"+creators.get(x)+".png")));
			tmp.setSize(setWidth(x), 20f);
			sprites.put(creators.get(x), tmp);
		}
	}
	
	private void initShapes(){//?
		shapes = new HashMap<String, PolygonShape>();

		PolygonShape boxShape = new PolygonShape();
		PolygonShape triangleShape = new PolygonShape();
		PolygonShape rockShape = new PolygonShape();
		PolygonShape stickShape = new PolygonShape();
		PolygonShape rockPillarShape = new PolygonShape();
		
		boxShape.setAsBox(5f,  5f);
		
		Vector2[] triangleVertices = new Vector2[3];
		triangleVertices[0] = new Vector2(5f, -5f);
		triangleVertices[1] = new Vector2(-5f, -5f);
		triangleVertices[2] = new Vector2(-5f, 5f);
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
		
		Vector2[] rockPillarVertices = new Vector2[8];
		rockPillarVertices[0] = new Vector2(0.25f, 14.5f);//tr
		rockPillarVertices[1] = new Vector2(1.6f, 14f);//tr
		rockPillarVertices[2] = new Vector2(3.5f, 13f);//tr
		rockPillarVertices[3] = new Vector2(5f, 7f);//tr
		rockPillarVertices[4] = new Vector2(5f, -15f);//br
		rockPillarVertices[5] = new Vector2(-5f, -15f);//bl
		rockPillarVertices[6] = new Vector2(-3.75f, 12.75f);//tl
		rockPillarVertices[7] = new Vector2(-5.0f, 1.0f);//tl
		rockPillarShape.set(rockPillarVertices);

		stickShape.setAsBox(3.75f, 9.5f);//lol
		
		shapes.put(stick, stickShape);//fuck all this
		shapes.put(rock, rockShape);
		shapes.put(rockPillar, rockPillarShape);
		shapes.put(woodenBox, boxShape);
		shapes.put(bronze, boxShape);
		shapes.put(iron, boxShape);
		shapes.put(concreteBox, boxShape);
		shapes.put(steelBox, boxShape);
		shapes.put(goldBox, boxShape);
		shapes.put(concreteTriangle, triangleShape);
	}
	
	private void initWater() {
		waterSpr = new Sprite(new Texture(Gdx.files.internal("water.png")));
		waterSpr.setSize(20f, 20f);//hack
		waterShape = new CircleShape();
		waterFixtureDef = new FixtureDef();
		waterBodyDef = new BodyDef();
		
		waterShape.setRadius(4f); //hack
		waterBodyDef.type = BodyType.DynamicBody;
		waterFixtureDef.density = 6f;
		waterFixtureDef.friction = 0.1f;
		waterFixtureDef.restitution = 0.8f;
		waterFixtureDef.shape = waterShape;
	}
	
	private void setDefinitions(World world){//that's hot
		definitions = new HashMap<String, Box2DDefinition>();
		
		for(String str : creators)
			if(items.get(str))
				definitions.put(str, new Box2DDefinition(setValues(str), shapes.get(str), sprites.get(str), world));
	}
	
	private void initCreators(GameStage gameStage){
		creatorObjects = new ArrayList<Box2DObjectCreator>();
		
		int x = 0;
		for(String str : creators)
			if(items.get(str))
				addCreator(str, gameStage, x++);
		
		for(int y = x; y < 7; y++)
			addActor(new Lock(locations[y], sprites.get("lock")));
	}
	
	private void createWater() {
		Timer.instance().clear();
		playing = true;
		gamestage.playing = true;
		for(int x = 0; x < waterAmount; x++)
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

		Timer.instance().clear();
		setTimer();
	}
	
	public void handlePause(){
		paused = !paused;
		gamestage.paused = paused;
		if(paused){
			table.addActor(menuGroup);
			table.removeActor(pauseGroup);
		}else{
			table.removeActor(menuGroup);
			table.addActor(pauseGroup);
		}
	}
	
	private void addCreator(String name, GameStage stage, int location){
		Sprite spr = sprites.get(name);
		Box2DObjectCreator act = new Box2DObjectCreator(locations[location], setOffset(name), spr, stage, definitions.get(name), camera);
		act.setBounds(act.getX(), act.getY(), spr.getWidth(), spr.getHeight());
		creatorObjects.add(act);
		table.addActor(act);
	}

	//helper functions below
	private float[] setValues(String box){
		return itemLoader.getValues(box, locations[currLocation++]+10f, 150f);
	}
	
	private float setWidth(int x){//Special cases
		switch(creators.get(x)){
		case "stick":
		case "rockPillar":
			return 10.0f;
		default:
			return 20.0f;
		}
	}

	private void setTimer(){
		currentCount = levelLength;
		Timer.schedule(createWaterTask, 1, 1, levelLength);
	}
	
	private float setOffset(String str){//Special cases | hack?
		switch(str){
		case "stick":
			return 4f;
		case "rockPillar":
			return 5f;
		default:
			return 0;
		}
	}
	
	private int reduceOne(int x){//fuck
		return x-1;
	}
}
