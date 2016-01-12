package Actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vacster.castleflood.Stages.HUDStage;

public class PauseButton extends ImageButton {

	HUDStage stageCpy;
	
	public PauseButton(Skin skin, HUDStage stage) {
		super(skin, "menupause");
		this.stageCpy = stage;
		addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				changeValue();
			}
		});
		setSize(22f, 22f);
		setPosition(0f, 158f);
	}
	
	private void changeValue(){
		stageCpy.handlePause();
	}

}
