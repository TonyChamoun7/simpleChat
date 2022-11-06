// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  this.sendToAllClients(client.getInfo("id")+" has disconnected from the server");
	  System.out.println(client.getInfo("id")+" has disconnected from the server");
  }
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("New client has connected to the server.");
	  System.out.println("Message received: #login from " + client.getInfo("id"));
  }
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String s = (String)msg;
	  
	  if(s.contains("#login")) {
		  client.setInfo("id", s.substring(7));
		  serverUI.display("User: " + s.substring(7) + " has succesfully connected to the server");
		  this.sendToAllClients("User: " + msg.toString().substring(7) + " has succesfully connected to the server");
		  
	  }else if(s.contains("#logoff") || s.contains("#quit")) {
		  try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }else {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    ServerConsole console = new ServerConsole(port);
    EchoServer sv = new EchoServer(port, console);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
  
  public void handleMessageFromServerUI(String message) {
		switch(message) {
	  	
		case "#quit":
	  		try {
				close();
			} catch (IOException e) {
				serverUI.display("Could not quit");
			}
	  		System.exit(0);
	  		break;
	  	
	  	case "#stop":
			stopListening();
	  		break;
	  	
	  	case "#close":
			try {
				stopListening();
				close();
			} catch (IOException e) {
				serverUI.display("Could not close");
			}
	  		break;
	  	
	  	case "#start":
	  		try {
				listen();
			} catch (IOException e) {
				serverUI.display("Could not start");
			}
	  		break;
	  	
	  	case "#getport":
	  		serverUI.display(Integer.toString(getPort()));
	  		break;
	  	
	  	default:
	  		if(message.length() > 9 && message.substring(0,8).equals("#setport")) {
	  			
	  			if(!isListening()) {
	  			   try {
	  				   setPort(Integer.parseInt(message.substring(9)));
	  				   System.out.println("The port is set to: " + message.substring(9));
	  			   }
	  			   catch(Exception e){
	  				   serverUI.display("Invalid port");
	  			   }
	  			 } else {
	  				 serverUI.display("Could not change port");
	  			 }
	  		} else {
	  			this.sendToAllClients("SERVER MESSAGE>" + message);
	  			serverUI.display("SERVER MESSAGE>" + message);
	  		}
		}
	}
  
}
//End of EchoServer class
