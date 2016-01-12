package com.vacster.castleflood;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader {
	
	public int concreteBoxAmount, concreteTriangleAmount, woodBoxAmount, steelBoxAmount, goldBoxAmount, rockAmount, waterAmount, stickAmount
	, bronzeBoxAmount, ironBoxAmount;
	
	public LevelLoader(int level){
		JsonReader json = new JsonReader();
		JsonValue jsonValue = json.parse(Gdx.files.internal("levels/"+level+".json"));
		concreteBoxAmount = jsonValue.getInt("concreteBoxAmount");
		concreteTriangleAmount = jsonValue.getInt("concreteTriangleAmount");
		rockAmount = jsonValue.getInt("woodBoxAmount");
		woodBoxAmount = jsonValue.getInt("woodBoxAmount");
		steelBoxAmount = jsonValue.getInt("steelBoxAmount");
		goldBoxAmount = jsonValue.getInt("goldBoxAmount");
		waterAmount = jsonValue.getInt("waterAmount");
		stickAmount = jsonValue.getInt("stickAmount");
		bronzeBoxAmount = jsonValue.getInt("bronzeBoxAmount");
		ironBoxAmount = jsonValue.getInt("ironBoxAmount");
	}

}
