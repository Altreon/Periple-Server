package Action;

import java.util.List;

import Entity.Monster;
import net.periple.server.Map;
import net.periple.server.Server;

public class ActionMonsterAttack extends Action{
	
	private static int damage = 1;
	private static double prob = 0.75;
	private static int time = 950;
	
	private int[][] areas = {{0,1}, {0,-1}, {1,0}, {-1,0}};
	private long startTime;
	private int targetI;
	private boolean applyDamage = false;

	public ActionMonsterAttack() {
		super(damage, prob, time);
	}
	
	@Override
	public void use(int turn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void use(int posX, int posY, List<Integer> target, Monster monster, int turn, Map map) {
		int targetID = -1;
		for(int i=0; i < target.size(); i++){
			for(int[] area : areas){
				if(posX + area[0] == player.get(target.get(i)).getX()/32 && posY + area[1] == player.get(target.get(i)).getY()/32){
					if(monster.turn(player.get(target.get(i)).getX()/32, player.get(target.get(i)).getY()/32)){
						Server.sendMonsterTurn(turn, monster.getDir(), target);
					}
					targetI = target.get(i);
					//Server.setLife(target.get(i), Integer.toString(player.get(target.get(i)).getLife() - super.getDamage()), false);
					targetID = i;
					break;
				}
			}
			if(targetID != -1){
				i = target.size();
				for(int p : target){
					e.get(p).sendAction(1, turn, false);
				}
			}
			startTime = System.currentTimeMillis();
		}
		
		/*
		Monster monster = lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getMonster().get(monst);
		Server.setLife(p, Integer.toString(player.get(p).getLife() - super.getDamage()), false);
		for(int i : lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getPlayer()){
			e.get(i).sendAction(1);
		}
		*/
	}

	@Override
	public boolean canUse(int posX, int posY, List<Integer> target) {
		boolean b = false;
		for(int i=0; i < target.size(); i++){
			for(int[] area : areas){
				if(posX + area[0] == player.get(target.get(i)).getX()/32 && posY + area[1] == player.get(target.get(i)).getY()/32){
					b = true;
					break;
				}
			}
			if(b){
				i = target.size();
			}
		}
		return b;
	}

	@Override
	public void effect() {
		if(!applyDamage && System.currentTimeMillis() - startTime >= 600){
			Server.setLife(targetI, Integer.toString(player.get(targetI).getLife() - super.getDamage()), false);
			applyDamage = true;
		}
		
	}

	@Override
	public boolean isFinish() {
		if(System.currentTimeMillis() - startTime >= time){
			return true;
		}else{
			return false;
		}
	}
}
