package udpserver;


import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import com.example.dimo.updclient.Data;

import org.eclipse.paho.client.mqttv3.MqttException;

public class UdpServerThread extends Thread {

        //Gateway - client
    private int serverport;        
    protected DatagramSocket socket = null;
    private ServerGUI gui;
    
        //Gateway - cloud
    protected Subscriber sub = null;
    
    public UdpServerThread(ServerGUI serverGUI) throws IOException, MqttException, URISyntaxException {
        this("UdpServerThread", serverGUI);
        
    }

    public UdpServerThread(String name, ServerGUI serverGUI) throws IOException, MqttException, URISyntaxException {
        super(name);
        this.gui = serverGUI;
        //run gateway waiting for client
        this.serverport = Integer.parseInt(this.gui.txtPort.getText());
        socket = new DatagramSocket(serverport);
		// TODO Auto-generated constructor stub
        this.gui.txtInfo.append("Server start\n");
        this.gui.txtInfo.append("Runtime Java: " 
                + System.getProperty("java.runtime.version")+"\n");
        this.gui.txtInfo.append("JavaUdpServer run on: " + serverport+"\n");

        //information to cloud MQTT
        sub = new Subscriber(new URI("mqtt://dmgjgrud:Jx6mrmDh3j7p@m13.cloudmqtt.com:17231"));
    }
    
    public void Disconnect() {
    	this.sub.disconnectClound();
    	this.socket.close();
    }

    
    @Override
    public void run() {
        while(!socket.isClosed()){

            try {
                byte[] buf = new byte[2048];

                // receive request from client
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                Data data = (Data) Serializer.deserialize(packet.getData());
                
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                this.gui.txtInfo.append("Request from: " + address + ":" + port+" - ID_ANDROID: "+data.getID_ANDROID()+"\n");

                    // send the response to the client at "address" and "port" to cloud MQTT

                sub.sendMessage(data);
                
                buf = "Updated at ".getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
            	socket.send(packet);

            } catch (IOException | MqttException ex) {
                System.out.println(ex.toString());
            } 
        }
    }

}
