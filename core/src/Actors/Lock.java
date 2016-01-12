package Actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Lock extends Actor{

	Sprite sprite;
	
	public Lock(float posX, Sprite sprite) {
		this.sprite = new Sprite(sprite);
		this.sprite.setSize(18f, 18f);
		this.sprite.setPosition(posX, 5f);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.draw(batch);
	}
	
}
