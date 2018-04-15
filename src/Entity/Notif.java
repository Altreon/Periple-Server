package Entity;

public class Notif {
	int ID;
	String type;
	String reply = "null";
	String nameSender;
	String nameReceiver;
	
	
	public Notif(int ID, String type, String nameSender, String nameReceiver){
		this.ID = ID;
		this.type = type;
		this.nameSender = nameSender;
		this.nameReceiver = nameReceiver;
	}
	
	public Notif(int ID, String type, String reply, String nameSender, String nameReceiver){
		this.ID = ID;
		this.type = type;
		this.reply = reply;
		this.nameSender = nameSender;
		this.nameReceiver = nameReceiver;
	}
	
	public int getID () {
		return ID;
	}
	
	public String getType () {
		return type;
	}
	
	public String getReply () {
		return reply;
	}
	
	public String getNameSender () {
		return nameSender;
	}
	
	public String getNameReceiver () {
		return nameReceiver;
	}
}