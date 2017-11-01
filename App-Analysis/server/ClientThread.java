package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientThread extends Thread {
	private AnalysisServer server = null;
	private Socket client = null;
	public String ID_ANDROID;
	public int ID;
	public boolean isExist = true;
	
	private ObjectInputStream inFromClient = null;
	private ObjectOutputStream outToClient = null;
	private ServerGUI gui = null;
	
	public ClientThread(AnalysisServer server, Socket client, ServerGUI gui) {
		super();
		this.server = server;
		this.client = client;
		this.gui = gui;
		this.ID = client.getPort();
	}
	
	public void Open(){
		try {
			this.outToClient = new ObjectOutputStream(this.client.getOutputStream());
			this.outToClient.flush();
			this.inFromClient = new ObjectInputStream(this.client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		this.gui.txtInfo.append("Client port " + this.ID + " connected!!\n");
		while(isExist){
			try {
				String info = (String) this.inFromClient.readObject();
				this.ID_ANDROID=info;
				this.server.handleInfo(info, this.ID);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
	}
	public void SendMessage(String m){
		try {
			this.outToClient.writeObject(m);
			this.outToClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Close(){
		try {
			this.isExist = false;
			if (this.client!=null)
				client.close();
			if (this.inFromClient!=null)
				inFromClient.close();
			if (this.outToClient!=null)
				outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
