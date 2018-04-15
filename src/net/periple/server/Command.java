package net.periple.server;


import java.io.*;

class Command implements Runnable {
  Server _server;
  BufferedReader _in;
  String _strCommande="";
  Thread _t;

  Command(Server server) {
    _server=server;
    _in = new BufferedReader(new InputStreamReader(System.in));
    _t = new Thread(this);
    _t.start();
  }

  public void run() {
    try {
      while ((_strCommande=_in.readLine())!=null){
        if (_strCommande.equalsIgnoreCase("quit")){
        	Server.quit();
        }else if(_strCommande.equalsIgnoreCase("total")){
          System.out.println("Nombre de connectes : "+Server.getTotal());
          System.out.println("--------");
        }else if(_strCommande.equalsIgnoreCase("info")){
        	if((_strCommande=_in.readLine()) != null){
        		System.out.println(Server.getInfo(Integer.parseInt(_strCommande.toString())));
        	}
        	System.out.println("--------");
        }else if(_strCommande.equalsIgnoreCase("notif")){
        	Server.notif();
        	System.out.println("--------");
        }else if(_strCommande.equalsIgnoreCase("lobby")){
        	Server.lobby();
        	System.out.println("--------");
        }else if(_strCommande.equalsIgnoreCase("help")){
        	Server.test();
        	//System.out.println("Quitter : \"quit\"");
            //System.out.println("Nombre de connectes : \"total\"");
            //System.out.println("Afficher les notifs : \"notif\"");
            //System.out.println("--------");
        }else{
          System.out.println("Cette commande n'est pas supportée");
          System.out.println("Pour afficher la liste des commandes disponnible, taper \"help\"");
          System.out.println("--------");
        }
        System.out.flush();
      }
    }
    catch (IOException e) {}
  }
}


