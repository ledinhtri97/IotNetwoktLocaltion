package protocol;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	public String type = null;
	public String PeerSender = null;
	public String PeerReceiver = null;
	public String content = null;
	public Message (String type, String sender, String receiver, String content){
		this.type = type;
		this.PeerSender = sender;
		this.PeerReceiver = receiver;
		this.content = content;
	}
	
	public String username = null;
	public String IP = null;
	public int port;
	public Message (String username, String IP, int port){
		this.type = "AddUser";
		this.username = username;
		this.IP = IP;
		this.port = port;
	}
}
