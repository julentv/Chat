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
	public void setChatReceiver(User chatReceiver) {
		this.chatReceiver = chatReceiver;
	}

	private LocalObservable observable;
	private static final int MESSAGE_MAX_LENGTH=1024;
	public String incompletedListUsers=null;
	public String incompletedMessage=null;
	public DatagramSocket udpSocket;
	public JFrameMainWindow mainWindow;
	
	
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
	
	private int calculateNumberOfMessages (String message){
		int numberOfMessages=1;
		String[] messageFields = message.split("&");
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
	
	//M�todo que env�a un paquete
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
	 * This method is used to receive a datagramPacket
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
			if (returnMessage.split("&")[0].equals("201")){
				this.connectedUser = new User();
				this.connectedUser.setNick(nick);
				if (returnMessage.charAt(returnMessage.length()-1)=='&'){
					incompletedListUsers = returnMessage.substring(4);
					System.out.println("acaba en &");
					MessageProcesorClient messageProcesor = new MessageProcesorClient(this);
					messageProcesor.start();
					return "";
				}
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
	public void sendAlreadyChatting(String nickTo){
		String message="303&"+nickTo;
		sendDatagramPacket(message);
	}
	public ArrayList<String> getConnectedUsers() {
		ArrayList<String> connectedUsers = new ArrayList<>();
		String message="107";
		sendDatagramPacket(message);
		
		//connectedUsers.add("Default");
		
		return connectedUsers;
	}
	
	public boolean sendMessage(String message) {
		
		//ENTER YOUR CODE TO SEND A MESSAGE
		String singleMessage;
		String header="108&"+this.chatReceiver.getNick();
		int dif=MESSAGE_MAX_LENGTH-header.length();
		while(message.length()>dif){
			singleMessage= header+"&"+message.substring(0,dif-1)+"&";
			this.sendDatagramPacket(singleMessage);
			message=message.substring(dif-1);
		}
		this.sendDatagramPacket(message);
		
		return true;
	}
	
	public void receiveMessage(String message) {
				
		//Notify the received message to the GUI
		this.observable.notifyObservers(message);
	}	
	
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
	
	public boolean acceptChatRequest(String nickToAccept) {
		
		//ENTER YOUR CODE TO ACCEPT A CHAT REQUEST
		this.chatReceiver=new User();
		this.chatReceiver.setNick(nickToAccept);
		String message= "103&"+this.chatReceiver.getNick();
		sendDatagramPacket(message);
		return true;
	}
	
	public boolean refuseChatRequest(String nickToRefuse) {
		
		//ENTER YOUR CODE TO REFUSE A CHAT REQUEST
		String message= "104&"+nickToRefuse;
		sendDatagramPacket(message);
		return true;
	}	
	
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
