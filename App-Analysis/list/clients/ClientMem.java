package list.clients;

import com.example.dimo.updclient.Data;

public class ClientMem {
	public String ID;
	public Data data;
	
	public ClientMem(Data data){
		this.data=data;
		this.ID=data.getID_ANDROID();
	}
	
	public void upDateMem(Data data) {
		this.data=data;
	}

}
