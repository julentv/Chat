package Thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import es.deusto.ingenieria.ssdd.chat.client.controller.ChatClientController;
import es.deusto.ingenieria.ssdd.chat.client.gui.JFrameMainWindow;
import es.deusto.ingenieria.ssdd.chat.exceptions.IncorrectMessageException;

public class MessageProcesorClient extends Thread{

	private ChatClientController controller;
	private JFrameMainWindow window;
	
	/**
	 * This method concat the split messages that corresponds to 
	 * a whole message
	 * @param message this is the message written by the client
	 */
	private void concatMessage(String message){
		if (controller.incompletedMessage==null){
			if(message.charAt(message.length()-1)== '&'){
				controller.incompletedMessage= message;
			}
			else{
				//escribir en pantalla
			}
		}
		else{
			if(message.charAt(message.length()-1)=='&'){
				controller.incompletedMessage= controller.incompletedMessage.concat(message);
			}
			else{
				controller.incompletedMessage= controller.incompletedMessage.concat(message);
				//escribir en pantalla
				controller.incompletedMessage=null;
			}
		}
		
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
		DatagramPacket receivedPacket= controller.receiveDatagramPacket();
		
		//Launch a new thread to continue receiving message while processing the first one.
		MessageProcesorClient newThread = new MessageProcesorClient();
		newThread.start();
		
		//Process the message
		returnMessage=receivedPacket.getData().toString();
		numericalId= returnMessage.split("&")[0];
		
		switch (numericalId){
			case "201":
				String users;
				users=concatListOfUsers(returnMessage.substring(3));
				window.refreshUserList(users);
				break;
			case "202":
				//Peticion recibida de A (202&nickA&nickMio)
				//mirar si hace click en si o en no en la ventana emergente
				//si enviar 103&minick
				//no enviar 104&minick
				break;
			case "203":
				//B ha aceptado (203)--desblokear texto para escribir
				//blokear resto de lista
				break;
			case "204":
				//B ha rechazado ventana emergente 
				break;
			case "205":
				//A ha cerrado la conversacion
				//desblokear lista de usuarios
				//limpiar ventana de texto
				break;
			case "206":
				//Disconnection successfull
				//boton = Connect
				break;
			case "207":
				//llamar metodo ConcatList(returnmessage)
				break;
			case "208":
				//llamar metodo ConcatMessage(returnmessage)
				break;
			case "301":
				//the nick already exists 
				//mensaje en ventana emergente
				break;
			case "302":
				//connection failed
				//mensaje en ventana emergente
				break;
			case "303":
				//b esta ya chateando
				//mensaje en ventana emergente
				break;
			case "304":
				//B is disconnected and send list
				//llamar método Concatlist(returnMessage)
				break;
			//default: throw new IncorrectMessageException("The message type code does not exist");
		
		}
		
		
	}
	
	

}
