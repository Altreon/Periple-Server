package net.periple.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Emission implements Runnable {

	private PrintWriter out;
	private String message;
	public int num = 0;
	
	public Emission(PrintWriter out, int n) {
		this.out = out;
		this.num = n;
	}

	
	public void run() {
		
	}
	
	synchronized public int getNum (){
		return num;
	}
	
	synchronized public void send(String login, String x, String y, String life) {
		out.write(0);
		out.println(login);
		out.println(x);
		out.println(y);
		out.println(life);
		out.flush();
	}
	
	synchronized public void send (int i, int a, String m){
		message = m;
		out.write(1);
		out.write(i);
		out.write(a);
		out.println(message);
		out.flush();
	}
	
	synchronized public void send (int i, int a){
		out.write(2);
		out.write(i);
		out.write(a);
		out.flush();
	}

	synchronized public void send(boolean b, boolean isFight) {
		out.write(3);
		out.println(isFight);
		out.flush();
	}
	
	synchronized public void send(boolean b, int n, String login, String x, String y, String life) {
		if(b){
			out.write(4);
			out.write(n);
			out.println(login);
			out.println(x);
			out.println(y);
			out.println(life);
			out.flush();
		}else{
			out.write(5);
			out.flush();
		}
	}
	
	public void send(String inv, String equ, String fri) {
		out.println(inv);
		out.println(equ);
		out.println(fri);
		out.flush();
	}

	synchronized public void sendDelete(int i) {
		out.write(6);
		out.write(i);
		out.flush();
	}
	
	synchronized public void sendAll(boolean b, int map, boolean isFight) {
		out.write(7);
		out.println(isFight);
		out.write(map);
		out.flush();
	}
	
	synchronized public void sendLife(int i, String l) {
		out.write(8);
		out.write(i);
		out.println(l);
		out.flush();
	}
	
	synchronized public void sendTurn() {
		out.write(9);
		out.flush();
	}

	synchronized public void sendAllMonster(boolean b, int id, int x, int y) {
		if(b){
			out.write(10);
			out.write(id);
			out.write(x);
			out.write(y);
			out.flush();
		}else{
			out.write(11);
			out.flush();
		}
	}
	
	synchronized public void sendMonster(int i, int x, int y) {
		out.write(12);
		out.write(i);
		out.write(x);
		out.write(y);
		out.flush();
	}

	synchronized public void sendMonsterTurn(int i, int a) {
		out.write(13);
		out.write(i);
		out.write(a);
		out.flush();
	}

	synchronized public void sendLifeMonster(int i, String l) {
		out.write(14);
		out.write(i);
		out.println(l);
		out.flush();
	}


	synchronized public void stopFight() {
		out.write(15);
		out.flush();
	}


	synchronized public void sendNotifQuestion(int ID, String type, String name) {
		out.write(16);
		out.write(ID);
		out.println(type);
		out.println(name);
		out.flush();
	}


	public void sendNotifInfo(int ID, String type, String info, String name) {
		out.write(17);
		out.write(ID);
		out.println(type);
		out.println(info);
		out.println(name);
		out.flush();
	}
	
	public void sendFriendWait(String name) {
		out.write(18);
		out.println(name);
		out.flush();
	}
	
	public void sendNewTeam(String name){
		out.write(19);
		out.println(name);
		out.flush();
	}
	
	synchronized public void sendTeam(boolean b, String name, String leaderName) {
		if(b){
			out.write(20);
			out.println(name);
			out.flush();
		}else{
			out.write(21);
			out.println(leaderName);
			out.flush();
		}
	}
	
	public void sendDeleteTeam(String name){
		out.write(22);
		out.println(name);
		out.flush();
	}
	
	public void sendDeleteAllTeam(){
		out.write(23);
		out.flush();
	}


	public void sendLeader(String newLeaderName) {
		out.write(24);
		out.println(newLeaderName);
		out.flush();
	}
	
	public void sendAction(int idAction, int id, boolean isPlayer) {
		out.write(25);
		out.write(idAction);
		out.write(id);
		out.println(isPlayer);
		out.flush();
	}
}
