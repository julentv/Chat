package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.client.gui.JFrameMainWindow;
import es.deusto.ingenieria.ssdd.chat.client.thread.MessageProcesorClient;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class ChatClientController{
	private String serverIP;
	private int serverPort;
	private User connectedUser;
	private User chatReceiver;
	private LocalObservable observable;
	//This variable represents the maximum length of the buffer
	private static final int MESSAGE_MAX_LENGTH=1024;
	public String incompletedListUsers=null;
	public String incompletedMessage=null;
	public DatagramSocket udpSocket;
	public JFrameMainWindow mainWindow;
	
	public void setChatReceiver(User chatReceiver) {
		this.chatReceiver = chatReceiver;
	}
	
	public JFrameMainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(JFrameMainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public ChatClientController() {
		this.observable = new LocalObservable();
		this.serverIP = null;
		this.serverPort = -1;
	}
	
	public String getConnectedUser() {
		if (this.connectedUser != null) {
			return this.connectedUser.getNick();
		} else {
			return null;
		}
	}
	
	public String getChatReceiver() {
		if (this.chatReceiver != null) {
			return this.chatReceiver.getNick();
		} else {
			return null;
		}
	}
	
	public String getServerIP() {
		return this.serverIP;
	}
	
	public int gerServerPort() {
		return this.serverPort;
	}
	
	public boolean isConnected() {
		return this.connectedUser != null;
	}
	
	public boolean isChatSessionOpened() {
		return this.chatReceiver != null;
	}
	
	public void addLocalObserver(Observer observer) {
		this.observable.addObserver(observer);
	}
	
	public void deleteLocalObserver(Observer observer) {
		this.observable.deleteObserver(observer);
	}
	
	/**This method calculates the number of messages that the original one must be split
	 * in order to be sent to the server. Each part must not be bigger than 1024 bits.
	 * 
	 * @param message this is the complete message sent by the user plus the 
	 * the corresponding header.
	 * @return numberOfMessages corresponds to the number of messages that the message
	 * must be split in case it is bigger than 1024 bits.
	 */
	private int calculateNumberOfMessages (String message){
		int numberOfMessages=1;
		String[] messageFields = message.split("&");
		//bits that correspond to the header and the nick
		int bytesToRest = messageFields[0].getBytes().length + messageFields[1].getBytes().length +"&&&".getBytes().length;
		int messageLength = messageFields[2].getBytes().length;
		if (messageLength > MESSAGE_MAX_LENGTH - bytesToRest){
			
			numberOfMessages=messageLength / (MESSAGE_MAX_LENGTH - bytesToRest);
			
			if (messageLength % (MESSAGE_MAX_LENGTH - bytesToRest) != 0)
			{						
				numberOfMessages++;
			}
		}
		return numberOfMessages;
	}
	
	/**This method divides the message sent by the user in parts of 1024 bits.
	 * 
	 * @param completeMessage message sent by the user
	 * @return aMessages is an arrayList that contains in each position
	 * each part of the split message.
	 */
	private ArrayList<byte[]> divideMessage (String completeMessage){
		ArrayList<byte[]> aMessages = new ArrayList<byte[]>();
		int numberOfMessages = calculateNumberOfMessages(completeMessage);
		int a= completeMessage.length()/numberOfMessages;
		String message;
		for (int i=0; i<numberOfMessages;i++){
			message= completeMessage.substring(a*i, a*(i+1));
			if(i!=numberOfMessages){
				message.concat("&");
			}
			aMessages.add(message.getBytes());
		}
		return aMessages;		
	}
	
	/**
	 * This method sends a datagramPacket to the server
	 * @param message this corresponds to every command or message the user sent to the server.
	 */
	public void sendDatagramPacket(String message){
		try  {
			InetAddress serverHost = InetAddress.getByName(this.serverIP);	
			byte[] byteMsg = message.getBytes();
			DatagramPacket request = new DatagramPacket(byteMsg, byteMsg.length, serverHost, this.serverPort);
			udpSocket.send(request);
		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
	}
	
	/**
	 * This method is used to receive a datagramPacket from the server
	 * @throws IOException 
	 */
	public DatagramPacket receiveDatagramPacket() throws IOException{
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			udpSocket.receive(reply);	
			System.out.println(" - Received a request from '" + reply.getAddress().getHostAddress() + ":" + reply.getPort() + 
	                   "' -> " + new String(reply.getData()));
			
			return reply;
	}
	
	/**This method is used to be connected to the user.
	 * 
	 * @param ip 
	 * @param port
	 * @param nick
	 * @return null if the message is not complete or the connection fails
	 *  or the list of connected users in case the message is complete.
	 * 
	 * @throws IOException
	 */
public String connect(String ip, int port, String nick) throws IOException {
		
		String message = "101&"+nick;
		String returnMessage ="";
		//ENTER YOUR CODE TO CONNECT
		this.serverIP = ip;
		this.serverPort = port;
		try {
			this.udpSocket= new DatagramSocket();
			sendDatagramPacket(message);
			System.out.println("A la espera");
			DatagramPacket receivedPacket= receiveDatagramPacket();
			returnMessage=new String(receivedPacket.getData());
			returnMessage=returnMessage.trim();
			System.out.println("returnmessage= "+returnMessage);
			
			//If the message starts with 201 the user can connect to the server and receive the list of users
			if (returnMessage.split("&")[0].equals("201")){
				this.connectedUser = new User();
				this.connectedUser.setNick(nick);
				//If the message ends with & means that the message is not complete yet
				if (returnMessage.charAt(returnMessage.length()-1)=='&'){
					incompletedListUsers = returnMessage.substring(4);
					System.out.println("acaba en &");
					MessageProcesorClient messageProcesor = new MessageProcesorClient(this);
					messageProcesor.start();
					return "";
				}
				//The message is complete
				else{					
					MessageProcesorClient messageProcesor = new MessageProcesorClient(this);
					messageProcesor.start();
					return returnMessage.substring(4);
				}
			}
			else{
				this.udpSocket.close();
				return null;
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
			return null;
		}
		
					
	}

/**This method sends the datagram packet corresponds to the discconnection
 * 
 * @return true if the disconnection is successful or false if not.
 */
	public boolean disconnect() {
		
		String message;
		//ENTER YOUR CODE TO DISCONNECT
		if (isChatSessionOpened()){
			sendChatClosure();
		}
		
		message= "106";
		sendDatagramPacket(message);
		
		this.connectedUser = null;
		this.chatReceiver = null;
		
		
		return true;
	}
	
	/**
	 * This method sends the message that corresponds when the receiver is already chatting.
	 * @param nickTo the nick of the receiver user
	 */
	public void sendAlreadyChatting(String nickTo){
		String message="303&"+nickTo;
		sendDatagramPacket(message);
	}
	
	/**This method sends the message to ask for the list of connected users.
	 * @return arrayList of nicks of connected users
	 */
	public ArrayList<String> getConnectedUsers() {
		ArrayList<String> connectedUsers = new ArrayList<>();
		String message="107";
		sendDatagramPacket(message);
		
		//connectedUsers.add("Default");
		
		return connectedUsers;
	}
	
	/**
	 * This method sends the message written by the user. The same method split in parts
	 * of 1024 bits if it is necessary.
	 * @param message this is the message written by the user
	 * @return true
	 */
	public boolean sendMessage(String message) {
		
		//ENTER YOUR CODE TO SEND A MESSAGE
		String singleMessage;
		String header="108&"+this.chatReceiver.getNick();
		int dif=MESSAGE_MAX_LENGTH-header.length();
		while(message.length()>dif){
			singleMessage= message.substring(0,dif-1)+"&";
			this.sendDatagramPacket(singleMessage);
			message=message.substring(dif-1);
			message=header+"&"+message;
		}
		this.sendDatagramPacket(message);
		
		return true;
	}
	
	public void receiveMessage(String message) {
				
		//Notify the received message to the GUI
		this.observable.notifyObservers(message);
	}	
	
	/**
	 * this method is used to send a chat request
	 * @param to corresponds to the nick of the receiver
	 * @return true
	 */
	public boolean sendChatRequest(String to) {
		
		String message= "102&"+to;
		sendDatagramPacket(message);
		this.chatReceiver = new User();
		this.chatReceiver.setNick(to);
		
		return true;
	}	
	
	public void receiveChatRequest() {
		
		//ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
	
	/**
	 * This method corresponds to the message that accepts a chat request
	 * @param nickToAccept the nick of the person that sends the request
	 * @return true
	 */
	public boolean acceptChatRequest(String nickToAccept) {
		
		//ENTER YOUR CODE TO ACCEPT A CHAT REQUEST
		this.chatReceiver=new User();
		this.chatReceiver.setNick(nickToAccept);
		String message= "103&"+this.chatReceiver.getNick();
		sendDatagramPacket(message);
		return true;
	}
	
	/**
	 * This method corresponds to the message that refuse a chat request
	 * @param nickToRefuse is the nick of the person that send the request
	 * @return true
	 */
	public boolean refuseChatRequest(String nickToRefuse) {
		
		//ENTER YOUR CODE TO REFUSE A CHAT REQUEST
		String message= "104&"+nickToRefuse;
		sendDatagramPacket(message);
		return true;
	}	
	
	/**
	 * This method is used to send the message of closing the chat
	 * @return true
	 */
	public boolean sendChatClosure() {
		
		//ENTER YOUR CODE TO SEND A CHAT CLOSURE
		String message="105&"+this.chatReceiver.getNick();
		sendDatagramPacket(message);
		this.chatReceiver = null;
		
		return true;
	}
	
	public void receiveChatClosure() {
		
		//ENTER YOUR CODE TO RECEIVE A CHAT REQUEST
		
		String message = "Chat request details";
		
		//Notify the chat request details to the GUI
		this.observable.notifyObservers(message);
	}
}
