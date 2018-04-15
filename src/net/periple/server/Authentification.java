package net.periple.server;

import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;

public class Authentification implements Runnable {

	private Socket socket;
	//private DatagramSocket Dsocket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private String login = "*", pass =  "*";
	public boolean authentifier = false;
	public Thread t2;
	
	public Authentification(Socket s){
		 socket = s;
	}
	
	public void run() {
	
		try {
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			
		while(!authentifier){
			login = in.readLine();
			pass = in.readLine();

			if(isValidLogin(login)){
				if(isValidPass(login, pass)){
					out.println("connecte");
					System.out.println(login +" vient de se connecter");
					out.flush();
					authentifier = true;
					t2 = new Thread(new ClientServeur(socket, login, true));
				}else{
					out.println("invalide");
					out.flush();
				}
			}else{
				//out.println("new connecte"); 
				//out.flush();
				
				FileWriter writer;
				try {
					writer = new FileWriter("Players.txt", true);
					writer.write(login + " " + pass);
					writer.write(System.getProperty("line.separator"));
					writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(login + " vient de se connecter");
				authentifier = true;
				//System.out.println(login +" s'est déconnectée");
				out.println("connecte"); 
				out.flush();
				t2 = new Thread(new ClientServeur(socket, login, false));
			}
		 }
			t2.start();
			
		} catch (IOException e) {
			
			System.err.println(login+" ne répond plus !");
		}
	}
	
	private static boolean isValidLogin(String login) {
		boolean connexion = false;
		try {
			Scanner sc = new Scanner(new File("Players.txt"));
			while(sc.hasNext()){
				StringTokenizer line = new StringTokenizer(sc.nextLine());
				if(line.nextToken().equals(login)){
              	  connexion=true;
				  break;
				}
             }
			sc.close();
		} catch (FileNotFoundException e) {	
			System.err.println("Le fichier n'existe pas !");
		}
		return connexion;
	}
	
	private static boolean isValidPass(String login, String pass) {
		boolean connexion = false;
		try {
			Scanner sc = new Scanner(new File("Players.txt"));
			while(sc.hasNext()){
				if(sc.nextLine().equals(login+" "+pass)){
              	  connexion=true;
				  break;
				}
             }
			sc.close();
		} catch (FileNotFoundException e) {	
			System.err.println("Le fichier n'existe pas !");
		}
		return connexion;
	}

}
