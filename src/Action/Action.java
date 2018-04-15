package Action;

import java.util.ArrayList;
import java.util.List;

import Entity.Monster;
import Entity.Player;
import net.periple.server.Emission;
import net.periple.server.Lobby;
import net.periple.server.Map;
import net.periple.server.Server;

public abstract class Action {
	private int damage;
	private double prob;
	private int time;
	
	protected List<Player> player = new ArrayList<Player>();
	protected List<Emission> e = new ArrayList<Emission>();
	protected ArrayList<Lobby> lobby = new ArrayList<Lobby>();

	public Action (int damage, double prob, int time){
		this.damage = damage;
		this.prob = prob;
		this.time = time;
		
		player = Server.getPlayer();
		e = Server.getE();
		lobby = Server.getLobby();
	}
	
	public abstract void use(int turn);
	public abstract void use(int posX, int posY, List<Integer> target, Monster monster, int turn, Map map);
	public abstract void effect();
	public abstract boolean canUse(int posX, int posY, List<Integer> target);
	public abstract boolean isFinish();
	
	public int getDamage () {
		return damage;
	}
	
	public double getProb () {
		return prob;
	}
	
	public int getWaitTime () {
		return time;
	}
}
