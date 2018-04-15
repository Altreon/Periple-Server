package Action;

import java.util.ArrayList;
import java.util.List;

import Entity.Monster;
import net.periple.server.Lobby;
import net.periple.server.Map;
import net.periple.server.Server;

public class ActionPlayerAttack extends Action{
	
	private static int damage = 10;
	private static double prob = 0;
	private static int time = 250;
	
	private int p;
	private int dir;
	private long startTime;
	private int targetID = -1;
	private boolean applyDamage = false;
	private Lobby lob;

	public ActionPlayerAttack(int p, int dir) {
		super(damage, prob, time);
		this.p = p;
		this.dir = dir;
	}
	
	@Override
	public void use (int turn) {
		lob = lobby.get(player.get(p).getLobby());
		List<Monster> monster = new ArrayList<Monster>();
		monster = lob.getMap(player.get(p).getMap()).getMonster();
		
		for(int i=0; i<monster.size(); i++){
			Monster m = monster.get(i);
			if(dir == 1 && m.isTouch(player.get(p).getX(), player.get(p).getY() + 32)){
				targetID = i;
			}else if(dir == 2 && m.isTouch(player.get(p).getX(), player.get(p).getY() - 32)){
				targetID = i;
			}else if(dir == 3 && m.isTouch(player.get(p).getX() + 32, player.get(p).getY())){
				targetID = i;
			}else if(dir == 4 && m.isTouch(player.get(p).getX() - 32, player.get(p).getY())){
				targetID = i;
			}
		}
		
		for(int play : lob.getMap(player.get(p).getMap()).getPlayer()){
			e.get(play).sendAction(0, turn, true);
		}
		startTime = System.currentTimeMillis();
	}

	@Override
	public void use(int posX, int posY, List<Integer> target, Monster monster, int turn, Map map) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void effect() {
		if(targetID != -1){
			if(!applyDamage && System.currentTimeMillis() - startTime >= 100){
				lob.monsterSetLife(player.get(p).getMap(), targetID, super.getDamage());
				applyDamage = true;
			}
		}
		
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
