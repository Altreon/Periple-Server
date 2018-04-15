package net.periple.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


public class ClientServeur implements Runnable {

	private String login;
	private boolean newPlayer;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	//private DatagramSocket Dsocket = null;
	//private InetAddress ip = null;
	//private int port;
	private Thread t3, t4;
	private Reception r;
	private Emission e;
	
	public ClientServeur(Socket s, String login, boolean newPlayer){
		socket = s;
		this.login = login;
		this.newPlayer = newPlayer;
	}
	public void run() {
		File f = new File("Players");
		if (f.exists() && f.isDirectory()) {
			
		}else{
			new File("Players").mkdirs();
		}
		
		int HP = 50;
		int MP = 50;
		//int x = 2*32;
		//int y = 9*32;
		List<Integer> inv = new ArrayList<Integer>();
		String invS = "";
		List<Integer> equ = new ArrayList<Integer>();
		String equS = "";
		List<String> fri = new ArrayList<String>();
		String friS = "";
		if(newPlayer){
			try {
				Scanner sc = new Scanner(new File("Players/"+login+".txt"));
				
				String line = sc.nextLine();
				StringTokenizer arg = new StringTokenizer(line);
				arg.nextToken();
				arg.nextToken();
				HP = Integer.parseInt(arg.nextToken());
				
				line = sc.nextLine();
				arg = new StringTokenizer(line);
				arg.nextToken();
				arg.nextToken();
				MP = Integer.parseInt(arg.nextToken());
				
				/*
				line = sc.nextLine();
				arg = new StringTokenizer(line);
				arg.nextToken();
				arg.nextToken();
				x = Integer.parseInt(arg.nextToken());
				arg.nextToken();
				y = Integer.parseInt(arg.nextToken());
				*/
				
				sc.nextLine();
				line = sc.nextLine();
				while(!line.equals("EQU:")){
					inv.add(Integer.parseInt(line));
					invS = invS + line + " ";
					line = sc.nextLine();
				}
				line = sc.nextLine();
				while(!line.equals("FRI:")){
					equ.add(Integer.parseInt(line));
					equS = equS + line + " ";
					line = sc.nextLine();
				}
				line = sc.nextLine();
				while(!line.equals("END")){
					fri.add(line);
					friS = friS + line + " ";
					line = sc.nextLine();
				}
				sc.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			PrintWriter writer;
			try {
				writer = new PrintWriter("Players/" + login + ".txt", "UTF-8");
				writer.println("HP = " + HP);
				writer.println("MP = " + MP);
				writer.println("INV:");
				writer.println("EQU:");
				writer.println("FRI:");
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
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		t3 = new Thread(r = new Reception(in, Server.getTotal()));
		t3.start();
		t4 = new Thread(e = new Emission(out, Server.getTotal()));
		t4.start();
		Server.addPlayer(login, socket, e, r, HP, MP, inv, invS, equ, equS, fri, friS);
		//t3 = new Thread(new Reception(Dsocket, num));
		//t3.start();
		//t4 = new Thread(new Emission(Dsocket, ip, port, num));
		//t4.start();
	}
}
