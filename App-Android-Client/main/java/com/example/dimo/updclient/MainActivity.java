package com.example.dimo.updclient;
/**
 * Created by dimo on 24/10/2017.
 */
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    EditText editTextAddress, editTextPort, editTextIPTCP, editTextPortTCP;
    Button btnUDP, btnStopUDP, btnTCP, btnStopTCP, btnSend, btnGPS;
    TextView textViewRx, txtIP, txtLoc;
    LocationManager locationManager; //
    private LocationListener listener;
    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread = null;



    private ListView mList;
    private ArrayList<String> arrayList;
    private AdapterCus mAdapter;
    private TCPClient mTcpClient;

    Data data;
    int TimeUpdateLoc = 5000;
    String ID_ANDROID, IPTCP, PORTTCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        addControls();

        addEvents();

        addLocalcation();

        udpClientHandler = new UdpClientHandler(this);

        //localcation
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TimeUpdateLoc, 0, this);

    }

    private void addLocalcation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                txtLoc.setText(String.format("Latitude:%s, Longitude:%s", lat, lon));


                System.out.println(ID_ANDROID);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                data = new Data(lat, lon, ID_ANDROID);

                if(udpClientThread!=null)
                    udpClientThread.UpdateData(data);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_buttonSentUDP();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_buttonSentUDP();
                break;
            default:
                break;
        }
    }

    private void configure_buttonSentUDP() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        btnGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnTCP.setEnabled(true);
                btnUDP.setEnabled(true);
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });

    }

    private void StartUDP(){
        udpClientThread = new UdpClientThread(
                data,
                editTextAddress.getText().toString(),
                Integer.parseInt(editTextPort.getText().toString()),
                udpClientHandler);
        udpClientThread.start();
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

    private void addEvents() {
//          final EditText editText = (EditText) findViewById(R.id.editText);
//          btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String message = editText.getText().toString();
//
//                //add the text in the arrayList
//                arrayList.add("c: " + message);
//
//                //sends the message to the server
//                if (mTcpClient != null) {
//                    mTcpClient.sendMessage(message);
//                }
//
//                //refresh the list
//                mAdapter.notifyDataSetChanged();
//                editText.setText("");
//            }
//        });

        btnUDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                StartUDP();
                btnStopUDP.setEnabled(true);
                btnUDP.setEnabled(false);
            }
        });
        btnStopUDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                udpClientThread.setRunning(true);
                btnStopUDP.setEnabled(false);
                btnUDP.setEnabled(true);
            }
        });

        btnTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPTCP = String.valueOf(editTextIPTCP.getText());
                PORTTCP =  String.valueOf(editTextPortTCP.getText());
                btnStopTCP.setEnabled(true);
                btnTCP.setEnabled(false);
                new connectTask().execute("");
            }
        });

        btnStopTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopTCP.setEnabled(false);
                btnTCP.setEnabled(true);
                mTcpClient.sendMessage("disconnected");

            }
        });
    }

    private void addControls() {

        btnGPS = (Button) findViewById(R.id.btnGPS);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        btnUDP = (Button) findViewById(R.id.btnUDP);
        btnTCP = (Button) findViewById(R.id.btnTCP);
        btnTCP.setEnabled(false);
        btnUDP.setEnabled(false);
        textViewRx = (TextView)findViewById(R.id.received);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        editTextIPTCP= (EditText) findViewById(R.id.edittextIPTCP);
        editTextPortTCP= (EditText) findViewById(R.id.edittextPortTCP);

        btnStopTCP = (Button) findViewById(R.id.btnStopTCP);
        btnStopTCP.setEnabled(false);
        btnStopUDP = (Button) findViewById(R.id.btnStopUDP);
        btnStopUDP.setEnabled(false);

        arrayList = new ArrayList<String>();
        btnSend = (Button)findViewById(R.id.send_button);
        btnSend.setEnabled(false);
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new AdapterCus(this, arrayList);
        mList.setAdapter(mAdapter);

        ID_ANDROID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String ip = null;
        try {
            ip = getLocalAddress().getHostAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        txtIP = (TextView) findViewById(R.id.txtIP);
        txtIP.setText(ip+" - "+ ID_ANDROID);
    }


    private void updateRxMsg(String rxmsg){
        textViewRx.setText(rxmsg + new Date().toString());
    }

    private void clientEnd(){
        udpClientThread = null;
        btnUDP.setEnabled(true);

    }

//    @Override
//    public void onLocationChanged(Location location) {
//        double lat = location.getLatitude();
//        double lon = location.getLongitude();
//
//        txtLoc.setText(String.format("Latitude:%s, Longitude:%s", lat, lon));
//
//
//        System.out.println(ID_ANDROID);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        data = new Data(lat, lon, ID_ANDROID);
//
//        if(udpClientThread!=null)
//            udpClientThread.UpdateData(data);
//
//    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    public static class UdpClientHandler extends Handler {
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

    @Override
      protected TCPClient doInBackground(String... message) {

                //we create a TCPClient object and
          mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
               @Override
                //here the messageReceived method is implemented
               public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                    publishProgress(message);
             }
          });
        mTcpClient.SERVERIP = IPTCP;
        mTcpClient.SERVERPORT = Integer.parseInt(PORTTCP);

        mTcpClient.run(ID_ANDROID);

            return null;
      }

        @Override
        protected void onProgressUpdate(String... values) {
           super.onProgressUpdate(values);

        //in the arrayList we add the messaged received from server
            if(arrayList.size() == 0){
                arrayList.add(values[0]);
                mAdapter.notifyDataSetChanged();
                return;
            }
            System.out.println(arrayList.get(arrayList.size() - 1));
            System.out.println(values[0]);
            if(!values[0].equals(arrayList.get(arrayList.size()-1))) {
                arrayList.add(values[0]);
                mAdapter.notifyDataSetChanged();
                return;
            }

        // notify the adapter that the data set has changed. This means that new message received
        // from server was added to the list

        }
    }
}