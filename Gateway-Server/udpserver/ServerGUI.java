package udpserver;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JScrollPane;

public class ServerGUI {

	private JFrame frmServer;
	private JScrollPane scrollPane;
	private UDPServer server;
	
	public JTextArea txtInfo;
	public JTextField txtPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (server!=null)
					server.StopServer();
			}
		});
		frmServer.setTitle("Gateway Server - BKUCSE");
		frmServer.setBounds(100, 100, 520, 520);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		JButton btnStop, btnStart, btnClearHis;
		
		btnStart = new JButton("Start");
		btnStop = new JButton("Stop");
		btnClearHis = new JButton("Clear History");
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnStart_Click();
				} catch (MqttException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				btnStart.setEnabled(false);
				txtPort.setEnabled(false);
				btnStop.setEnabled(true);
			}
		});
		
		
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (server!=null)
					server.StopServer();
				btnStart.setEnabled(true);
				txtPort.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
		
		
		
		btnClearHis.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				txtInfo.setText("Gateway - BKUCSE\n");;
			}
		});
		
		
		
		
		btnStart.setBounds(10, 10, 100, 25);
		frmServer.getContentPane().add(btnStart);
		
		btnStop.setBounds(110, 10, 100, 25);
		frmServer.getContentPane().add(btnStop);
		
		btnClearHis.setBounds(210, 10, 150, 25);
		frmServer.getContentPane().add(btnClearHis);
		
		txtPort = new JTextField();
		txtPort.setBounds(400, 10, 85, 25);
		txtPort.setText("4445");
		frmServer.getContentPane().add(txtPort);
		
		JLabel lblInfo = new JLabel("Infomation:");
		lblInfo.setBounds(10, 40, 100, 25);
		frmServer.add(lblInfo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 70, 500, 400);
		frmServer.getContentPane().add(scrollPane);
		
		this.txtInfo = new JTextArea();
		scrollPane.setViewportView(txtInfo);
		txtInfo.setEditable(false);
	}
	
	private void btnStart_Click() throws MqttException, URISyntaxException{
		try {
			server = new UDPServer(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
