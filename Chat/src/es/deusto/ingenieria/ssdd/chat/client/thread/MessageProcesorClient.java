package es.deusto.ingenieria.ssdd.chat.client.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import es.deusto.ingenieria.ssdd.chat.client.controller.ChatClientController;
import es.deusto.ingenieria.ssdd.chat.client.gui.JFrameMainWindow;
import es.deusto.ingenieria.ssdd.chat.exceptions.IncorrectMessageException;

public class MessageProcesorClient extends Thread{

	private ChatClientController controller;
	
	public MessageProcesorClient(ChatClientController controller){
		this.controller=controller;
	}
	
	/**
	 * This method concat the split messages that corresponds to 
	 * a whole message
	 * @param message this is the message written by the client
	 */
	private String concatMessage(String message){
		String mes= null;
		if (controller.incompletedMessage==null){
			if(message.charAt(message.length()-1)== '&'){
				controller.incompletedMessage= message.split("&")[2];
			}
			else{
				//escribir en pantalla
				mes= message.split("&")[2];
			}
		}
		else{
			if(message.charAt(message.length()-1)=='&'){
				controller.incompletedMessage= controller.incompletedMessage.concat(message.split("&")[2]);
			}
			else{
				controller.incompletedMessage= controller.incompletedMessage.concat(message.split("&")[2]);
				//escribir en pantalla
				mes= controller.incompletedMessage;
				controller.incompletedMessage=null;
			}
		}
		return mes;
	}
	
	/**
	 * This method concat the split messages that corresponds to 
	 * a whole message of the list of connected users
	 * @param message formed by the nicks of connected users
	 */
	private String concatListOfUsers(String message){
		String userList=null;
		if (controller.incompletedListUsers==null){
			if(message.charAt(message.length()-1)== '&'){
				controller.incompletedListUsers= message;
			}
			else{
				//crear lista
				userList= message;
			}
		}
		else{
			if(message.charAt(message.length()-1)=='&'){
				controller.incompletedListUsers= controller.incompletedListUsers.concat(message);
			}
			else{
				controller.incompletedListUsers= controller.incompletedListUsers.concat(message);
				//crear lista
				userList= controller.incompletedListUsers;
				controller.incompletedListUsers=null;
			}
		}
		return userList;
	}
	
		
	@Override
	public void run() {
		String returnMessage ="";
		String numericalId;
		String userList;
		String message;
		DatagramPacket receivedPacket;
		try {
			receivedPacket = controller.receiveDatagramPacket();
			//Launch a new thread to continue receiving message while processing the first one.
			MessageProcesorClient newThread = new MessageProcesorClient(controller);
			newThread.start();
			
			//Process the message
			returnMessage=new String(receivedPacket.getData());
			returnMessage=returnMessage.trim();
			numericalId= returnMessage.split("&")[0];
			
			System.out.println("numericalId"+numericalId);
			switch (numericalId){
			
				case "201":
					System.out.println("Dentro del switch"+numericalId);
					userList=concatListOfUsers(returnMessage.substring(4).trim());
					if (userList != null){
					controller.mainWindow.refreshUserList(userList);}
					break;
				case "202":
					//Peticion recibida de A (202&nickA&nickMio)
					String receiverNick=returnMessage.split("&")[1].trim();
					if(this.controller.getChatReceiver()==null){
						boolean acceptInvitation= controller.mainWindow.acceptChatInvitation(receiverNick);
						if (acceptInvitation){
							controller.acceptChatRequest(receiverNick);
							this.controller.mainWindow.startConversationMessage();
						}
						else{
							controller.refuseChatRequest(receiverNick);
						}
					}else{
						//the user is already chatting. Notify the server
						this.controller.sendAlreadyChatting(receiverNick);
					}
					
					break;
				case "203":
					//B ha aceptado (203)
					//blokear resto de lista
					this.controller.mainWindow.startConversationMessage();
					break;
				case "204":
					//B ha rechazado ventana emergente 
					controller.mainWindow.conversationRejected();
					this.controller.setChatReceiver(null);
					break;
				case "205":
					//A ha cerrado la conversacion
					//desblokear lista de usuarios
					this.controller.setChatReceiver(null);
					this.controller.mainWindow.endConversationMessage();
					//limpiar ventana de texto
					break;
				case "206":
					//Disconnection successfull
					controller.mainWindow.disconnectionSuccessful();
					this.controller.udpSocket.close();
					break;
				case "207":
					userList=concatListOfUsers(returnMessage.substring(4).trim());
					if (userList != null){
					controller.mainWindow.refreshUserList(userList);}
					break;
				case "208":
					//llamar metodo ConcatMessage(returnmessage)
					message= concatMessage(returnMessage);
					if (message!=null){
						this.controller.receiveMessage(message);
					}
					break;
				case "301":
					//the nick already exists 
					controller.mainWindow.existentNick();
					break;
				case "302":
					//connection failed
					controller.mainWindow.connectionFailed();
					break;
				case "303":
					//b already chatting
					controller.mainWindow.alreadyChatting(controller.getChatReceiver());
					this.controller.setChatReceiver(null);
					break;
				case "304":
					//B is disconnected and send list
					controller.mainWindow.disconnectedB(this.controller.getChatReceiver());
					this.controller.setChatReceiver(null);
					userList=concatListOfUsers(returnMessage.substring(4).trim());
					if (userList != null){
						controller.mainWindow.refreshUserList(userList);}
					break;
				//default: throw new IncorrectMessageException("The message type code does not exist");
			}
		} catch (IOException e) {
			System.out.println("Hilo detenido.");
		}
	}
	
	

}
