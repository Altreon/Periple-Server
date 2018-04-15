package net.periple.server;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import Action.*;
import Entity.Monster;
import Entity.Notif;
import Entity.Player;

public class Server {
	public static ServerSocket ss = null;
	public static Thread t;
	public static Server server = null;

	private static List<Player> player = new ArrayList<Player>();
	private static int num = 0;
	private static List<Socket> socket = new ArrayList<Socket>();
	private static List<Emission> e = new ArrayList<Emission>();
	private static List<Reception> r = new ArrayList<Reception>();
	
	private static ArrayList<ArrayList<Integer>> mapList = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<Integer> mapCity = new ArrayList<Integer>();
	private static ArrayList<Integer> mapShop = new ArrayList<Integer>();
	
	private static ArrayList<MapInfo> mapInfo = new ArrayList<MapInfo>();
	
	private static ArrayList<Lobby> lobby = new ArrayList<Lobby>();
	
	private static ArrayList<Notif> notifs = new ArrayList<Notif>();
	private static int notifID = 0;
	
	private static List<String> monsters = new ArrayList<String>();
	private static ArrayList<String> actions = new ArrayList<String>();
	//private static ArrayList<Action> actions = new ArrayList<Action>();
	
	public static void main(String[] args) {
		//ArrayList<Integer> ps = new ArrayList<Integer>();
		//ps.add(0);
		//ps.add(1);
		
		System.out.println("Chargement des actions...");
		actions.add("ActionPlayerAttack");
		actions.add("ActionMonsterAttack");
		actions.add("ActionPlayerSuperAttack");
		actions.add("ActionMonsterSuperAttack");
		/*
		actions.add(new ActionPlayerAttack("PlayerAttack", 10, 0, 0));
		actions.add(new ActionMonsterAttack("MonsterAttack", 10, 0.75, 0));
		actions.add(new ActionPlayerSuperAttack("PlayerSuperAttack", 50, 0, 3000));
		actions.add(new ActionMonsterSuperAttack("MonsterSuperAttack", 50, 0.25, 3000));
		*/
		System.out.println("Chargement des actions terminé");
		
		System.out.println("Chargement des monstres...");
		monsters.add("Knight");
		monsters.add("Kaisirak");
		System.out.println("Chargement des monstres terminé");
		
		System.out.println("Chargement des cartes...");
		mapList.add(mapCity);
		mapList.add(mapShop);
		
		mapInfo.add(new MapInfo("", 0, new ArrayList<List<Monster>>()));
		File directory = new File("Maps");
		File[] fList = directory.listFiles();
		int numMap = 0;
		for (File file : fList){
			if (file.isFile()){
				String mapName = file.getName();
				List<List<Monster>> monsterList = new ArrayList<List<Monster>>();
				try {
					Scanner sc = new Scanner(new File("Maps/"+file.getName()));
					while(sc.hasNext()){
						sc.nextLine();
						String line = sc.nextLine();
						List<Monster> monster = new ArrayList<Monster>();
						while(!line.equals("END")){
							try {
								StringTokenizer info = new StringTokenizer(line);
								String[] act = {actions.get(1), actions.get(3)};
								Monster monst = (Monster)Class.forName("Entity." + monsters.get(Integer.parseInt(info.nextToken()))).getConstructor(int.class, int.class, String[].class).newInstance(Integer.parseInt(info.nextToken()), Integer.parseInt(info.nextToken()), act);
								monster.add(monst);
								line = sc.nextLine();
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
									| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						monsterList.add(monster);
						if(sc.hasNext()){
							sc.nextLine();
						}
						numMap++;
					}
					sc.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mapInfo.add(new MapInfo(mapName, numMap, monsterList));
			}
		}
		System.out.println("Chargement des cartes terminé");
		
		System.out.println("Chargement des notifications...");
		try {
			Scanner sc = new Scanner(new File("Notifs.txt"));
			while(sc.hasNext()){
				StringTokenizer info = new StringTokenizer(sc.nextLine());
				notifs.add(new Notif(notifID, info.nextToken(), info.nextToken(), info.nextToken(), info.nextToken()));
				notifID++;
				
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Chargement des notifications terminé");

		System.out.println("Ouverture du serveur...");
		try {
			ss = new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("Le serveur est à l'écoute du port "+ss.getLocalPort());
			server = new Server();
			new Command(server);
			t = new Thread(new Accepter_connexion(ss));
			t.start();
			
		} catch (IOException e) {
			System.err.println("Le port "+ss.getLocalPort()+" est déjà utilisé !");
		}
		System.out.println("Serveur ouvert");
		System.out.println("Attention, si vous ne fermez pas le serveur à l'aide de la commande \"quit\", ");
		System.out.println("Certaines données risques de ne pas êtres sauvegarder");
		System.out.println("Pour afficher la liste des commandes disponnible, taper \"help\"");
		System.out.println("--------");
	}
	
	public static List<Player> getPlayer() {
		return player;
	}

	public static List<Emission> getE() {
		return e;
	}

	public static ArrayList<Lobby> getLobby() {
		return lobby;
	}
	
	synchronized static void addPlayer (String login, Socket so, Emission em, Reception rc, int HP, int MP, List<Integer> inv, String invS, List<Integer> equ, String equS, List<String> fri, String friS) {
		socket.add(so);
		e.add(em);
		r.add(rc);
		mapCity.add(num);
		player.add(new Player(login, 2*32, 9*32, 0, HP, MP, inv, equ, fri));
		num++;
		
		for(int i=0; i<mapCity.size()-1; i++){
			e.get(mapCity.get(i)).send(login, Integer.toString(2*32), Integer.toString(9*32), Integer.toString(HP));
		}

		e.get(num-1).send(true, false);
		for(int i=0; i<mapCity.size(); i++){
			if(i == mapCity.size()-1){
				e.get(num-1).send(true, 1, player.get(mapCity.get(i)).getLogin(), Integer.toString(player.get(mapCity.get(i)).getX()), Integer.toString(player.get(mapCity.get(i)).getY()), Integer.toString(player.get(mapCity.get(i)).getLife()));
				e.get(num-1).send(invS, equS, friS);
			}else{
				e.get(num-1).send(true, 0, player.get(mapCity.get(i)).getLogin(), Integer.toString(player.get(mapCity.get(i)).getX()), Integer.toString(player.get(mapCity.get(i)).getY()), Integer.toString(player.get(mapCity.get(i)).getLife()));
			}
		}
		e.get(num-1).send(false, 0, null, null, null, null);
		
		for(Notif notif : notifs){
			if(notif.getNameReceiver().equals(login)){
				if(notif.getReply().equals("null")){
					e.get(num-1).sendNotifQuestion(notif.getID(), notif.getType(), notif.getNameSender());
				}else{
					e.get(num-1).sendNotifInfo(notif.getID(), notif.getType(), notif.getReply(), notif.getNameSender());
				}
			}else if(notif.getNameSender().equals(login)){
				e.get(num-1).sendFriendWait(notif.getNameReceiver());
			}
		}
	}
	
	synchronized static int getTotal () {
		return num;
	}
	
	synchronized static String getInfo (int i) {
		String rep = "player " + i + " :  // map = " + player.get(i).getMap() + " //";
		return rep;
	}
	
	//synchronized static String getPlayerX (int i) {
		//return playerX.get(i);
	//}
	
	//synchronized static String getPlayerY (int i) {
		//return playerY.get(i);
	//}
	
	synchronized static void setPlayer (int a, String pos, int i) {
		if(a == 0){
			player.get(i).setX(Integer.parseInt(pos));
		}else if(a == 1){
			player.get(i).setY(Integer.parseInt(pos));
		}
		
		List<Integer> mL = null;
		if(player.get(i).getLobby() == -1 || player.get(i).isHUB()){
			mL = mapList.get(player.get(i).getMap());
		}else{
			mL = lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap()).getPlayer();
		}
		
		int p = 0;
		for(int pi=0; pi<mL.size(); pi++){
			if(mL.get(pi) == i){
				p = pi;
			}
		}
		
		for(int z=0; z<mL.size(); z++){
			if(i != mL.get(z)){
				//System.out.println("player : " + mL.get(z) + " send : " + i);
				e.get(mL.get(z)).send(p, a, pos);
			}
		}
	}
	
	synchronized static void setPlayer (int a, int i) {
		List<Integer> mL = null;
		if(player.get(i).getLobby() == -1 || player.get(i).isHUB()){
			mL = mapList.get(player.get(i).getMap());
		}else{
			mL = lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap()).getPlayer();
		}
		
		int p = 0;
		for(int pi=0; pi<mL.size(); pi++){
			if(mL.get(pi) == i){
				p = pi;
			}
		}
		
		for(int z=0; z<mL.size(); z++){
			if(i != mL.get(z) && !player.get(mL.get(z)).isChangeMap()){
				e.get(mL.get(z)).send(p, a);
			}
		}
			
	}
	
	synchronized static void setPlayer (String map, String x, String y, int i) {
		player.get(i).setChangeMap(true);
		player.get(i).setX(Integer.parseInt(x));
		player.get(i).setY(Integer.parseInt(y));
		StringTokenizer mapWord = new StringTokenizer(map);
		String mapType = mapWord.nextToken();
		int preMapID = player.get(i).getMap();
		int nextMapID = Integer.parseInt(mapWord.nextToken());
		if(mapType.equals("Lobby")){
			if(player.get(i).getLobby() == -1){
				Createlobby(i);
			}
			lobby.get(player.get(i).getLobby()).setMaps(mapInfo.get(nextMapID));
			boolean isFight = lobby.get(player.get(i).getLobby()).getFight(0);
			//lobby.get(player.get(i).getLobby()).setMapList(mapTestList);
			List<Integer> allPlayer = lobby.get(player.get(i).getLobby()).getSender();
			List<Monster> monster = lobby.get(player.get(i).getLobby()).getMonsterList(0);
			for(int p=0; p<allPlayer.size(); p++){
				player.get(allPlayer.get(p)).setChangeMap(true);
				player.get(allPlayer.get(p)).setX(player.get(i).getX());
				player.get(allPlayer.get(p)).setY(player.get(i).getY());
			}
			sendAllPlayerList(nextMapID, allPlayer, true, isFight, monster);
			for(int pi=0; pi<allPlayer.size(); pi++){
				mapList.get(player.get(pi).getMap()).remove(allPlayer.get(pi));
				player.get(allPlayer.get(pi)).setMap(0);
				lobby.get(player.get(allPlayer.get(pi)).getLobby()).getMap(0).getPlayer().add(allPlayer.get(pi));
				player.get(allPlayer.get(pi)).setHUB(false);
				player.get(allPlayer.get(pi)).setChangeMap(false);
			}
			lobby.get(player.get(i).getLobby()).setStart(0);
			
		}else if(mapType.equals("LobbyMap")){
			int p = 0;
			for(int pi=0; pi<lobby.get(player.get(i).getLobby()).getMap(preMapID).getPlayer().size(); pi++){
				if(lobby.get(player.get(i).getLobby()).getMap(preMapID).getPlayer().get(pi) == i){
					p = pi;
				}
			}
			boolean isFight = false;
			if(lobby.get(player.get(i).getLobby()).getMonsterList(nextMapID).size() > 0){
				isFight = true;
			}
			//boolean isFight = lobby.get(player.get(i).getLobby()).getFight(nextMapID);
			List<Monster> monster = lobby.get(player.get(i).getLobby()).getMonsterList(nextMapID);
			sendPlayerList(nextMapID, i, true, isFight, monster);
			
			lobby.get(player.get(i).getLobby()).endTurn(preMapID, i);
			
			if(lobby.get(player.get(i).getLobby()).getMonsterList(preMapID).size() > 0){
				lobby.get(player.get(i).getLobby()).removeMapPlayer(preMapID, i);
			}else{
				lobby.get(player.get(i).getLobby()).getMap(preMapID).getPlayer().remove(p);
			}
			
			player.get(i).setMap(nextMapID);
			lobby.get(player.get(i).getLobby()).getMap(nextMapID).getPlayer().add(i);

			if(lobby.get(player.get(i).getLobby()).getMap(preMapID).getPlayer().size() <= 0){
				lobby.get(player.get(i).getLobby()).setStop(preMapID);
			}
			if(lobby.get(player.get(i).getLobby()).isStop(nextMapID)){
				lobby.get(player.get(i).getLobby()).setStart(nextMapID);
			}
			
		}else if(mapType.equals("Hub")){
			List<Integer> allPlayer = lobby.get(player.get(i).getLobby()).getSender();
			for(int p=0; p<allPlayer.size(); p++){
				player.get(allPlayer.get(p)).setX(player.get(i).getX());
				player.get(allPlayer.get(p)).setY(player.get(i).getY());
			}
			sendAllPlayerList(nextMapID, allPlayer, false, false, new ArrayList<Monster>());
			for(int pi=0; pi<allPlayer.size(); pi++){
				player.get(allPlayer.get(pi)).setMap(nextMapID);
				mapList.get(nextMapID).add(pi);
				player.get(allPlayer.get(pi)).setHUB(true);
			}
			lobby.get(player.get(i).getLobby()).DestroyMap();
			
		}else if(mapType.equals("HubMap")){
			int p = 0;
			for(int pi=0; pi<mapList.get(preMapID).size(); pi++){
				if(mapList.get(preMapID).get(pi) == i){
					p = pi;
				}
			}
			sendPlayerList(nextMapID, i, false, false, new ArrayList<Monster>());
			mapList.get(preMapID).remove(p);
			player.get(i).setMap(nextMapID);
			mapList.get(nextMapID).add(i);
		}
		player.get(i).setChangeMap(false);
	}
	
	synchronized static void sendPlayerList (int nextMap, int z, boolean ifLobby, boolean isFight, List<Monster> monster) {
		List<Integer> mL = null;
		List<Integer> nML = null;
		if(ifLobby){
			mL = lobby.get(player.get(z).getLobby()).getMap(player.get(z).getMap()).getPlayer();
			nML = lobby.get(player.get(z).getLobby()).getMap(nextMap).getPlayer();
		}else{
			mL = mapList.get(player.get(z).getMap());
			nML = mapList.get(nextMap);
		}
		
		//suppression du joueur qui arrive pour tous les joueurs sur la carte précédente
				int p = 0;
				for(int pi=0; pi<mL.size(); pi++){
					if(mL.get(pi) == z){
						p = pi;
					}
				}
				for(int i=0; i<mL.size(); i++){
					if(i != p){
						e.get(mL.get(i)).sendDelete(p);
					}
				}
		
		//ajout pour tous les joueurs déja présent
		for(int i=0; i<nML.size(); i++){
			e.get(nML.get(i)).send(player.get(z).getLogin(), Integer.toString(player.get(z).getX()), Integer.toString(player.get(z).getY()), Integer.toString(player.get(z).getLife()));
		}
		
		//ajout de tous les joueurs déja présent pour le joueur qui arrive
		e.get(z).send(true, isFight);
		for(int i=0; i<nML.size(); i++){
			e.get(z).send(true, 0, player.get(nML.get(i)).getLogin(), Integer.toString(player.get(nML.get(i)).getX()), Integer.toString(player.get(nML.get(i)).getY()), Integer.toString(player.get(nML.get(i)).getLife()));
		}
		e.get(z).send(true, 1, player.get(z).getLogin(), Integer.toString(player.get(z).getX()), Integer.toString(player.get(z).getY()), Integer.toString(player.get(z).getLife()));
		e.get(z).send(false, 0, null, null, null, null);
		
		//ajout des monstres
		if(isFight){
			for(int i2=0; i2<monster.size(); i2++){
				e.get(z).sendAllMonster(true, monster.get(i2).getId(), monster.get(i2).getX(), monster.get(i2).getY());
			}
			e.get(z).sendAllMonster(false, 0, 0, 0);
		}
	}
	
	synchronized static void sendAllPlayerList (int nextMap, List<Integer> allPlayer, boolean ifLobby, boolean isFight, List<Monster> monster) {
		for(int i=0; i<allPlayer.size(); i++){
			int z = allPlayer.get(i);
			List<Integer> mL = mapList.get(player.get(z).getMap());
			List<Integer> nML = null;
			
			if(ifLobby){
				//suppression du joueur qui arrive pour tous les joueurs sur la carte précédente
				int p = 0;
			
				for(int pi=0; pi<mL.size(); pi++){
					if(mL.get(pi) == z){
						p = pi;
					}
				}
				//System.out.println("id : " + p);
				for(int i2=0; i2<mL.size(); i2++){
					boolean inAllPlayer = false;
					for(int i3=0; i3<allPlayer.size(); i3++){
						if(mL.get(i2) == allPlayer.get(i3)){
							inAllPlayer = true;
						}
					}
					if(!inAllPlayer){
						for(int ip=0; ip<allPlayer.size(); ip++){
							System.out.println("Delete");
							e.get(mL.get(i2)).sendDelete(p);
						}
					}
				}
			}
			
			if(!ifLobby){
				//ajout pour tous les joueurs déja présent
				nML = mapList.get(nextMap);
				for(int i2=0; i2<nML.size(); i2++){
					e.get(nML.get(i2)).send(player.get(z).getLogin(), Integer.toString(player.get(z).getX()), Integer.toString(player.get(z).getY()), Integer.toString(player.get(z).getLife()));
				}
			}
			
			//ajout de tous les joueurs déja présent pour le joueur qui arrive
			e.get(z).sendAll(true, nextMap, isFight);
			
			if(!ifLobby){
				for(int i3=0; i3<nML.size(); i3++){
					e.get(z).send(true, 0, player.get(nML.get(i3)).getLogin(), Integer.toString(player.get(nML.get(i3)).getX()), Integer.toString(player.get(nML.get(i3)).getY()), Integer.toString(player.get(nML.get(i3)).getLife()));
				}
			}
			
			for(int i2=0; i2<allPlayer.size(); i2++){
				int z2 = allPlayer.get(i2);
				if(z != z2){
					e.get(z).send(true, 0, player.get(allPlayer.get(i2)).getLogin(), Integer.toString(player.get(allPlayer.get(i2)).getX()), Integer.toString(player.get(allPlayer.get(i2)).getY()), Integer.toString(player.get(allPlayer.get(i2)).getLife()));
				}else{
					e.get(z).send(true, 1, player.get(z).getLogin(), Integer.toString(player.get(z).getX()), Integer.toString(player.get(z).getY()), Integer.toString(player.get(z).getLife()));
				}
			}
			e.get(z).send(false, 0, null, null, null, null);
			/*
			//Ajout des monstres
			if(isFight){
				for(int i2=0; i2<monster.size(); i2++){
					e.get(z).sendAllMonster(true, monster.get(i2).getX(), monster.get(i2).getY());
				}
				e.get(z).sendAllMonster(false, 0, 0);
			}
				for(int i2=0; i2<allPlayer.size(); i2++){
			}
			
			*/
			//*/
		}
	}
	
	public synchronized static void setLife (int i, String l, boolean b) {
		player.get(i).setLife(Integer.parseInt(l));
		if(player.get(i).getLobby() != -1 && !player.get(i).isHUB()){
			List<Integer> allPlayer = lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap()).getPlayer();
			int ip = 0;
			
			for(int pi=0; pi<allPlayer.size(); pi++){
				if(allPlayer.get(pi) == i){
					ip = pi;
				}
			}
			
			if(b){
				for(int p=0; p<allPlayer.size(); p++){
					if(allPlayer.get(p) != i){
						e.get(allPlayer.get(p)).sendLife(ip, l);
					}
				}
			}else{
				for(int p=0; p<allPlayer.size(); p++){
					e.get(allPlayer.get(p)).sendLife(ip, l);
				}
			}
			
			if(player.get(i).getLife() <= 0){
				lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap()).playerDead(ip);
			}
		}else{
			List<Integer> allPlayer = mapList.get(player.get(i).getMap());
			int ip = 0;
			
			for(int pi=0; pi<allPlayer.size(); pi++){
				if(allPlayer.get(pi) == i){
					ip = pi;
				}
			}
			
			if(b){
				for(int p=0; p<allPlayer.size(); p++){
					if(allPlayer.get(p) != i){
						e.get(allPlayer.get(p)).sendLife(ip, l);
					}
				}
			}else{
				for(int p=0; p<allPlayer.size(); p++){
					//e.get(allPlayer.get(p)).sendLife(ip, l);
				}
			}
		}
		if(player.get(i).getLife() <= 0){
			disconnect(i, true);
		}
	}
	
	synchronized static void endTurn (int i) {
		lobby.get(player.get(i).getLobby()).endTurn(player.get(i).getMap(), i);
	}
	
	synchronized static void sendTurn (int i) {
		e.get(i).sendTurn();
	}

	synchronized static void sendMonster(int monster, List<Integer> playerList, int x, int y) {
		for(int i=0; i < playerList.size(); i++){
			e.get(playerList.get(i)).sendMonster(monster, x, y);
		}
	}

	public synchronized static void sendMonsterTurn(int monster, int a, List<Integer> playerList) {
		for(int i=0; i < playerList.size(); i++){
			e.get(playerList.get(i)).sendMonsterTurn(monster, a);
		}

	}
	
	synchronized static void sendMonsterLife(int monster, List<Integer> playerList, int l) {
		for(int i=0; i < playerList.size(); i++){
			e.get(playerList.get(i)).sendLifeMonster(monster, Integer.toString(l));
		}
	}

	synchronized static List<Player> getPlayerList(List<Integer> playerList) {
		List<Player> Player = new ArrayList<Player>();
		for(int i=0; i < playerList.size(); i++){
			Player.add(player.get(playerList.get(i)));
		}
		return Player;
	}
	
	synchronized static void setAction(int i, int value, int idAction) {
		Action action = null;
		try {
			action = (Action)Class.forName("Action." + actions.get(idAction)).getConstructor(int.class, int.class).newInstance(i, value);
			lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap()).setAction(action);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//actions.get(action).use(i, value);
	}

	//synchronized static void monsterSetLife(int i, int dir, String l) {
		//lobby.get(player.get(i).getLobby()).monsterSetLife(player.get(i), dir, Integer.parseInt(l), i);
		//for(int p : lobby.get(player.get(i).getLobby()).getMap(player.get(i).getMap())){
			//e.get(p).sendAction(0);;
		//}
	//}
	
	synchronized static void stopFight (List<Integer> playerList){
		for(int i=0; i < playerList.size(); i++){
			e.get(playerList.get(i)).stopFight();
		}
	}
	
	synchronized static void Createlobby (int i) {
		List<Integer> play = new ArrayList<Integer>();
		play.add(i);
		lobby.add(new Lobby(play));
		player.get(i).setLobby(lobby.size()-1);
		lobby.get(player.get(i).getLobby()).setLeaderName(player.get(i).getLogin());
	}
	
	synchronized static void addLobby (int i, int p) {
		for(int x : lobby.get(player.get(i).getLobby()).getSender()){
			e.get(x).sendNewTeam(player.get(p).getLogin());
			e.get(p).sendTeam(true, player.get(x).getLogin(), null);
		}
		e.get(p).sendTeam(false, null, lobby.get(player.get(i).getLobby()).getLeaderName());
		lobby.get(player.get(i).getLobby()).addPlayer(p);
		player.get(p).setLobby(player.get(i).getLobby());
	}
	
	synchronized static boolean deleteLobby (String name, int p) {
		Lobby lob = lobby.get(player.get(p).getLobby());
		int i = -1;
		for(int x : lob.getSender()){
			if(player.get(x).getLogin().equals(name)){
				i = x;
			}else{
				e.get(x).sendDeleteTeam(name);
			}
		}
		
		if(i != -1){
			e.get(i).sendDeleteAllTeam();
			lob.removePlayer(i);
			player.get(i).setLobby(-1);
		}
		
		if(name.equals(lob.getLeaderName()) && lob.getSender().size() > 0){
			String newLeaderName = player.get(lob.getSender().get(0)).getLogin();
			lob.setLeaderName(newLeaderName);
			for(int x : lob.getSender()){
				e.get(x).sendLeader(newLeaderName);
			}
		}
		
		boolean b = true;
		if(lob.getSender().size() <= 0){
			lobby.remove(lob);
			b = false;
		}
		return b;
	}

	public static void disconnect(int numP, boolean isDead) {
		if(!player.get(numP).isHUB()){
			List<Integer> map = lobby.get(player.get(numP).getLobby()).getMap(player.get(numP).getMap()).getPlayer();
			int p = 0;
			for(int i=0; i<map.size(); i++){
				if(i != numP){
					if(!isDead){
						e.get(map.get(i)).sendDelete(numP);
					}
				}else{
					p = i;
				}
			}		
			
			if(player.get(numP).getLife() > 0){
				if(map.size()-1 > 0){
					lobby.get(player.get(numP).getLobby()).endTurn(player.get(numP).getMap(), numP);
				}else{
					lobby.get(player.get(numP).getLobby()).endTurnFinal(player.get(numP).getMap());
				}
			}else{
				lobby.get(player.get(numP).getLobby()).endTurnFinal(player.get(numP).getMap());
			}
			
			lobby.get(player.get(numP).getLobby()).removeMapPlayer(player.get(numP).getMap(), numP);
			//map.remove(p);
			
			for(int i = 0; i<map.size(); i++){
				if(map.get(i)>numP){
					map.set(i, map.get(i)-1);
				}
			}
			
			if(map.size() <= 0){
				lobby.get(player.get(numP).getLobby()).setStop(player.get(numP).getMap());
			}
			/*
			for(int i = 0; i<lobby.get(player.get(numP).getLobby()).getSender().size(); i++){
				if(lobby.get(player.get(numP).getLobby()).getSender().get(i)>numP){
					lobby.get(player.get(numP).getLobby()).getSender().set(i, lobby.get(player.get(numP).getLobby()).getSender().get(i)-1);
				}
			}


			lobby.get(player.get(numP).getLobby()).getSender().remove(p);
			if(lobby.get(player.get(numP).getLobby()).getSender().size() <= 0){
				lobby.remove(player.get(numP).getLobby());
			}
			*/
		
		}else{
			List<Integer> map = mapList.get(player.get(numP).getMap());
			if(!isDead){
				for(int i=0; i<map.size(); i++){
					if(map.get(i) != numP){
						e.get(map.get(i)).sendDelete(numP);
					}
				}
			}
			for(int i = 0; i<mapList.get(player.get(numP).getMap()).size(); i++){
				if(mapList.get(player.get(numP).getMap()).get(i)>numP){
					mapList.get(player.get(numP).getMap()).set(i, mapList.get(player.get(numP).getMap()).get(i)-1);
				}
			}
			mapList.get(player.get(numP).getMap()).remove(Integer.valueOf(numP));
			
		}
		
		for(int i = 0; i<r.size(); i++){
			if(i>numP){
				r.get(i).changeNum();
			}
		}
		
		String login = player.get(numP).getLogin();
		if(player.get(numP).getLobby() != -1){
			int l = player.get(numP).getLobby();
			boolean b = deleteLobby(login, numP);
			if(b){
				lobby.get(l).changeNum(numP);
			}
		}
		r.get(numP).stop();
		r.remove(numP);
		e.remove(numP);
		socket.remove(numP);
		savePlayer(player.get(numP));
		player.remove(numP);
		num--;
		System.out.println(login + " vient de se déconnecter");
		
	}

	public static void setInventory(String invS, String equS, int i) {
		List<Integer> inv = new ArrayList<Integer>();
		List<Integer> equ = new ArrayList<Integer>();
		StringTokenizer line = new StringTokenizer(invS);
		while(line.hasMoreTokens()){
			int temp = Integer.parseInt(line.nextToken());
			inv.add(temp);
		}
		line = new StringTokenizer(equS);
		while(line.hasMoreTokens()){
			equ.add(Integer.parseInt(line.nextToken()));
		}
		player.get(i).setInventory(inv, equ);
	}
	
	public static void setFriend(String friS, int i) {
		List<String> fri = new ArrayList<String>();
		StringTokenizer line = new StringTokenizer(friS);
		while(line.hasMoreTokens()){
			fri.add(line.nextToken());
		}
		player.get(i).setFriend(fri);
	}
	
	synchronized public static void notifQuestion(String type, String name, int num) {
		notifs.add(new Notif(notifID, type, player.get(num).getLogin(), name));
		int p = -1;
		for(int i = 0; i<player.size(); i++){
			if(player.get(i).getLogin().equals(name)){
				p = i;
				i = player.size();
			}
		}
		if(p >= 0){
			e.get(p).sendNotifQuestion(notifID, type, player.get(num).getLogin());
		}else{
			
		}
		notifID++;
		
		if(type.equals("team") && player.get(num).getLobby() == -1){
			Createlobby(num);
		}
	}
	
	synchronized public static void notifInfo(String type, String info, String name, int num) {
		notifs.add(new Notif(notifID, type, info, player.get(num).getLogin(), name));
		int p = -1;
		for(int i = 0; i<player.size(); i++){
			if(player.get(i).getLogin().equals(name)){
				p = i;
				i = player.size();
			}
		}
		if(p >= 0){
			e.get(p).sendNotifInfo(notifID, type, info, player.get(num).getLogin());
		}else{
			
		}
		notifID++;
		
		if(type.equals("teamReply") && info.equals("yes")){
			addLobby(p, num);
		}
	}
	
	synchronized public static void notifDelete(int id) {
		int n = -1;
		for(int i = 0; i<notifs.size(); i++){
			if(notifs.get(i).getID() == id){
				n = i;
				i = notifs.size();
			}
		}
		
		notifs.remove(n);
	}
	
	synchronized public static String getAction(int id) {
		return actions.get(id);
	}
	
	synchronized public static Action[] getActionList (String[] actionsString){
		Action[] actions = new Action[actionsString.length];
		int i=0;
		for(String action : actionsString){
			try {
				actions[i] = (Action)Class.forName("Action." + action).getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		return actions;
	}
	
	public static void notif() {
		for(Notif notif : notifs){
			System.out.println(notif.getID() + " : " + notif.getNameReceiver() + " : " + notif.getNameSender() + " : " + notif.getType() + " : " + notif.getReply());
		}
	}
	
	public static void lobby() {
		int i = 0;
		for(Lobby l: lobby){
			System.out.println("Lobby" + " : " + i);
			for(int x : l.getSender()){
				System.out.println(x);
			}
			System.out.println("///");
			i++;
		}
	}

	public static void quit() {
		System.out.println("Sauvegarde des données joueur...");
		for(int i = 0; i<player.size(); i++){
			savePlayer(player.get(i));
		}
		System.out.println("Sauvegarde des données notif...");
		saveNotif();
		System.out.println("Sauvegarde des données joueur terminé");
		System.out.println("Coupure du serveur");
		System.exit(0);
	}
	
	public static void test() {
		System.out.println(lobby.get(0).getLeaderName());
	}
	
	private static void savePlayer(Player p) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("Players/" + p.getLogin() + ".txt", "UTF-8");
			//writer.println("HP = " + p.getLife());
			//writer.println("MP = " + p.getMana());
			writer.println("HP = " + 50);
			writer.println("MP = " + 50);
			writer.println("INV:");
			for(int item : p.getInv()){
				writer.println(item);
			}
			writer.println("EQU:");
			for(int item : p.getEqu()){
				writer.println(item);
			}
			writer.println("FRI:");
			for(String name : p.getFri()){
				writer.println(name);
			}
			writer.println("END");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void saveNotif() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("Notifs.txt", "UTF-8");
			for(Notif notif : notifs){
				if(notif.getType() == "friend" || notif.getType() == "friendReply" || notif.getType() == "friendDelte"){
					writer.println(notif.getType() + " " + notif.getReply() + " " + notif.getNameSender() + " " + notif.getNameReceiver());
				}
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

