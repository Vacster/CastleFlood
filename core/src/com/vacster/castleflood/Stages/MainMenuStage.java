package com.vacster.castleflood.Stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vacster.castleflood.CastleFlood;

public class MainMenuStage extends Stage{
	
	private TextButton quitButton, startButton;
	private Table table;
	private float buttonWidth = getWidth()/2.0f, buttonHeight = getHeight()/6.0f, fontScale = getWidth()/1920;
	
	public MainMenuStage(Skin skin, final CastleFlood game) {
		startButton = new TextButton("Start", skin);
		startButton.getLabel().setFontScale(fontScale);
		startButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.changeStage(STAGE.LEVELSELECT);
				super.clicked(event, x, y);
			}
		});
		
		quitButton = new TextButton("Quit", skin);
		quitButton.getLabel().setFontScale(fontScale);
		quitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
				super.clicked(event, x, y);
			}
		});
		
		table = new Table();
		table.setWidth(getWidth());
		table.setHeight(getHeight());
		table.add(startButton).size(buttonWidth, buttonHeight);
		table.row().padTop(buttonHeight/2);
		table.add(quitButton).size(buttonWidth, buttonHeight);
		
		addActor(table);
	}
}
