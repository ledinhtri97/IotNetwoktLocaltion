package udpserver;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.paho.client.mqttv3.MqttException;

/*
reference:
https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
*/
public class UDPServer {
    
    static UdpServerThread udpServerThread;
    
    public UDPServer(ServerGUI serverGUI) throws IOException, MqttException, URISyntaxException {
        
        udpServerThread = new UdpServerThread(serverGUI);
        udpServerThread.start();
	}

	public void StopServer(){
		if (udpServerThread!=null){
			//serverThread.stop();
			udpServerThread.Disconnect();
		}
	}
}