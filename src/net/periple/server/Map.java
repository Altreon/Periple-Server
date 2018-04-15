package net.periple.server;

import java.util.ArrayList;
import java.util.List;

import Action.Action;
import Entity.Monster;
import Entity.Player;

public class Map implements Runnable {
	
	private int ID;
	private boolean isStarted = false;
	private boolean isRunning = true;
	private boolean isPaused = true;
	private boolean fight;
	
	private List<Integer> player = new ArrayList<Integer>();
	private List<Monster> monster = new ArrayList<Monster>();
	
	private int turn = 0;
	private int monsterTurn = 0;
	private int mili = 0;
	
	private List<Action> actions = new ArrayList<Action>();
	private List<Integer> actionsEraseID = new ArrayList<Integer>();

	public Map(int ID, boolean fight, List<Integer> player, List<Monster> monster) {
		this.ID = ID;
		this.fight = fight;
		this.player = player;
		this.monster = monster;
		//monster.add(new Monster(5, 5, ID));
	}
	
	public void goRun () {
		run();
	}

	@Override
	public void run() {
		while(isRunning){
			while(fight){
				while(turn < player.size()){
					Server.sendTurn(player.get(turn));
					try {
						while (mili < 10000){
							int i=0;
							for(Action action : actions){
								if(!action.isFinish()){
									action.effect();
								}else{
									actionsEraseID.add(i);
								}
								i++;
							}
							for(int a=0; a < actionsEraseID.size(); a++){
								actions.remove(actionsEraseID.get(a));
							}
							actionsEraseID.clear();
							Thread.sleep(50);
							mili+=50;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mili = 0;
					turn++;
				}
				
				if(fight){
					while(monsterTurn < monster.size()){
						//int x = monster.get(monsterTurn).getX();
						//int y = monster.get(monsterTurn).getY();
						if(!monster.get(monsterTurn).action(player, monsterTurn, this)){
							//}else if(x != monster.get(monsterTurn).getX() || y != monster.get(monsterTurn).getY()){
							monster.get(monsterTurn).move(Server.getPlayerList(player));
							Server.sendMonster(monsterTurn, player, monster.get(monsterTurn).getX(), monster.get(monsterTurn).getY());
							waitTime(1200, false);
						}
						try {
							while (mili < 0){
								int i=0;
								for(Action action : actions){
									if(!action.isFinish()){
										action.effect();
									}else{
										actionsEraseID.add(i);
									}
									i++;
								}
								for(int a=0; a < actionsEraseID.size(); a++){
									actions.remove(actionsEraseID.get(a));
								}
								actionsEraseID.clear();
								/*
								if(actionRest !=  null && System.currentTimeMillis() - actionRestStart > actionRest.getWaitTime()){
									actionRest.useRest(actionRestArgs[0], actionRestArgs[1]);
									actionRest = null;
								}
								*/
								Thread.sleep(50);
								mili+=50;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
							/*
							Server.sendMonsterTurn(monsterTurn, monster.get(monsterTurn).getDir(), player);
							int actionID = 0;
							if(Math.random() < 0.5){
								actionID = monster.get(monsterTurn).getAttackActionID();
							}else{
								actionID = monster.get(monsterTurn).getSuperAttackActionID();
							}
							Server.setAction(player.get(monster.get(monsterTurn).getTargetID()), monsterTurn, actionID);
							*/
						//}
						mili = 0;
						monsterTurn++;
					}
				}
				turn = 0;
				monsterTurn = 0;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setFight (boolean fight) {
		this.fight = fight;
	}
	
	public boolean getFight () {
		return fight;
	}
	
	public List<Integer> getPlayer () {
		return player;
	}
	
	public List<Monster> getMonster () {
		return monster;
	}
	
	public void endTurn (int p) {
		if(player.get(turn) == p){
			mili = 10000;
		}
	}
	
	public void endTurnFinal () {
		mili = 10000;
	}
	
	public void removePlayer(int p) {
		int playerPlace = -1;
		for(int i=0; i < player.size(); i++){
			if(player.get(i) == p){
				playerPlace = i;
				i = player.size();
			}
		}
		if(fight && turn >= playerPlace){
			turn--;
		}
		
		try {
			player.remove(playerPlace);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ce joueur n'existe pas sur cette carte");
		}
	}

	public void monsterSetLife(int monstID, int damage) {
		Monster monst = monster.get(monstID);
		monst.setLife(monst.getLife()-damage);
		Server.sendMonsterLife(monstID, player, monst.getLife());
		if(monst.getLife() <= 0){
			monster.remove(monstID);
			if(monster.size() <= 0){
				fight = false;
				isRunning = false;
				mili = 10000;
				Server.stopFight(player);
			}
		}
	}
	
	public void playerDead (int i) {
		
	}
	
	public void waitTime (int milis, boolean isPlayer){
		try {
			if(isPlayer){
				mili -= milis;
			}else{
				System.out.println("startWait");
				Thread.sleep(milis);
				System.out.println("endWait");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAction(Action action) {
		action.use(turn);
		actions.add(action);
		waitTime(action.getWaitTime(), true);
		/*
		action.use(actionArgs[0], actionArgs[1]);
		actionRest = action;
		actionRestArgs = actionArgs;
		actionRestStart = System.currentTimeMillis();
		waitTime(action.getWaitTime(), true);
		*/
	}
	
	public void setAction(Action action, int x, int y, Monster monster) {
		action.use(x, y, player, monster, monsterTurn, this);
		actions.add(action);
		waitTime(action.getWaitTime(), true);
	}
	
	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}
}
