package Action;

import java.util.List;

import Entity.Monster;
import net.periple.server.Map;

public class ActionPlayerSuperAttack extends Action{
	
	private static int damage = 50;
	private static double prob = 0;
	private static int time = 4950;
	
	//private boolean asEffect = false;
	private int[][][] areas = {{{0,1}}, 
								{{0,2}}, 
								{{0,3}}};
	private boolean[] framesActive = {false, false, false};
	private long startTime;

	private int frame = -1;	
	private int milis = 200;
	private long timeOld;
	
	private int p;
	private int dir;
	private List<Monster> target;

	public ActionPlayerSuperAttack(int p, int dir) {
		super(damage, prob, time);
		this.p = p;
		this.dir = dir;
		target = lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getMonster();
		
		if(dir == 2){
			int[][][] newAreas = {{{0,-1}}, 
					{{0,-2}}, 
					{{0,-3}}};
			areas = newAreas;
		}else if(dir == 3){
			int[][][] newAreas = {{{1,0}}, 
					{{2,0}}, 
					{{3,0}}};
			areas = newAreas;
		}else if(dir == 4){
			int[][][] newAreas = {{{-1,0}}, 
					{{-2,0}}, 
					{{-3,0}}};
			areas = newAreas;
		}
	}

	@Override
	public void use (int turn) {
		for(int play : lobby.get(player.get(p).getLobby()).getMap(player.get(p).getMap()).getPlayer()){
			e.get(play).sendAction(2, turn, true);
		}
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void effect() {
		if(System.currentTimeMillis() - startTime > 3750){
			if(System.currentTimeMillis() - timeOld > milis && frame < areas.length-1){
				frame++;
				timeOld = System.currentTimeMillis();
			}
			if(!framesActive[frame]){
				for(int i=0; i < target.size(); i++){
					for(int[] area : areas[frame]){
						if(target.get(i).isTouch(player.get(p).getX() + (area[0]*32), player.get(p).getY() + (area[1]*32))){
							lobby.get(player.get(p).getLobby()).monsterSetLife(player.get(p).getMap(), i, super.getDamage());
						}
					}
				}
				framesActive[frame] = true;
			}
		}
		
		/*
		if(!applyDamage && System.currentTimeMillis() - startTime >= 3750){
			lob.monsterSetLife(player.get(p).getMap(), targetI, super.getDamage());
			applyDamage = true;
		}
		*/
		
	}

	@Override
	public void use(int posX, int posY, List<Integer> target, Monster monster, int turn, Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canUse(int posX, int posY, List<Integer> target) {
		// TODO Auto-generated method stub
		return false;
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
