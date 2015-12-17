/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* This Class is started by peerSend() on a thread and it does all the job of sending the required
 * file to the client
 */
public class ClientManager implements Runnable {
	ServerSocket miniServer;
	Socket client;
	int miniport;
	DataInputStream in;
	DataOutputStream out;
	String message = null;
	
	ClientHandler cnHand;
	
	
	public ClientManager(ServerSocket miniServer) {
		this.miniServer = miniServer;
	}

	@Override
	public void run() {
		while(true){
			try {
				client = miniServer.accept();	//Accepting connection requests from other peers
				cnHand = new ClientHandler(client);
				Thread th = new Thread(cnHand);
				th.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
}


