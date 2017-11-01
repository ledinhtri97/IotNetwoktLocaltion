package server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;

import list.clients.ClientMem;

public class AnalysisServer implements Runnable {

	private ClientThread clients[];
	private ServerSocket server = null;
	private Thread serverThread = null;
	private int portServer = 4446;
	private ServerGUI gui;

	private List<ClientMem> listClients;
	//AppAnalysis - cloud
    protected Subscriber sub = null;
	private int numClient;
	
	public AnalysisServer(ServerGUI ui) throws MqttException, URISyntaxException {
		// TODO Auto-generated constructor stub
		this.clients = new ClientThread[100];
		this.gui = ui;
		
		try {
			this.portServer = Integer.parseInt(this.gui.txtPort.getText());
			this.server = new ServerSocket(this.portServer);

			listClients = new ArrayList<>();
			//sub appanalysis to cloud in order to receice data from cloud
			sub = new Subscriber(new URI("mqtt://dmgjgrud:Jx6mrmDh3j7p@m13.cloudmqtt.com:17231"), this.gui, this.listClients);

			String info = "SERVER running with:\n IP: " + getLocalAddress().getHostAddress()
					+ "\n Port: " + server.getLocalPort() + "\n";
		this.gui.txtInfo.append(info);
		this.StartServer();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void StartServer() {
		// TODO Auto-generated method stub
		if(serverThread == null) {
			serverThread = new Thread(this);
			serverThread.start();
		}
	}
	
	public void StopServer(){
		if (serverThread!=null){
			//serverThread.stop();
			serverThread = null;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Socket c = server.accept();
				addClientWhenRequest(c);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
	}

	private void addClientWhenRequest(Socket c) {
		// TODO Auto-generated method stub
		clients[numClient] = new ClientThread(this, c, this.gui);
		clients[numClient].Open(); //open chat cl1 send request and receice data result
		clients[numClient].start(); 
		numClient++;
	}

	private String Distance(double x0, double x1, double y0, double y1) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(4);
		return df.format(Math.sqrt(
				Math.pow((x0-y0), 2)
				+Math.pow((x1-y1), 2)
				));
	}
	
	private ClientMem findMem(String ID) {
		for(int i = 0; i< listClients.size(); i++) {
			if(listClients.get(i).data.getID_ANDROID().equals(ID)) {
				return listClients.get(i);
			}
		}
		return null;
	}
	
	private String resultHandleDistance(ClientMem mem, String ID_ANDROID) {
		String result = "";
		if(mem == null) return "error 404 : this ID not found";
		for(int i = 0; i < listClients.size(); i++) {
			ClientMem temp = listClients.get(i);
			if(!temp.data.getID_ANDROID().equals(ID_ANDROID)) {
				result += "from you to "+temp.data.getID_ANDROID()+
						": "+Distance(mem.data.getLatitude(),
								mem.data.getLongitude(),
								temp.data.getLatitude(),
								temp.data.getLongitude())+" meters\n";
			}
		}
		return result;
	}
	
	public synchronized void handleInfo(String info, int ID) {
		// TODO Auto-generated method stub
		ClientMem mem = findMem(info);
		if(mem != null) {
			String dis = resultHandleDistance(mem, info);
			ClientThread client = findClient(ID);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(client!=null) client.SendMessage(dis); 
		}
		else if(info.equals("disconnected")) {
			System.out.println("dis "+ID);
			removeClient(ID);
			return;
		}
	}
	
	public void removeClient(int ID){
		for (int i=0; i<numClient; i++){
			if (clients[i].ID == ID){
				ClientThread temp = clients[i];
				this.gui.txtInfo.append("Client port " + ID + " disconnected!!\n");
				for (int j=i; j<numClient-1; j++){
					clients[j] = clients[j+1];
				}
				numClient--;
				temp.Close();
				
				//temp.stop();
			}
		}
	}
	
	private synchronized ClientThread findClient(int ID){
		for (int i=0; i<numClient; i++){
			if (clients[i].ID == ID){
				return clients[i];
			}
		}
		return null;
	}
	
	
	public static InetAddress getLocalAddress() throws SocketException
    {
      Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      while( ifaces.hasMoreElements() )
      {
        NetworkInterface iface = ifaces.nextElement();
        Enumeration<InetAddress> addresses = iface.getInetAddresses();

        while( addresses.hasMoreElements() )
        {
          InetAddress addr = addresses.nextElement();
          if( addr instanceof Inet4Address && !addr.isLoopbackAddress() )
          {
            return addr;
          }
        }
      }

      return null;
    }

}
