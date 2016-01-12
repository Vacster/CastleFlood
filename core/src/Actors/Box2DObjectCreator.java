package Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.vacster.castleflood.Box2DDefinition;

public class Box2DObjectCreator extends Actor{

	private Sprite sprite;
	private int count;
	private int maxCount;
	private float posX;
	private BitmapFont font;
	public boolean paused = false, playing = false;

	public Box2DObjectCreator(float posX, final int maxCount, Sprite sprite, final Stage stage, final Box2DDefinition definition, OrthographicCamera camera) {
			this.sprite = sprite;
			setPosition(posX, 5f);//hack
			sprite.setPosition(posX, 5f);//hack
			setTouchable(Touchable.enabled);
			this.maxCount = maxCount;
			count = maxCount;
			addListener(new InputListener(){
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if(count>0 && !paused && !playing){
						stage.addActor(new Box2DObject(definition));
						count--;
						return true;
					}
					return false;
				}
			});
			font = new BitmapFont(Gdx.files.internal("fontSmall.fnt"));//change to more visible one
			this.posX = posX+6f;
	}
	
	public void reset(){
		count = this.maxCount;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.draw(batch);
		font.draw(batch, Integer.toString(count), posX, 12f, 8, Align.center, false); //hack values
	}
}
