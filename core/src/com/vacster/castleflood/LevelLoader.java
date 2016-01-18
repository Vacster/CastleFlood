package com.vacster.castleflood;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader {
	
	private ArrayList<Boolean> items;
	private int waterAmount, resources, length;
	
	public LevelLoader(int level, ArrayList<String> things){
		JsonReader json = new JsonReader();
		items = new ArrayList<Boolean>();
		JsonValue jsonValue = json.parse(Gdx.files.internal("levels/"+level+".json"));
		for(int x = 0; x < things.size(); x++)
			items.add(jsonValue.getBoolean(things.get(x)));
		waterAmount = jsonValue.getInt("waterAmount");
		resources = jsonValue.getInt("resources");
		length = jsonValue.getInt("length");
	}
	
	public boolean getExists(int i){
		return items.get(i);
	}
	
	public int getWater(){
		return waterAmount;
	}
	
	public int getResources() {
		return resources;
	}

	public int getLength() {
		return length;
	}

}
