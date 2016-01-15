package Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import com.vacster.castleflood.Box2DDefinition;
import com.vacster.castleflood.Stages.GameStage;

public class Box2DObjectCreator extends Actor{

	private Sprite sprite;
	private BitmapFont font;
	private String costStr;
	private float posX;
	private int cost;

	public Box2DObjectCreator(float posX, float offSet, Sprite sprite, final GameStage stage, final Box2DDefinition definition, OrthographicCamera camera) {
			this.sprite = sprite;
			this.cost = definition.getCost();
			setPosition(posX+offSet, 5f);//hack
			sprite.setPosition(posX+offSet, 5f);//hack
			addListener(new InputListener(){
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if(!stage.paused && !stage.playing && stage.currentResources >= cost){
							stage.addActor(new Box2DObject(definition));
							stage.currentResources-=cost;
						return true;
					}
					return false;
				}
			});
			font = new BitmapFont(Gdx.files.internal("font/creatorFont.fnt"));//change to more visible one
			this.posX = posX+6f;//hack
			costStr = Integer.toString(cost);//optimization?
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.draw(batch);
		font.draw(batch, costStr, posX, 15f, 8, Align.center, false); //hack values
	}
}
