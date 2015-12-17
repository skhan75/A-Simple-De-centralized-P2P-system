/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;

/*----------------------------------------------------------------------------------------------
 * CLASS NAME: ClientHandler
 * FUNCTIONS: 1. Makes a socket connection with the server
 * 			  2. Ask the user for selection of choice
 * 			  3. Sends the required operation to the Server to perform the required operation
 * METHODS USED: ClientHandler() 
 * 				 run()
 *----------------------------------------------------------------------------------------------
 */

public class ClientHandler implements Runnable {
	Socket client;
	int miniport;
	DataInputStream in;
	DataOutputStream out;
	String message = null;
	
	public ClientHandler(Socket client) {
		// TODO Auto-generated constructor stub
		this.client = client;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    String filename;
	    int filelength;
	    try{
			/*Instantiating IO Stream objects*/
			in =new DataInputStream(client.getInputStream());
	        out =new DataOutputStream(client.getOutputStream());
	    }
	    catch (IOException e) {
	    	e.printStackTrace(); 
		}		        
		try {
			/*Listening for requests from the other peers*/
			while(in.available()!=0) {  
				message = in.readUTF();
			    os = client.getOutputStream();
			    
			    /*Processing the download request form peers*/
			    if(message.equalsIgnoreCase("GET")){
			    	
			        filename = in.readUTF();
			        File myFile = new File (Paths.get("./UPLOAD").toAbsolutePath().toString()+"/"+filename); //Opening it's folder to retrieve the file to send
			        filelength = (int)myFile.length(); // Calculating file size and sending to the client
	
			        //File sending is now taking place below    
				    byte [] mybytearray  = new byte [filelength];
				    fis = new FileInputStream(myFile);
				    bis = new BufferedInputStream(fis);
				    bis.read(mybytearray,0,mybytearray.length);
				    out.writeInt(filelength); //Sending file legth to client to prepare it to set it'sincoming file size
				    System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
				    os.write(mybytearray,0,mybytearray.length); // Sending/Writing file
				    os.flush();		        
			    }
			    /*Processing the Replicate request form peers*/    
			    else if(message.equalsIgnoreCase("REPLICATE")){
			    	System.out.println("\n You have a replication instance from : " + "<"+client.getInetAddress().getHostAddress()+" : "+ client.getLocalPort()+">"); // Displaying acceptance message
			        System.out.println("<"+client.getInetAddress().getHostAddress()+" : "+client.getLocalPort()+">"+"  Added a file to your UPLOAD folder\n");
				   
			        filename = in.readUTF(); //Reading the file name to be recieved
				    	
				    int size = in.readInt();
				   	int bytesRead;
				    int current = 0;
					   
				    /*Downloading the file from the peer*/
					try{
				    	InputStream is = client.getInputStream();
				    	byte [] mybytearray  = new byte [size];
				    	fos = new FileOutputStream(Paths.get("./UPLOAD").toAbsolutePath().toString()+"/"+filename);
				 	    bos = new BufferedOutputStream(fos);
				 	    bytesRead = is.read(mybytearray,0,mybytearray.length);
					 	    
				 	    do{
				 	    	bytesRead = is.read(mybytearray,current,(mybytearray.length-current));
				 	    	if(bytesRead >= 0)
				    		current += bytesRead;					 	    
					     }
					    while(bytesRead>0);
					    bos.write(mybytearray);
					    bos.flush(); 
					}
					finally { 
						if (bos != null) bos.close();
						if (fos != null) fos.close(); 
					}
				   }			    
			}
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
	}		
}


