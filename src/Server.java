/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
	

/*-------------------------------------------------------------------------------------------------------------------------------------------
 * CLASS NAME: Server
 * FUNCTIONS: 1. Makes a socket connection with the the clients as well as connects with other 8 servers in the network
 * 			  2. Ask the user to enter a Server ID i.e. the server no. which it wants to connect (1-8)
 * 			  3. Starts the corresponding server
 * 			  4. Then it waits for peers to connect
 * 			  5. Initializes an object of class TableHash where every server will be having a private Hash table which store their corresponding keys and values
 * METHODS USED: 1. main() function
 * 				 2. configReader()
 *-------------------------------------------------------------------------------------------------------------------------------------------
 */	
public class Server {		
	
	//This is the main function of the server that will instruct the other corresponding methods
	public static void main(String[] args) 
	{   	 
		int port; 
		ServerSocket server;   	      // Creating a Server Socket where Clients/Peers can connect
		Socket socket = new Socket(); // Socket that will bring communication from Client to the Server Socket
		ServerManager sman;     
		try{
			System.out.println("*************************************");
			System.out.println("|       DECENTRALIZED P2P SYSTEM    |");
			System.out.println("*************************************");
			System.out.println("\nEnter a server ID between 1-8:");
			Scanner in = new Scanner(System.in);
			String id = in.nextLine();
			port = Integer.parseInt(configReader(id));
	
			server = new ServerSocket(port);
			
			InetAddress add = InetAddress.getLocalHost();
			
			/*Displaying server information to the user*/
			System.out.println("-------------------------------------------------");
			System.out.println("\nSERVER STARTED... !!");							
			System.out.println("\nCURRENT SERVER INFORMATION ");
			System.out.println("-----------------------------");
			System.out.println("IP Address : " + add.getHostAddress());
			System.out.println("Port Number: " + port);	    
			System.out.println("-----------------------------");
			
			System.out.println("\nWaiting for Peers to connect on port " +server.getLocalPort() + "....");
			TableHash th = new TableHash(); //Initializing an object for class TableHash()
			while(true) { 
				socket = server.accept(); // Listening from the Peer
				System.out.println("\nConnection made to: "+ socket.getRemoteSocketAddress()); 
				sman = new ServerManager(socket, id, th);//Initializing an object of ServerManager and starting it in a thread.
				Thread t = new Thread(sman); // Creating thread and assigning the thread to ServerManager
				t.start(); }
		    }	
		    
		    catch(IOException e) {//Catching the exception
		    	e.printStackTrace();
		    }	  
		   }	
	
	// This method "configReader()", reads the Server's IP address and Port numbers from "config.properties" file and store in variable server[].  
	public static String configReader(String key){
		Properties prop = new Properties();
		InputStream input = null;
		String id = "";
		String[] server = new String[2];
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);// load a properties file
			id = prop.getProperty(key);// get the property value and print it out
			server = id.split(",");								
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return server[1]; //Returns the value part of the "config.properties" file i.e. the port numbers on which the servers will start.
	}
}

