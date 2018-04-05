import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client {
  static String serverAddress;
  static int serverPort;
  static Socket socket;
  static DataInputStream inStream;
  static DataOutputStream outStream;
  static JFrame splashFrame, frame;
  JPanel outputPanel, inputPanel;
  private JPanel contentPane;
  private JTextField input;
  TextArea output;

  public Client() {
    initGUI();
    addListeners();
  }

  private void initGUI() {
    frame = new JFrame("Ch\u01B0\u01A1ng tr\u00ECnh t\u1EA1i Client " 
 							+ serverAddress + ":" + serverPort);
    frame.setLayout(new GridLayout(2, 1));
    frame.setBounds(200, 200, 500, 200);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(100, 100, 427, 300);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	frame.setContentPane(contentPane);
	contentPane.setLayout(null);
	
	JLabel lblRequest = new JLabel("Request");
	lblRequest.setBounds(10, 11, 414, 14);
	contentPane.add(lblRequest);
	
	input = new JTextField();
	input.setBounds(10, 36, 188, 20);
	contentPane.add(input);
	input.setColumns(10);
	JButton btnSend = new JButton("Send");
	btnSend.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			String expresion = input.getText();
			output.append("Request = " + (expresion.isEmpty() ? "null" : expresion )+ "\n");
	        try {
	          socket = new Socket(serverAddress, serverPort);
	          socket.setSoTimeout(1000);
	          inStream = new DataInputStream(socket.getInputStream());
	          outStream = new DataOutputStream(socket.getOutputStream());
	          outStream.writeUTF(expresion);
	          String result = inStream.readUTF();
	          output.append("Result = " +result + "\n");
	          inStream.close();
	          outStream.close();
	          socket.close();
	        } catch (UnknownHostException e) {
	          JOptionPane.showMessageDialog(null, "Không tìm thấy Server.");
	          e.printStackTrace();
	        } catch (IOException e) {
	          JOptionPane.showMessageDialog(null, "Lỗi kết nối vào ra khi truyền dữ liệu.");
	          e.printStackTrace();
	        }
		}
	});
	btnSend.setBounds(212, 36, 89, 23);
	contentPane.add(btnSend);
	
	JButton btnClean = new JButton("Clean");
	btnClean.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			input.setText("");
		}
	});
	btnClean.setBounds(311, 36, 89, 23);
	contentPane.add(btnClean);
	
	JLabel lblResponse = new JLabel("Response");
	lblResponse.setBounds(10, 67, 390, 14);
	contentPane.add(lblResponse);
	
	output = new TextArea();
	output.setBounds(10, 92, 390, 158);
	contentPane.add(output);
	
	output.setEditable(false);
    frame.setVisible(true);
  }

  private void addListeners() {
    frame.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent arg0) {
        splashFrame.setVisible(false);
      }
      @Override
      public void windowIconified(WindowEvent arg0) {}
      @Override
      public void windowDeiconified(WindowEvent arg0) {}
      @Override
      public void windowDeactivated(WindowEvent arg0) {}
      @Override
      public void windowClosing(WindowEvent arg0) {}
      @Override
      public void windowClosed(WindowEvent arg0) {
        splashFrame.setVisible(true);
        frame.setVisible(false);
      }
      @Override
      public void windowActivated(WindowEvent arg0) {}
    });
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    splashFrame = new JFrame("Nhập thông tin");
    splashFrame.setBounds(200, 200,300, 100);
    splashFrame.setResizable(false);
    splashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    splashFrame.setLayout(new BorderLayout());
    splashFrame.add(new JLabel("Nhập địa chỉ máy chủ và cổng"), BorderLayout.NORTH);
    JTextField host, port;
    host = new JTextField("Localhost");
    port = new JTextField("123");
    splashFrame.add(host, BorderLayout.CENTER);
    splashFrame.add(port, BorderLayout.EAST);
    JButton enterBtn = new JButton("Enter");
    splashFrame.add(enterBtn, BorderLayout.SOUTH);
    splashFrame.setVisible(true);
    enterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        serverAddress = host.getText();
        serverPort = Integer.parseInt(port.getText());
        new Client();
      }
    });
  }
}
