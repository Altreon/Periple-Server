package Entity;

import java.util.List;

public class Kaisirak extends Monster{
	private static int id = 1;
	private static int[][] collision = {{3,2},{3,1},{3,0},
			{2,3},{2,2},{2,1},{2,0},{2,-1},{2,-2},
			{1,3},{1,2},{1,1},{1,0},{1,-1},{1,-2},
			{0,3},{0,2},{0,1},{0,0},{0,-1},{0,-2},
			{-1,3},{-1,2},{-1,1},{-1,0},{-1,-1},{-1,-2},
			{-2,3},{-2,2},{-2,1},{-2,0},{-2,-1},{-2,-2},
			{-3,2},{-3,1},{-3,0}};

	public Kaisirak(int x, int y, String[] actions) {
		super(id, x, y, actions, collision);
	}

	@Override
	public void move(List<Player> player) {
		/*
		int xTarget = 0;
		int yTarget = 0;
		double minDistance = 1000;
		for(int i=0; i < player.size(); i++){
			int xPlayer = player.get(i).getX();
			int yPlayer = player.get(i).getY();
			double distance = distanceBetween(super.getX()*32, super.getY()*32, xPlayer, yPlayer);
			if(minDistance > distance){
				minDistance = distance;
				xTarget = xPlayer;
				yTarget = yPlayer;
			}
		}
		if(minDistance > 32){
			if(super.getX()*32 > xTarget){
				super.setX(super.getX()-1);
				super.setDir(4);
			}else if(super.getX()*32 < xTarget){
				super.setX(super.getX()+1);
				super.setDir(3);
			}else if(super.getY()*32 > yTarget){
				super.setY(super.getY()-1);
				super.setDir(2);
			}else if(super.getY()*32 < yTarget){
				super.setY(super.getY()+1);
				super.setDir(1);
			}
		}else{
			super.turn(xTarget/32, yTarget/32);
		}
		*/
	}

}
