package server;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.example.dimo.updclient.Data;

import list.clients.ClientMem;

import java.net.URI;
import java.util.List;
import java.util.Random;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client blocking API.
 */
public class Subscriber extends Thread implements MqttCallback {

    private final int qos = 0;
    private String topic = "test";
    private MqttClient client;
    private List<ClientMem> listClients;
    private ServerGUI gui;
    
    public Subscriber(URI uri, ServerGUI frame, List<ClientMem> listClients) throws MqttException {
        String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
        String[] auth = this.getAuth(uri);
        String username = auth[0];
        String password = auth[1];
	Random rand = new Random();
	int  id = rand.nextInt(50) + 1;
        String clientId = "Application Analysis - id" + id;
        if (!uri.getPath().isEmpty()) {
            this.topic = uri.getPath().substring(1);
        }

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());

        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);
        
        this.client.subscribe(this.topic, qos);
        
        this.listClients = listClients;
        this.gui = frame;
    }
    

	private boolean addClientFromCloud(List<ClientMem> listClients, Data data) {
		// TODO Auto-generated method stub
		for(int i = 0; i<listClients.size();i++) {
			ClientMem mem = listClients.get(i);
			if(mem.ID.equals(data.getID_ANDROID())){
				mem.upDateMem(data);
				System.out.println("updare info mem :"+data.getID_ANDROID());
				return false;
			}
		}
		listClients.add(new ClientMem(data));
		System.out.println("added android id: " + data.getID_ANDROID() + " num mem = " + listClients.size());
		return true;
	}
	
    private String[] getAuth(URI uri) {
        String a = uri.getAuthority();
        String[] first = a.split("@");
        return first[0].split(":");
    }
 
    public void sendMessage(Data data) throws MqttException {
        MqttMessage message = new MqttMessage(Serializer.serialize(data));
        message.setQos(qos);
        this.client.publish(this.topic, message); // Blocking publish
    }

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
    	Data data = (Data) Serializer.deserialize(message.getPayload());
        //System.out.println(String.format("[%s] - Id = %s", topic, d.getID_ANDROID()));
    	if(addClientFromCloud(listClients, data))
    		gui.txtInfo.append(data.getID_ANDROID()+"\n");
    }

}

