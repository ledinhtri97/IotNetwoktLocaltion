package server;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;

import javax.swing.JScrollPane;

public class ServerGUI {

	private JFrame frmServer;
	private JScrollPane scrollPane;
	private AnalysisServer server;
	
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
		frmServer.setTitle("Applicaltion Analysis Local - BKUCSE");
		frmServer.setBounds(100, 100, 450, 500);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		Image icon = new ImageIcon(getClass().getResource("/resource/analysis.ico")).getImage();
		frmServer.setIconImage(icon);
		
		JButton btnStart = new JButton("Start");
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
			}
		});
		
		btnStart.setBounds(10, 10, 100, 25);
		frmServer.getContentPane().add(btnStart);
		
		txtPort = new JTextField();
		txtPort.setBounds(340, 10, 85, 25);
		txtPort.setText("4446");
		frmServer.getContentPane().add(txtPort);
		
		JLabel lblInfo = new JLabel("Infomation:");
		lblInfo.setBounds(10, 40, 100, 25);
		frmServer.add(lblInfo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 70, 415, 380);
		frmServer.getContentPane().add(scrollPane);
		
		this.txtInfo = new JTextArea();
		scrollPane.setViewportView(txtInfo);
		txtInfo.setEditable(false);
	}
	
	private void btnStart_Click() throws MqttException, URISyntaxException{
		server = new AnalysisServer(this);
	}
}
