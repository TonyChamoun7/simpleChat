// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String id;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String id, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.id= id;
    
    try {
    openConnection();
    sendToServer("#login " + id);
  }catch(Exception e) {
	  System.out.println("Connection Failed. Awaiting command");
  }
   
  }

  
  //Instance methods ************************************************
  
  protected void connectionClosed() {
		clientUI.display("Connection closed");
	}
  
  protected void connectionException(Exception exception) {
		System.out.println("WARNING - The server has stopped listening for connections\n"
				+ "SERVER SHUTTING DOWN! DISCONNECTING!\n"
				+ "Abnormal termination of connection.");
	}
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  switch(message) {
	  	case "#quit":
		try {
			sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  		quit();
	  		break;
	  	
	  	case "#logoff":
			try {
				sendToServer(message);
				closeConnection();
			} catch (IOException e) {
				clientUI.display("Unable to logoff");
			}
	  		break;
	  	
	  	case "#gethost":
	  		clientUI.display(getHost());
	  		break;
	  	
	  	case "#getport":
	  		clientUI.display(Integer.toString(getPort()));
	  		break;
	  	
	  	default:
	  		if(message.length() > 9 && message.substring(0,8).equals("#sethost")) {
	  			 if(!isConnected()) {
	  			   setHost(message.substring(9));
	  			   System.out.println("The host is set to: " + message.substring(9));
	  			 } else {
	  				clientUI.display("Unable to change host");
	  			 }
	  		} else if(message.length() > 9 && message.substring(0,8).equals("#setport")) {
	  			if(!isConnected()) {
	  			   try {
	  				   setPort(Integer.parseInt(message.substring(9)));
	  				   System.out.println("The port is set to: " + message.substring(9));
	  			   }
	  			   catch(Exception e){
	  				   clientUI.display("Invalide port");
	  			   }
	  			 } else {
	  				 clientUI.display("Unable to change port");
	  			 }
	  		} else if(message.length() > 7 && message.substring(0,6).equals("#login")) {
	  			if(!isConnected()) {
	  				try {
	  					openConnection();
						sendToServer(message);
	  				}catch(Exception e) {
	  					clientUI.display("Unable to login");
	  				}
				} else {
					clientUI.display("You are already logged in");		
				}
	  		} else {
	  			try{
	  		      sendToServer(message);
	  		    }catch(IOException e){
	  		      clientUI.display("Could not send message to server.  Terminating client.");
	  		      quit();
	  		    }
	  		}
	  }
    
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
