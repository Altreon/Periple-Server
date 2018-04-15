package Action;

import java.util.List;

import Entity.Monster;
import net.periple.server.Map;
import net.periple.server.Server;

public class ActionMonsterSuperAttack extends Action{
	
	private static int damage = 10;
	private static double prob = 0.25;
	private static int time = 5450;
	
	private int[][][] areas = {{{0,1}, {0,-1}, {1,0}, {-1,0}}, 
								{{0,2}, {1,1}, {-1,1}, {0,-2}, {1,-1}, {-1,-1}, {2,0}, {-2,0}}, 
								{{0,3}, {1,2}, {-1,2}, {0,-3}, {1,-2}, {-1,-2}, {3,0}, {2,1}, {2,-1}, {-3,0}, {-2,1}, {-2,-1}}};
	private boolean[] framesActive = {false, false, false};
	private long startTime;
	
	private int frame = -1;	
	private int milis = 200;
	private long timeOld;
	
	private int posX;
	private int posY;
	private List<Integer> target;

	public ActionMonsterSuperAttack() {
		super(damage, prob, time);
	}

	@Override
	public void use(int turn) {
		// TODO Auto-generated method stub
	}

	@Override
	public void use(int posX, int posY, List<Integer> target, Monster monster, int turn, Map map) {
		this.posX = posX;
		this.posY = posY;
		this.target = target;
		
		boolean b = false;
		for(int i=0; i < target.size(); i++){
			for(int[][] areaa : areas){
				for(int[] area : areaa){
					if(posX + area[0] == player.get(target.get(i)).getX()/32 && posY + area[1] == player.get(target.get(i)).getY()/32){
						if(monster.turn(player.get(target.get(i)).getX()/32, player.get(target.get(i)).getY()/32)){
							Server.sendMonsterTurn(turn, monster.getDir(), target);
						}
						break;
					}
				}
				if(b){
					break;
				}
			}
			if(b){
				i = target.size();
			}
		}
		
		for(int p : target){
			e.get(p).sendAction(3, turn, false);
		}
		startTime = System.currentTimeMillis();
	}

	@Override
	public boolean canUse(int posX, int posY, List<Integer> target) {
		boolean b = false;
		for(int i=0; i < target.size(); i++){
			for(int[][] areaa : areas){
				for(int[] area : areaa){
					if(posX + area[0] == player.get(target.get(i)).getX()/32 && posY + area[1] == player.get(target.get(i)).getY()/32){
						b = true;
						break;
					}
				}
				if(b){
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
		if(System.currentTimeMillis() - startTime > 4400){
			if(System.currentTimeMillis() - timeOld > milis && frame < areas.length-1){
				frame++;
				timeOld = System.currentTimeMillis();
			}
			if(!framesActive[frame]){
				for(int i=0; i < target.size(); i++){
					for(int[] area : areas[frame]){
						if(posX + area[0] == player.get(target.get(i)).getX()/32 && posY + area[1] == player.get(target.get(i)).getY()/32){
							boolean remove = false;
							if(player.get(target.get(i)).getLife() - super.getDamage() <= 0){
								remove = true;
							}
							Server.setLife(target.get(i), Integer.toString(player.get(target.get(i)).getLife() - super.getDamage()), false);
							if(remove){
								break;
							}
						}
					}
				}
				framesActive[frame] = true;
			}
		}
			
		/*
		Monster monster = lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getMonster().get(monst);
		Server.setLife(p, Integer.toString(player.get(p).getLife() - super.getDamage()), false);
		for(int i : lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getPlayer()){
			e.get(i).sendAction(3);
		}
		*/
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
