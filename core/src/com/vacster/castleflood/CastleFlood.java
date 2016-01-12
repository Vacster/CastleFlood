package com.vacster.castleflood;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vacster.castleflood.Stages.GameStage;
import com.vacster.castleflood.Stages.HUDStage;
import com.vacster.castleflood.Stages.LevelSelectStage;
import com.vacster.castleflood.Stages.MainMenuStage;
import com.vacster.castleflood.Stages.STAGE;


public class CastleFlood extends ApplicationAdapter {
	
	private Skin uiskin;
	private Stage currentStage, mainMenuStage, levelSelectStage;
	private GameStage gameStage;
	private HUDStage hudStage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private World world;
	private boolean HUD = false;
	private static int currentLevel = 1;
	
	private int width = 320, height = 180;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(true, width, height);
		world = new World(new Vector2(0, -98f), true);
		
		uiskin = new Skin(Gdx.files.internal("uiskin.json"));
		
		changeStage(STAGE.MAINMENU);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(HUD && !hudStage.paused)currentStage.act();//hack
		
		batch.begin();
		currentStage.draw();
		if(HUD) {
			hudStage.draw();//less-hacky now
			hudStage.act();
		}
		batch.end();
	}
	
	public void changeStage(STAGE mainmenu){
		switch(mainmenu){
		case MAINMENU:
			HUD = false;
			mainMenuStage = new MainMenuStage(uiskin, this);
			currentStage = mainMenuStage;
			Gdx.input.setInputProcessor(currentStage);	
			break;
		case LEVELSELECT:
			HUD = false;
			levelSelectStage = new LevelSelectStage(uiskin, this, currentLevel);
			currentStage = levelSelectStage;
			Gdx.input.setInputProcessor(currentStage);	
			break;
		case GAME:
			HUD = true;
			gameStage = new GameStage(uiskin, this, batch, world, 1);
			hudStage = new HUDStage(uiskin,this, world, ((LevelSelectStage)levelSelectStage).newLevel, gameStage);
			GestureDetector gd = new GestureDetector((GestureListener) gameStage);
			InputMultiplexer im = new InputMultiplexer();
			im.addProcessor(gameStage);
			im.addProcessor(gd);
			im.addProcessor(hudStage);
			Gdx.input.setInputProcessor(im);
			currentStage = gameStage;
			break;
		}
	}
}
