package Actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Background extends Actor{

	Texture txt;
	
	public Background(Texture text){
		txt = text;	
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(txt, 0, 0, 320, 180);
		super.draw(batch, parentAlpha);
	}
}
