package es.deusto.ingenieria.ssdd.chat.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import es.deusto.ingenieria.ssdd.chat.client.controller.ChatClientController;
import es.deusto.ingenieria.ssdd.chat.data.Message;

public class JFrameMainWindow extends JFrame implements Observer, WindowListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFieldServerIP;
	private JTextField txtFieldServerPort;
	private JTextField txtFieldNick;
	private JButton btnConnect;
	private JList<String> listUsers;
	private JTextPane textAreaHistory;
	private JTextArea textAreaSendMsg;
	private JButton btnSendMsg;
	private JButton btnReloadListOfUsers;
	private SimpleDateFormat textFormatter = new SimpleDateFormat("HH:mm:ss");
	
	private ChatClientController controller;	

	/**
	 * Create the frame.
	 */
	public JFrameMainWindow(ChatClientController controller) {
		controller.setMainWindow(this);
		this.controller = controller;
		//Add the frame as Observer of the Controller
		this.controller.addLocalObserver(this);
		
		setResizable(false);
		setType(Type.UTILITY);
		setTitle("Chat main window");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelUsers = new JPanel();
		panelUsers.setBorder(new TitledBorder(null, "Connected users", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panelUsers, BorderLayout.EAST);
		panelUsers.setLayout(new BorderLayout(0, 0));
		panelUsers.setPreferredSize(new Dimension(200, 0));
		panelUsers.setMinimumSize(new Dimension(200, 0));
		
		listUsers = new JList<>();
		listUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listUsers.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		listUsers.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				selectUser();
				
			}
		});
		
		panelUsers.add(listUsers);
		
		JPanel panelConnect = new JPanel();
		panelConnect.setBorder(new TitledBorder(null, "Connection details", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panelConnect, BorderLayout.NORTH);
		
		JLabel lblServerIp = new JLabel("Server IP:");		
		JLabel lblServerPort = new JLabel("Server Port:");
		
		txtFieldServerIP = new JTextField();
		txtFieldServerIP.setColumns(10);
		txtFieldServerIP.setText("127.0.0.1");
		txtFieldServerPort = new JTextField();
		txtFieldServerPort.setColumns(10);
		txtFieldServerPort.setText("6789");
		
		JLabel lblNick = new JLabel("Nick:");
		
		txtFieldNick = new JTextField();
		txtFieldNick.setColumns(10);
		txtFieldNick.setText("aaa");
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnConnectClick();
			}
		});
		btnConnect.setToolTipText("Connect");
		GroupLayout gl_panelConnect = new GroupLayout(panelConnect);
		gl_panelConnect.setHorizontalGroup(
			gl_panelConnect.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConnect.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelConnect.createParallelGroup(Alignment.LEADING)
						.addComponent(lblServerIp)
						.addComponent(lblServerPort))
					.addGap(21)
					.addGroup(gl_panelConnect.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelConnect.createSequentialGroup()
							.addComponent(txtFieldServerIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(37)
							.addComponent(lblNick)
							.addGap(18)
							.addComponent(txtFieldNick, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addGap(28)
							.addComponent(btnConnect))
						.addComponent(txtFieldServerPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(302, Short.MAX_VALUE))
		);
		gl_panelConnect.setVerticalGroup(
			gl_panelConnect.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConnect.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelConnect.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServerIp)
						.addComponent(txtFieldServerIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNick)
						.addComponent(btnConnect)
						.addComponent(txtFieldNick, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelConnect.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServerPort)
						.addComponent(txtFieldServerPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(15, Short.MAX_VALUE))
		);
		panelConnect.setLayout(gl_panelConnect);
		
		JPanel panelHistory = new JPanel();
		panelHistory.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "History", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panelHistory, BorderLayout.CENTER);
		panelHistory.setLayout(new BorderLayout(0, 0));
		
		textAreaHistory = new JTextPane();
		textAreaHistory.setBackground(Color.BLACK);
		textAreaHistory.setToolTipText("Messages history");
		textAreaHistory.setEditable(false);
		
		JScrollPane scrollPaneHistory = new JScrollPane(textAreaHistory);
		scrollPaneHistory.setViewportBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelHistory.add(scrollPaneHistory);
		
		
		JPanel panelSendMsg = new JPanel();
		contentPane.add(panelSendMsg, BorderLayout.SOUTH);
		panelSendMsg.setBorder(new TitledBorder(null, "New message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSendMsg.setLayout(new BorderLayout(0, 0));
		
		btnSendMsg = new JButton("Send");
		btnSendMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSendClick();
			}
		});
		btnSendMsg.setToolTipText("Send new message");
		btnSendMsg.setEnabled(false);
		panelSendMsg.add(btnSendMsg, BorderLayout.EAST);
		
		//========================================================================
		//Button of reload the list of connected users
		btnReloadListOfUsers = new JButton("Reload list of users");
		btnReloadListOfUsers.setEnabled(false);
		btnReloadListOfUsers.setToolTipText("Reload list of ussers");
		btnReloadListOfUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				askForListOfUsers();
			}
		});
		panelSendMsg.add(btnReloadListOfUsers, BorderLayout.NORTH);
		this.addWindowListener(this);
		//========================================================================
		
		textAreaSendMsg = new JTextArea();
		textAreaSendMsg.setTabSize(3);
		textAreaSendMsg.setRows(4);
		textAreaSendMsg.setToolTipText("New message");	
		
		JScrollPane scrollPaneNewMsg = new JScrollPane(textAreaSendMsg);
		scrollPaneNewMsg.setViewportBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelSendMsg.add(scrollPaneNewMsg, BorderLayout.CENTER);		
	}
	
	/**
	 * This method refresh the list of connected users
	 * @param allUsers list of connected users separated by &
	 */
	public void refreshUserList(String allUsers){
		
		String[] users = allUsers.split("&");
		if (users.length!=0) {
			DefaultListModel<String> listModel = new DefaultListModel<>();
			
			for (String user : users) {
				if(!user.equals(controller.getConnectedUser()))
				listModel.addElement(user);
			}
			this.listUsers.setModel(listModel);
		}
	}
	
	
	private void btnConnectClick() {

		if (!this.controller.isConnected()) {
			if (this.txtFieldServerIP.getText().trim().isEmpty() ||
				this.txtFieldServerIP.getText().trim().isEmpty() ||
				this.txtFieldNick.getText().trim().isEmpty() ) {				
				JOptionPane.showMessageDialog(this, "Some connection parameters are empty", "Connection initializarion error", JOptionPane.ERROR_MESSAGE);				
				
				return;
			}
			
			//Connect to the server
			String listaDeUsuarios;
			try {
				listaDeUsuarios = this.controller.connect(this.txtFieldServerIP.getText(),Integer.parseInt(this.txtFieldServerPort.getText()),this.txtFieldNick.getText());
				if (listaDeUsuarios!=null) {
					this.txtFieldServerIP.setEditable(false);
					this.txtFieldServerPort.setEditable(false);
					this.txtFieldNick.setEditable(false);
					this.btnConnect.setText("Disconnect");
					this.btnSendMsg.setEnabled(true);
					this.btnReloadListOfUsers.setEnabled(true);
					this.textAreaHistory.setText("");
					this.textAreaSendMsg.setText("");
					refreshUserList(listaDeUsuarios);		
				} else {
					JOptionPane.showMessageDialog(this, "Can't connect to the server.", "Connection error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Can't connect to the server.", "Connection error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Can't connect to the server.", "Connection error", JOptionPane.ERROR_MESSAGE);
			}
			
		} else {
			//Disconnect from the server
			if (this.controller.disconnect()) {
			} else {
				JOptionPane.showMessageDialog(this, "Disconnection from the server fails.", "Disconnection error", JOptionPane.ERROR_MESSAGE);				
			}
		}
	}
	
	public void conversationRejected(){
		JOptionPane.showMessageDialog(this, this.controller.getChatReceiver()+" rejected establishing a conversation with you.");
	}
	
	public void existentNick(){
		JOptionPane.showMessageDialog(this, "The nick is already used.");
	}
	
	/**
	 * THis method is responsible of changing elements state in the GUI
	 * when the disconnection has been made with success.
	 */
	@SuppressWarnings("rawtypes")
	public void disconnectionSuccessful(){
		this.txtFieldServerIP.setEditable(true);
		this.txtFieldServerPort.setEditable(true);
		this.txtFieldNick.setEditable(true);
		this.listUsers.setEnabled(true);
		this.listUsers.clearSelection();
		((DefaultListModel)this.listUsers.getModel()).removeAllElements();
		this.btnConnect.setText("Connect");
		this.btnSendMsg.setEnabled(false);
		this.btnReloadListOfUsers.setEnabled(false);
		this.textAreaHistory.setText("");
		this.textAreaSendMsg.setText("");
		this.setTitle("Chat main window - 'Disconnected'");
		JOptionPane.showMessageDialog(this, "Disconnection successful.");
	}
	
	//The following methods corresponds to emergent window that notifies to the user
	//about the chat state.
	public void connectionFailed(){
		JOptionPane.showMessageDialog(this, "Connection failed.");
	}
	
	public void alreadyChatting(String nick){
		JOptionPane.showMessageDialog(this, nick+" is already chatting.");
	}
	
	public void disconnectedB(String nick){
		JOptionPane.showMessageDialog(this, nick+" is disconnected.");
	}
	
	public boolean acceptChatInvitation(String nick){
		int result = JOptionPane.showConfirmDialog(this, "Do you want to start a new chat session with '" + nick + "'", "Open chat Session", JOptionPane.YES_NO_OPTION);
	if (result==JOptionPane.OK_OPTION){
		return true;
	}
	else
	{
		return false;
	}
	
	}
	private void selectUser() {
		if (this.listUsers.getSelectedIndex() != -1 && this.controller.getConnectedUser() != null) {		
			//Send chat Request
			if (!this.controller.isChatSessionOpened()) {			
				int result = JOptionPane.showConfirmDialog(this, "Do you want to start a new chat session with '" + this.listUsers.getSelectedValue() + "'", "Open chat Session", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.OK_OPTION && this.controller.sendChatRequest(this.listUsers.getSelectedValue())) {
					this.setTitle("Chat session between '" + this.controller.getConnectedUser() + "' & '" + this.listUsers.getSelectedValue() + "'");
				} else {
					this.listUsers.clearSelection();
				}
			//Send a chat closure
			} else if (this.controller.isChatSessionOpened() && this.listUsers.getSelectedValue().equals(this.controller.getChatReceiver())) {			
				int result = JOptionPane.showConfirmDialog(this, "Do you want to close your current chat session with '" + this.controller.getChatReceiver() + "'", "Close chat Session", JOptionPane.YES_NO_OPTION);				

				if (result == JOptionPane.OK_OPTION && this.controller.sendChatClosure()) {
					this.listUsers.clearSelection();
					this.endConversationMessage();
					this.setTitle("Chat main window - 'Connected'");
				}
			}
		}
	}
	
	private void btnSendClick() {
		if (!this.textAreaSendMsg.getText().trim().isEmpty()) {
		
			if (this.controller.getChatReceiver()==null) {				
				JOptionPane.showMessageDialog(this, "No chat started", "Chat initialization error", JOptionPane.ERROR_MESSAGE);				
				return;
			}	
			
			String message = this.textAreaSendMsg.getText().trim();
			
			//message sent
			controller.sendMessage("108&"+this.controller.getChatReceiver()+"&"+message);
			appendMyMessageToHistory(message,this.controller.getConnectedUser(),new Date().getTime());
			textAreaSendMsg.setText("");
			
		}
	}
	

	public void endConversationMessage(){
		String time = textFormatter.format(new Date());		
		String newMessage = " " + time + ": CONVERSATION FINISHED\n";
		appendMessageToHistory(newMessage, Color.GREEN); 
	}
	public void startConversationMessage(){
		String time = textFormatter.format(new Date());		
		String newMessage = " " + time + ": BEGINING OF THE CONVERSATION WITH ["+controller.getChatReceiver()+"]\n";
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		appendMessageToHistory(newMessage, Color.GREEN); 
	}
	public void appendReceivedMessageToHistory(String message, String user, long timestamp) {		
		String time = textFormatter.format(new Date(timestamp));		
		String newMessage = " " + time + " - [" + user + "]: " + message.trim() + "\n";
		appendMessageToHistory(newMessage, Color.MAGENTA); 
	}
	public void appendMyMessageToHistory(String message, String user, long timestamp) {		
		String time = textFormatter.format(new Date(timestamp));		
		String newMessage = " " + time + " - [" + user + "]: " + message.trim() + "\n";
		appendMessageToHistory(newMessage, Color.BLUE); 
	}
	public void appendMessageToHistory(String message, Color color){
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setBold(attrs, true);
		StyleConstants.setForeground(attrs, color);		
		
		try {
			this.textAreaHistory.getStyledDocument().insertString(this.textAreaHistory.getStyledDocument().getLength(), message, attrs);
		} catch (BadLocationException e) {
			System.err.println("# Error updating message history: " + e.getMessage());
		} 
	}

	@Override
	public void update(Observable observable, Object object) {
		
		//Update this method to process the request received from other users
		
		if (this.controller.isConnected()) {			
			if (object.getClass().getName().equals(String.class.getName())) {
				String newMessage = (String) object;
				this.appendReceivedMessageToHistory(newMessage, controller.getChatReceiver(), new Date().getTime());
				
			}
		}
	}
	private void askForListOfUsers(){
		System.out.println("Peticion de lista de usuarios");
		this.controller.getConnectedUsers();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
		
	}

	//This method is used when the user close the chat window with (X).
	@Override
	public void windowClosing(WindowEvent e) {
			this.controller.disconnect();
			System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
		
	}
}
