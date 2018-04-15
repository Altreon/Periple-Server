package net.periple.server;

import java.util.ArrayList;
import java.util.List;

import Entity.Monster;

public class MapInfo {
	
	private String mapName;
	private int numMap;
	private List<List<Monster>> monster = new ArrayList<List<Monster>>();
	
	public MapInfo (String mapName, int numMap, List<List<Monster>> monster) {
		this.mapName = mapName;
		this.numMap = numMap;
		this.monster = monster;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public int getNumMap() {
		return numMap;
	}
	
	public boolean isFight(int i) {
		if(monster.get(i).size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	public List<Monster> getMonster(int i) {
		return monster.get(i);
	}
}
