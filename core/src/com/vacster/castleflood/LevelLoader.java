package com.vacster.castleflood;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader {
	
	private ArrayList<Integer> items;
	private int waterAmount, resources;
	
	public LevelLoader(int level, ArrayList<String> things){
		JsonReader json = new JsonReader();
		items = new ArrayList<Integer>();
		JsonValue jsonValue = json.parse(Gdx.files.internal("levels/"+level+".json"));
		for(int x = 0; x < things.size(); x++)
			items.add(jsonValue.getInt(things.get(x)));
		waterAmount = jsonValue.getInt("waterAmount");
		resources = jsonValue.getInt("resources");
	}
	
	public boolean getAmount(int i){
		return items.get(i)>0;//TODO: Temp fix, change json to bools
	}
	
	public int getWater(){
		return waterAmount;
	}
	
	public int getResources() {
		return resources;
	}

}
