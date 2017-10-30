package com.example.dimo.updclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Created by dimo on 26/10/2017.
 */
import java.net.Socket;

public class TCPClient {

    private String serverMessage;
    public static String SERVERIP ; //your computer IP address
    public static int SERVERPORT;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    Socket client = null;

    private ObjectInputStream inFromServer = null;
    private ObjectOutputStream outToServer = null;

    public void Open(){
        try {
            outToServer = new ObjectOutputStream(client.getOutputStream());
            outToServer.flush();
            inFromServer = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        try {
            this.outToServer.writeObject(message);
            this.outToServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run(String ID_ANDROID) {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            client = new Socket(serverAddr, SERVERPORT);
            this.Open();

            try {
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    this.sendMessage(ID_ANDROID);

                    serverMessage = (String) inFromServer.readObject();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                }
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                outToServer.close();
                inFromServer.close();
                client.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
