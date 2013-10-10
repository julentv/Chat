package es.deusto.ingenieria.ssdd.chat.client.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import es.deusto.ingenieria.ssdd.util.observer.local.LocalObservable;
import es.deusto.ingenieria.ssdd.chat.data.User;

public class ChatClientController {
	private String serverIP;
	private int serverPort;
	private User connectedUser;
	private User chatReceiver;
	private LocalObservable observable;
	private static final int MESSAGE_MAX_LENGTH=1024;
	
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
		int messageLength = message.getBytes().length;
		if (messageLength > MESSAGE_MAX_LENGTH){
			
			numberOfMessages=messageLength / MESSAGE_MAX_LENGTH;
			
			if (messageLength % MESSAGE_MAX_LENGTH != 0)
			{						
				numberOfMessages++;
			}
		}
		return numberOfMessages;
	}
	
	private ArrayList <byte []> divideList (ArrayList <User> connectedUsers) throws IOException{
		ArrayList <byte []> aMessages = new ArrayList<byte []>();
		aMessages.add(new byte [0]);
		int cont =0;
		int arrayPosition=0;
		String nick;
		for (int i=0; i< connectedUsers.size(); i++){
			nick= connectedUsers.get(i).getNick().concat("&");
			cont += nick.getBytes().length;
			
			if(cont> MESSAGE_MAX_LENGTH){
				arrayPosition++;
				aMessages.add(nick.getBytes());
				
				cont=connectedUsers.get(i).toString().getBytes().length;
			}
			else
			{
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
				outputStream.write( aMessages.get(arrayPosition) );
				outputStream.write( nick.getBytes() );
								
				aMessages.set(arrayPosition, outputStream.toByteArray( ));
				
			}
			
			
		}
				
		
		return aMessages;
	}
	
	
	
	private byte[][] divideMessage (String completeMessage){
		byte [][] aMessages = null;
		int numberOfMessages = calculateNumberOfMessages(completeMessage);
		
		
		return aMessages;		
		
	}
	//Método que envía un paquete
	public void sendDatagramPacket(String message){
		try (DatagramSocket udpSocket = new DatagramSocket()) {
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
	 */
	private void receiveDatagramPacket(){
		try (DatagramSocket udpSocket = new DatagramSocket()) {
			
			byte[] buffer = new byte[1024];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			udpSocket.receive(reply);			
			
		} catch (SocketException e) {
			System.err.println("# UDPClient Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("# UDPClient IO error: " + e.getMessage());
		}
	}
	public boolean connect(String ip, int port, String nick) {
		
		String message = "101&"+nick;
		//ENTER YOUR CODE TO CONNECT
		this.serverIP = ip;
		this.serverPort = port;
		sendDatagramPacket(message);
		this.connectedUser = new User();
		this.connectedUser.setNick(nick);
		return true;
	}
	
	public boolean disconnect() {
		
		String message;
		//ENTER YOUR CODE TO DISCONNECT
		if (isChatSessionOpened()){
			 message = "105";
			sendDatagramPacket(message);
			message="205"+chatReceiver.getNick();
			sendDatagramPacket(message);
		}
		
		message= "106";
		sendDatagramPacket(message);
		message= "206";
		sendDatagramPacket(message);
		
		this.connectedUser = null;
		this.chatReceiver = null;
		
		return true;
	}
	
	public List<String> getConnectedUsers() {
		List<String> connectedUsers = new ArrayList<>();
		
		//ENTER YOUR CODE TO OBTAIN THE LIST OF CONNECTED USERS
		connectedUsers.add("Default");
		
		return connectedUsers;
	}
	
	public boolean sendMessage(String message) {
		
		//ENTER YOUR CODE TO SEND A MESSAGE
		
		return true;
	}
	
	public void receiveMessage() {
		
		//ENTER YOUR CODE TO RECEIVE A MESSAGE
		
		String message = "Received message";		
		
		//Notify the received message to the GUI
		this.observable.notifyObservers(message);
	}	
	
	public boolean sendChatRequest(String to) {
		
		//ENTER YOUR CODE TO SEND A CHAT REQUEST
		
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
	
	public boolean acceptChatRequest() {
		
		//ENTER YOUR CODE TO ACCEPT A CHAT REQUEST
		
		return true;
	}
	
	public boolean refuseChatRequest() {
		
		//ENTER YOUR CODE TO REFUSE A CHAT REQUEST
		
		return true;
	}	
	
	public boolean sendChatClosure() {
		
		//ENTER YOUR CODE TO SEND A CHAT CLOSURE
		
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