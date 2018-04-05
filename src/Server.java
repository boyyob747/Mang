import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Stack;

public class Server extends JFrame implements Runnable{
	static ServerSocket server;
	static Socket socket;
	static String fromClient, ToClient = null;
	private JPanel contentPane;
	TextArea textProcessing;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		setTitle("Ch\u01B0\u01A1ng tr\u00ECnh t\u1EA1i Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblProcessing = new JLabel("Processing");
		lblProcessing.setBounds(10, 11, 414, 14);
		contentPane.add(lblProcessing);
		
		textProcessing = new TextArea();
		textProcessing.setBounds(10, 31, 380, 220);
		contentPane.add(textProcessing);
		boolean portIsNumber = false;
		int port = 0;
		do{
			try {
				String stringPort = JOptionPane.showInputDialog("Nhập port : ");
				port = Integer.parseInt(stringPort);
				portIsNumber = false;
			} catch (Exception e) {
				portIsNumber = true;
			}
		}while(portIsNumber);
		try {
			server = new ServerSocket(port);
			textProcessing.append("Server đang chạy tại port : " + port + "\n");
			textProcessing.append("Waiting connection\n");	
		} catch (Exception e) {
			textProcessing.append("Không thể chạy tại port : " + port + "\n");
		}
		
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  try{
					  socket.close();
				  }catch (Exception e) {
					// TODO: handle exception
				}
				    System.exit(0);
				  }
				});
		new Thread(this).start();
	}
	public static int KiemTraDau(String c) {
		if (c.equals("+") || c.equals("-"))
			return 1;
		if (c.equals("*") || c.equals("/"))
			return 2;
		return 0;
	}

	public static int laDau(String c) {
		if (KiemTraDau(c) == 0) {
			if (!c.equals("(") && !c.equals(")"))
				return 0;
			else
				return 1;
		}
		return 2;
	}
	public static void tinhPow(){
		if (fromClient.contains("pow")){
			try{
				String pow = fromClient.substring(fromClient.indexOf("p"), fromClient.indexOf("]") + 1);
				String firstNumber = pow.substring(pow.indexOf("[") + 1, pow.indexOf(","));
				String secNumber = pow.substring(pow.indexOf(",") + 1, pow.indexOf("]"));
				double sumOfPow = Math.pow(Double.valueOf(firstNumber), Double.valueOf(secNumber));
				fromClient = fromClient.replace(pow, String.valueOf(sumOfPow));
				tinhPow();
			}catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	public static String tinhToan(String a, String b, String i) {
		double x = Double.parseDouble(a);
		double y = Double.parseDouble(b);
		switch (i) {
		case "+":
			return x + y + "";
		case "-":
			return x - y + "";
		case "*":
			return x * y + "";
		case "/":
			return x / y + "";
		default:
			return "0";
		}
	}
	public static Inet6Address getIPv6Addresses(InetAddress[] addresses) 
	{
	    for (InetAddress addr : addresses) 
	    {
	        if (addr instanceof Inet6Address) 
	        {
	            return (Inet6Address) addr;
	        }
	    }
	    return null;
	}
	public static String Calculator(String str) {
		Stack<String> St = new Stack<String>();
		Stack<String> Sh = new Stack<String>();
		String number = "";
		for (int i = 0; i < str.length(); i++) {
			String kitu = String.valueOf(str.charAt(i));
			if (laDau(kitu) == 0 && i != str.length() - 1) {
				number += kitu;
			} else {
				if (laDau(kitu) == 0 && i == str.length() - 1) {
					number += kitu;
				}
				if (number.length() > 0) {
					Sh.push(number);
					number = "";
				}
				if (laDau(kitu) == 1) {
					if (kitu.equals("("))
						St.push("(");
					else if (kitu.equals(")")) {
						while (!St.peek().equals("(")) {
							String a = Sh.pop();
							String b = Sh.pop();
							Sh.push(tinhToan(b, a, St.pop()));
						}
						St.pop();
					}
				} else if (laDau(kitu) == 2) {
					while (!St.isEmpty() && KiemTraDau(kitu) <= KiemTraDau(St.peek())) {
						String a = Sh.pop();
						String b = Sh.pop();
						Sh.push(tinhToan(b, a, St.pop()));
					}
					St.push(kitu);
				}
			}
		}
		while (!St.isEmpty()) {
			String a = Sh.pop();
			String b = Sh.pop();
			Sh.push(tinhToan(b, a, St.pop()));
		}
		return Sh.pop();
	}
	@Override
	public void run() {
		while (true) {
			try {
				DataInputStream dataInputStream;
				DataOutputStream dataOutputStream;
				socket = server.accept();
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				fromClient = dataInputStream.readUTF();
				if (fromClient.isEmpty()) {
					textProcessing.append("CLIENT > null\n");
					ToClient = "null";
				}else if (fromClient.charAt(0) != '='){
					textProcessing.append("CLIENT > " + fromClient + "\n");
					ToClient = "Tên máy chủ : " + getComputerName();
					ToClient += "\nĐịa chỉ IP của máy chủ : " + getLocalIp();
				}
				else{
					textProcessing.append("CLIENT > " + fromClient + "\n");
					fromClient = fromClient.replace("=", "");
					tinhPow();
					try{
						ToClient = "" + Calculator(fromClient);
					}catch (Exception e) {
						ToClient = "Dạng Không đúng !"; // error format ...
					}
					
				}
				textProcessing.append("SERVER > " + ToClient + "\n");
				dataOutputStream.writeUTF(String.valueOf(ToClient));

			} catch (IOException e) {
				try {
					socket.close();
					textProcessing.append("Đã đóng server\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	}
	public static String getLocalIp()
	{
		InetAddress IP;
		try {
			IP = InetAddress.getLocalHost();
			return IP.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		}
		
	}
	public static String getComputerName()
	{
	    Map<String, String> env = System.getenv();
	    if (env.containsKey("COMPUTERNAME"))
	        return env.get("COMPUTERNAME");
	    else if (env.containsKey("HOSTNAME"))
	        return env.get("HOSTNAME");
	    else
	        return "Unknown Computer";
	}
}
