package net.periple.server;

import java.util.ArrayList;
import java.util.List;

import Entity.Monster;
import Entity.Player;

public class Lobby {
	
	private List<Integer> player = new ArrayList<Integer>();
	private List<Map> map = new ArrayList<Map>();
	private List<Thread> mapThread = new ArrayList<Thread>();
	
	private String leaderName;
		
	public Lobby (List<Integer> player) {
		this.player = player;
	}
	
	public Lobby (List<Integer> player, String leaderName) {
		this.player = player;
		this.leaderName = leaderName;
	}
	
	public void setMaps(MapInfo mapInfo) {
		for(int i=0; i<mapInfo.getNumMap(); i++){
			Map m;
			List<Monster> monster = mapInfo.getMonster(i);
			mapThread.add(new Thread(m = new Map(i, mapInfo.isFight(i), new ArrayList<Integer>(), monster)));
			map.add(m);
		}
	}
	
	public void setStart (int i) {
		if(map.get(i).getMonster().size() > 0){
			map.get(i).setPaused(false);
			if(!map.get(i).isStarted()){
				map.get(i).setStarted(true);
				mapThread.get(i).start();
			}else{
				map.get(i).setFight(true);
			}
		}
	}
	
	public void setStop (int i) {
		map.get(i).setFight(false);
		map.get(i).setPaused(true);
		map.get(i).endTurnFinal();
	}
	
	public boolean isStop (int i) {
		if(map.get(i).isPaused()){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Integer> getSender () {
		return player;
	}
	
	public void addPlayer (int p) {
		player.add(p);
	}
	
	public void removePlayer (int p) {
		for(int i=0; i < player.size(); i++){
			if(player.get(i) == p){
				player.remove(i);
				i = player.size();
			}
		}
	}
	
	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public Map getMap(int i) {
		return map.get(i);
	}

	public boolean getFight(int i) {
		return map.get(i).getFight();
	}

	public List<Monster> getMonsterList(int i) {
		return map.get(i).getMonster();
	}
	
	public void removeMapPlayer (int i, int p) {
		map.get(i).removePlayer(p);
	}
	
	public void endTurn (int i, int p) {
		map.get(i).endTurn(p);
	}
	
	public void endTurnFinal (int i) {
		map.get(i).endTurnFinal();
	}
	
	public void monsterSetLife (int i, int monstID, int damage) {
		map.get(i).monsterSetLife(monstID, damage);
	}
	
	public void changeNum(int p) {
		for(int i=0; i < player.size(); i++){
			if(player.get(i)>p){
				player.set(i, player.get(i)-1);
			}
		}
	}

	public void DestroyMap() {	
		for(int i=0; i<map.size(); i++){
			map.get(i).setFight(false);
			map.get(i).setRunning(false);
			map.get(i).endTurnFinal();
		}
		map = new ArrayList<Map>();
		mapThread = new ArrayList<Thread>();
	}
}
