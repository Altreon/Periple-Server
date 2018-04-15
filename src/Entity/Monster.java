package Entity;

import java.util.ArrayList;
import java.util.List;

import Action.Action;
import net.periple.server.Map;
import net.periple.server.Server;

public abstract class Monster {
	private int id;
	
	private int x;
	private int y;
	private int dir;
	
	private int[][] collision;
		
	private int life;
	private int mana;
	
	private String[] actions;
	
	//private int attackActionID;
	//private int superAttackActionID;
	
	public Monster(int id, int x, int y, String[] actions, int[][] collision) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.actions = actions;
		this.collision = collision;
		this.life = 150;
		this.mana = 10;
	}
	
	public int getId () {
		return id;
	}
	
	public int getX () {
		return x;
	}
	
	public void setX (int x) {
		this.x = x;
	}
	
	public int getY () {
		return y;
	}
	
	public void setY (int y) {
		this.y = y;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}
	
	public double distanceBetween(double x1, double y1, double x2, double y2) {
		double dx = Math.abs(x2-x1);
		double dy = Math.abs(y2-y1);
		if (dx > dy) {
			double r = dy/dx;
			return dx*Math.sqrt(1.0 + r*r);
		} else {
			double r = dx/dy;
			return dy*Math.sqrt(1.0 + r*r);
		}
	}
	 
	public abstract void move(List<Player> player);
	
	public boolean turn(int xTarget, int yTarget){
		int newDir = 0;
		
		if(yTarget - y > 0){
			if(xTarget - x > 0){
				if(yTarget - y > xTarget - x){
					newDir = 1;
				}else{
					newDir = 3;
				}
			}else{
				if(yTarget - y > x - xTarget){
					newDir = 1;
				}else{
					newDir = 4;
				}
			}
		}else{
			if(xTarget - x > 0){
				if(y - yTarget > xTarget - x){
					newDir = 2;
				}else{
					newDir = 3;
				}
			}else{
				if(y - yTarget > x - xTarget){
					newDir = 2;
				}else{
					newDir = 4;
				}
			}
		}
				
		if(newDir != dir){
			dir = newDir;
			return true;
		}else{
			return false;
		}
		
		/*
		boolean b = false;
		if(x == xTarget && y < yTarget && dir != 1){
			dir = 1;
			b = true;
		}else if(x == xTarget && y > yTarget && dir != 2){
			dir = 2;
			b = true;
		}else if(y == yTarget && x < xTarget && dir != 3){
			dir = 3;
			b = true;
		}else if(y == yTarget && x > xTarget && dir != 3){
			dir = 4;
			b = true;
		}
		return b;
		*/
	}
	
	public void setDir(int dir){
		this.dir = dir;
	}

	public int getDir() {
		return dir;
	}

	public boolean action(List<Integer> target, int turn, Map map) {
		List<Action> actionPossible = new ArrayList<Action>();
		List<Double> actionP = new ArrayList<Double>();
		
		double actionNotPossibletotalP = 0;
		
		for(Action action : Server.getActionList(actions)){
			if(action.canUse(x, y, target)){
				actionPossible.add(action);
			}else{
				actionNotPossibletotalP += action.getProb();
			}
		}
		
		double preActionRef = 0;
		actionP.add(preActionRef);
		for(Action action : actionPossible){
			if(actionNotPossibletotalP == 0){
				actionP.add(action.getProb() + preActionRef);
				preActionRef = action.getProb() + preActionRef;
			}else{
				double pBis = action.getProb()/(1-actionNotPossibletotalP);
				actionP.add(pBis + preActionRef);
				preActionRef = pBis + preActionRef;
			}
		}
		
		if(actionPossible.size() > 0){
			double random = Math.random();
			int ref = 0;
			try {
				while(!(random > actionP.get(ref) && random < actionP.get(ref+1))){
					ref++;
				}
			} catch (Exception e) {
				ref = actionPossible.size()-1;
			}
			map.setAction(actionPossible.get(ref), x, y, this);
			//actionPossible.get(ref).use(x, y, target, this, turn, map);
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isTouch (int tX, int tY) {
		for(int[] coord : collision){
			if(x*32 + coord[0]*32 == tX && y*32 + coord[1]*32 == tY){
				return true;
			}
		}
		return false;
	}
}
