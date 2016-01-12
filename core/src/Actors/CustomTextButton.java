package Actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vacster.castleflood.CastleFlood;
import com.vacster.castleflood.Stages.LevelSelectStage;
import com.vacster.castleflood.Stages.STAGE;

public class CustomTextButton extends TextButton{

	public int level;
	
	public CustomTextButton(String text, Skin skin, final LevelSelectStage stage, final CastleFlood game) {
		super(text, skin);
		level = Integer.valueOf(text);
		addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.newLevel = level;
				game.changeStage(STAGE.GAME);
				super.clicked(event, x, y);
			}
		});
	}

}
