package Entity;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private String login;
	private int x;
	private int y;
	private int map;
	private int lobby;
	private boolean isHUB = true;
	
	private int life;
	private int mana;
	
	private boolean changeMap = false;
	
	private List<Integer> inv = new ArrayList<Integer>();
	private List<Integer> equ = new ArrayList<Integer>();
	private List<String> fri = new ArrayList<String>();
	
	public Player(String login, int x, int y, int map, int life, int mana, List<Integer> inv, List<Integer> equ, List<String> fri) {
		this.login = login;
		this.x = x;
		this.y = y;
		this.map = map;
		this.lobby = -1;
		
		this.life = life;
		this.mana = mana;
		
		this.inv = inv;
		this.equ = equ;
		this.fri = fri;
	}
	
	public String getLogin () {
		return login;
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
	
	public int getMap () {
		return map;
	}
	
	public void setMap (int map) {
		this.map = map;
	}
	
	public int getLobby () {
		return lobby;
	}
	
	public void setLobby (int lobby) {
		this.lobby = lobby;
	}

	public boolean isHUB() {
		return isHUB;
	}

	public void setHUB(boolean isHUB) {
		this.isHUB = isHUB;
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
	
	public void setInventory (List<Integer> inv, List<Integer> equ) {
		this.inv = inv;
		this.equ = equ;
	}
	
	public void setFriend(List<String> fri) {
		this.fri = fri;
	}
	
	public List<Integer> getInv () {
		return inv;
	}
	
	public List<Integer> getEqu () {
		return equ;
	}
	
	public List<String> getFri () {
		return fri;
	}

	public boolean isChangeMap() {
		return changeMap;
	}

	public void setChangeMap(boolean changeMap) {
		this.changeMap = changeMap;
	}
}
