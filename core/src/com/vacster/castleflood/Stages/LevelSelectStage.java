package com.vacster.castleflood.Stages;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.vacster.castleflood.CastleFlood;

import Actors.CustomTextButton;

public class LevelSelectStage extends Stage{
	
	private Table table, uiTable, levelTable;
	private ImageButton backButton;
	private Array<CustomTextButton> levelButtons;
	private float buttonWidth = getWidth()/8, buttonHeight = getHeight()/5, fontScale = getWidth()/1920, buttonSidePad = getWidth()/40, rowPad = getHeight()/10;
	public int newLevel = 0;
	
	public LevelSelectStage(Skin skin, final CastleFlood game, int currentLevel) {
		backButton = new ImageButton(skin);
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.changeStage(STAGE.MAINMENU);
				super.clicked(event, x, y);
			}
		});
		
		levelButtons = new Array<CustomTextButton>();//Hashtable<TexButton, Integer>?
		for(int x = 0; x < 10; x++){
			levelButtons.add(new CustomTextButton(""+(x+1), skin, this, game));
			levelButtons.get(x).getLabel().setFontScale(fontScale);
		}
		
		table = new Table();
		uiTable = new Table();
		levelTable = new Table();
		table.setWidth(getWidth());
		table.setHeight(getHeight());
		levelTable.setWidth(getWidth());
		levelTable.setHeight(getHeight());
		uiTable.setWidth(getWidth());
		uiTable.setHeight(getHeight());
		
		uiTable.add(backButton).width(buttonWidth).height(buttonWidth);
		uiTable.align(Align.topLeft);
		table.add(uiTable).expand().fill();
		table.row();
		table.add(levelTable).expand().fill();
		for(int x = 0; x < 10; x++){
			levelTable.add(levelButtons.get(x)).width(buttonWidth).height(buttonHeight).padRight(buttonSidePad).padLeft(buttonSidePad);
			if(x == 4) levelTable.row().padTop(rowPad);
		}
		
		addActor(table);
	}
}
