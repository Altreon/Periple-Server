package net.periple.server;

import java.io.BufferedReader;
import java.io.IOException;

public class Reception implements Runnable {

	private BufferedReader in;
	public int num = 0;
	private int t;
	private int a;
	private String r;
	private boolean run = true;
	
	public Reception(BufferedReader in, int n){
		this.in = in;
		this.num = n;
	}
	
	public void run() {
		while(run){
	        try {
	        	t = in.read();
	        	if(t == 0){
	        		a = in.read();
	        		r = in.readLine();
	        		Server.setPlayer(a, r, num);
	        	}else if(t == 1){
	        		a = in.read();
	        		Server.setPlayer(a, num);
	        	}else if(t == 2){
	        		r = in.readLine();
	        		String x = in.readLine();
	        		String y = in.readLine();
	        		Server.setPlayer(r, x, y, num);
	        	}
	        	//if(t == 3){
	        		//String x = in.readLine();
	        		//String y = in.readLine();
	        		//Server.Createlobby(num, x, y);
	        	//}
	        	else if(t == 4){
	        		String l = in.readLine();
	        		Server.setLife(num, l, true);
	        	}else if(t == 5){
	        		Server.endTurn(num);
	        	}else if(t == 6){
	        		int dir = in.read();
	        		int action = in.read();
	        		Server.setAction(num, dir, action);
	        	}else if(t == 7){
	        		String invS = in.readLine();
	        		String equS = in.readLine();
	        		Server.setInventory(invS, equS, num);
	        	}else if(t == 8){
	        		String friS = in.readLine();
	        		Server.setFriend(friS, num);
	        	}else if(t == 9){
	        		String name = in.readLine();
	        		Server.notifQuestion("friend", name, num);
	        	}else if(t == 10){
	        		String info = in.readLine();
	        		String name = in.readLine();
	        		Server.notifInfo("friendReply", info, name, num);
	        	}else if(t == 11){
	        		String name = in.readLine();
	        		Server.notifInfo("friendDelete", "", name, num);
	        	}else if(t == 12){
	        		int ID = in.read();
	        		Server.notifDelete(ID);
	        	}else if(t == 13){
	        		System.out.println("receive");
	        		String name = in.readLine();
	        		Server.notifQuestion("team", name, num);
	        	}else if(t == 14){
	        		String info = in.readLine();
	        		String name = in.readLine();
	        		Server.notifInfo("teamReply", info, name, num);
	        	}else if(t == 15){
	        		String name = in.readLine();
	        		Server.deleteLobby(name, num);
	        	}else if(t == 16){
	        		Server.setLife(num, "0", false);
	        	}
		    } catch (IOException e) {
		    	if(run){
		    		Server.disconnect(num, false);
		    	}
		    	run = false;
			}
		}
	}
	
	public void stop () {
		run = false;
	}
	
	public void changeNum () {
		num--;
	}
}

