package com.vacster.castleflood;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class ItemLoader {

	JsonValue jsonValue;

	public ItemLoader(){
		JsonReader json = new JsonReader();
		jsonValue = json.parse(Gdx.files.internal("items.json"));
	}
	
	public float[] getValues(String str, float posX, float posY){
		//{WIDTH, HEIGHT, X, Y, DENSITY, FRICTION, RESTITUTION}
		JsonValue tv = jsonValue.get(str);
		float[] tmp = {tv.getFloat("width"), tv.getFloat("height"), posX, posY, 
				tv.getFloat("density"), tv.getFloat("friction"), tv.getFloat("restitution")};
		return tmp;	
	}
}

