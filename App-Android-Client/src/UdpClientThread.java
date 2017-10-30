package com.example.dimo.updclient;

/**
 * Created by dimo on 24/10/2017.
 */

import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread {

    String dstAddress;
    int dstPort;
    private boolean stopUDP;
    MainActivity.UdpClientHandler handler;

    DatagramSocket socket;
    InetAddress address;
    Data data;

    public UdpClientThread(Data data, String addr, int port, MainActivity.UdpClientHandler handler) {
        super();
        this.data = data;
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    public void UpdateData(Data data){
        this.data = data;
    }

    public void setRunning(boolean running){
        this.stopUDP = running;
    }


    @Override
    public void run() {
        stopUDP = false;
        while (true) {
            try {
                Thread.sleep(2000);
                socket = new DatagramSocket();
                address = InetAddress.getByName(dstAddress);

                // send request
                byte[] buffer_sent = Serializer.serialize(data);

                DatagramPacket packet =
                        new DatagramPacket(buffer_sent, buffer_sent.length, address, dstPort);
                socket.send(packet);


                byte[] bufrec = new byte[256];
                // get response
                packet = new DatagramPacket(bufrec, bufrec.length);


                socket.receive(packet);
                String line = new String(packet.getData(), 0, packet.getLength());

                handler.sendMessage(
                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line));

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (stopUDP) {
                    socket.close();
                    handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
                    break;
                }
            }
        }
    }
}
