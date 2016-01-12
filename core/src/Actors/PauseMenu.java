package Actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PauseMenu extends Actor{//TODO: could be image, even done completely in HUDStage

	Sprite sprite;
	
	public PauseMenu(float width, float height, Sprite sprite) {
		this.sprite = new Sprite(sprite);
		this.sprite.setPosition(50f,35f);
		this.sprite.setSize(width, height);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.draw(batch);
	}
	
}
