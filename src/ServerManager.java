/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/

/*-------------------------------------------------------------------------------------------------------------------------------------------
 * CLASS NAME: ServerManager
 * FUNCTIONS: 1. This is like a manager class of the main class Server() and it is running in a Thread
 * 			  2. Creates two Hashtables that will store the DataInputStreams and DataOutputStreams respectively of all the servers
 * 			  3. It will read a message from both Client and Servers. If the message is from Client, it will perform the client functions, else the servers functions
 * 			  4. It will Put, Get or Delete key-value pairs from it's DHT as per the client's choice.
 * CONSTRUCTOR USED: ServerManager(Socket client, String id, TableHash ht)
 * METHODS USED: 1. run()
 * 				 2. configReader()
 * 				 3. clientFunction()
 * 				 4. serverFunction()
 *-------------------------------------------------------------------------------------------------------------------------------------------
 */	

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map.Entry;


public class ServerManager implements Runnable { // Running in a Thread
	private static final String DataInputStream = null;
	private static final String DataOutputStream = null;
	Socket client;
	String id;
	TableHash ht; // Declaring an object of class  TableHash
	ServerMapper smp; // Declaring an object of class serverMapper
	String peerInfo;
	static int flag = 0;
	
	Hashtable<Integer,DataInputStream> dInList = new Hashtable<Integer, DataInputStream>(); // Declaring hash tables for DataInputStreams
	Hashtable<Integer,DataOutputStream> dOutList = new Hashtable<Integer, DataOutputStream>();// Declaring hash tables for DataOutputStreams
	
	public ServerManager(Socket client, String id, TableHash ht){
		this.client = client; //initializing client socket
		this.id = id; //initializing server's id
		this.ht = ht; //initializing object of TableHash
		this.smp = new ServerMapper();
	}
	
public void run(){ // run function of a thread
	try {
		String message = "";
		String keyVal;
		int port;
		
	    DataInputStream in =new DataInputStream(client.getInputStream());   
	    DataOutputStream out = new DataOutputStream(client.getOutputStream());
	    while(client.isBound()){// While the socket is in binding state
	    	message = in.readUTF(); 
	    if(message.equalsIgnoreCase("CLIENT")){ // If the incoming request is from Client
	    	clientFunction(in,out);// Then the clienFunction() will execute that will perform client's requests
	    }
	    else
	    	serverFunction(in,out, id); // Otherwise it will perform serverFunction() that will perform on other servers
	    }
	}     
	catch(IOException e){ 
        e.printStackTrace();
        }
	}

// This clientFunction() will read Client's request i.e. choice and performs the relevant operation
//---------------------------------------------------------------------------------------------------
public void clientFunction(DataInputStream din,DataOutputStream dos) throws IOException{
	String searchFile, temp1;
	String[] onlyKey;
	int key, port;
	int mod, size = 0, div;
	
	DataInputStream dinServer;
	DataOutputStream doutServer;
	String choice, res;
	
	if(flag == 0){
		port = din.readInt();
	    ht.addPeer(client,port);	//Adding peer to the peerlist
	    smp.serversConnect(Integer.parseInt(id), dInList, dOutList); // Calls serversConnect() method from the ServerMapper Class
	    flag = 1;
	}
	peerInfo = (client.getInetAddress().getHostAddress()+","+ client.getPort()); // Getting Client's info
	Sha1 sha1 = new Sha1();
	choice = din.readUTF();// Reading choice from the client	
	
	
	if(choice.equalsIgnoreCase("PUT")){ // If client wants PUT operation to be executed
		
		size = din.readInt();
		DataOutputStream dTemp = null ;
		boolean bool;
		
		for(Entry<Integer, DataOutputStream> entry: dOutList.entrySet()){
			dTemp = entry.getValue();
		}
		
		for(int i=0;i<size;i++){		
			String fileName = din.readUTF(); // Reading file name from the Client
			int hashcode = fileName.hashCode();
			if(hashcode<0)
				hashcode = -hashcode;
			mod = hashcode % 8;
			res = sha1.hashing(fileName);
			System.out.println("FILENAME:" + fileName);
			
			if(mod == Integer.parseInt(id)){ // This if statement will check whether the client is putting value on the same server or not
				bool = ht.putTable(res, peerInfo);//  If same server then it will put the values in it's private hash table and return True
				if(bool)
					dos.writeUTF("TRUE");
				else
					dos.writeUTF("FALSE");	
					
					if(mod == 0)
						mod = 1;
					else
						mod += 1;
					doutServer = dOutList.get(mod);
					dinServer = dInList.get(mod);
					doutServer.writeUTF("SERVER"); //Sends message as SERVER to the ServerManager to let it know that the request is from the server
					doutServer.writeUTF("PUT");				
					doutServer.writeUTF(res+","+peerInfo);
					String test = dinServer.readUTF();
			}
			else{ // else It will be distributed to the other servers equally in the network
				int x;
				x = mod;
				if(x == 0)
					x = 8;
				doutServer = dOutList.get(x);
				dinServer = dInList.get(x);
				
				System.out.println("Sending to - "+ x + ": " + res +","+peerInfo);
				
				doutServer.writeUTF("SERVER"); //Sends message as SERVER to the ServerManager to let it know that the request is from the server
				doutServer.writeUTF("PUT");
				doutServer.writeUTF(res+","+peerInfo);
				String test = dinServer.readUTF();
				dos.writeUTF(test);
				
				if(mod == 0)
					mod = 1;
				else
					mod += 1;
				if(mod == Integer.parseInt(id)){
					bool = ht.putTable(res, peerInfo);
				}
				else{
					doutServer = dOutList.get(mod);
					dinServer = dInList.get(mod);
					doutServer.writeUTF("SERVER"); //Sends message as SERVER to the ServerManager to let it know that the request is from the server
					doutServer.writeUTF("PUT");				
					doutServer.writeUTF(res+","+peerInfo);
					String test1 = dinServer.readUTF();
				}		
			}
		}
	}
	else if(choice.equalsIgnoreCase("GET")){// If client wants GET operation to be executed
		
		DataOutputStream dTemp;
		String str;
		String tmp = null;
		for(Entry<Integer, DataOutputStream> entry: dOutList.entrySet()){
			dTemp = entry.getValue();
			dTemp.writeUTF("SERVER");
			dTemp.writeUTF("GET");
		}
			searchFile = din.readUTF();
			int hashcode = searchFile.hashCode();
			if(hashcode<0)
				hashcode = -hashcode;
			
			mod = hashcode % 8;// mod will tell the server as to which server no. the key-value pairs will be sent
			res = sha1.hashing(searchFile);// It will result in Hashed value of the Key
			
			if(mod == Integer.parseInt(id)){
				str = ht.getTable(res);// It will retrieve the Key-value pairs from DHT
				dos.writeUTF(str);
			}
			else{
				if(mod == 0)
					mod = 8;
				doutServer = dOutList.get(mod);
				dinServer = dInList.get(mod);	
				try{	
					doutServer.writeUTF(res);
					tmp = dinServer.readUTF();
				}
				catch(IOException e){
					if(mod ==8)
						mod = 0;
					else
						mod += 1;
					doutServer = dOutList.get(mod);
					dinServer = dInList.get(mod);	
					System.out.println(mod);
					System.out.println(res);
					doutServer.writeUTF(res);
					tmp = dinServer.readUTF();
					//System.out.println("TMP: "+tmp);
				}
				dos.writeUTF(tmp);
			}	
	}
	
	else if(choice.equalsIgnoreCase("REPLICATE")){ 
		
		DataOutputStream dTemp;
		DataInputStream dinTemp = null;
		int str, noOfReplicas;
		String fileName;
		String peersCollect = "", temp;
		String[] collect;
		
		for(Entry<Integer, DataOutputStream> entry: dOutList.entrySet()){
			dTemp = entry.getValue();
			dTemp.writeUTF("SERVER");
			dTemp.writeUTF("REPLICATE");
		}
		
		for(Entry<Integer, DataInputStream> entry: dInList.entrySet()){	
			dinTemp = entry.getValue();
			temp = dinTemp.readUTF();
			if(!temp.isEmpty()){
				peersCollect += temp;	
			}
		}
		collect = peersCollect.split(";");
		int length = collect.length;
		noOfReplicas = din.readInt();	
		if(length >= noOfReplicas){
			dos.writeUTF(peersCollect);
		}
		else
			dos.writeUTF("Sorry!! Not many Clients exists to replicate");
	}
	
	else if(choice.equalsIgnoreCase("DELETE")){// If client wants DELETE operation to be executed
		DataOutputStream dTemp;
		boolean bool;
		for(Entry<Integer, DataOutputStream> entry: dOutList.entrySet()){
			dTemp = entry.getValue();
			dTemp.writeUTF("SERVER");
			dTemp.writeUTF("DELETE");//  Pre determined DELETE message is sent to the server
		}
			searchFile = din.readUTF();
			int hashcode = searchFile.hashCode();
			if(hashcode<0)
				hashcode = -hashcode;
			mod = hashcode % 8;
			res = sha1.hashing(searchFile);
			
			if(mod == Integer.parseInt(id)){
				bool = ht.deleteTable(res);// It will delete the Key-value pairs from DHT
				if(bool)
					dos.writeUTF("TRUE");
				else
					dos.writeUTF("FALSE");
				
				if(mod == 0)
					mod = 1;
				else
					mod += 1;
				doutServer = dOutList.get(mod);
				dinServer = dInList.get(mod);
				doutServer.writeUTF("SERVER"); //Sends message as SERVER to the ServerManager to let it know that the request is from the server
				doutServer.writeUTF("DELETE");				
				doutServer.writeUTF(res);
				String test = dinServer.readUTF();
			}
			else{
				int x;
				x = mod;
				if(x == 0)
					x = 8;
				doutServer = dOutList.get(x);
				dinServer = dInList.get(x);	
				doutServer.writeUTF(res);
				String test = dinServer.readUTF();
				dos.writeUTF(test);
				
				if(mod == 0)
					mod = 1;
				else
					mod += 1;
				if(mod == Integer.parseInt(id)){
					bool = ht.deleteTable(res);
				}
				else{
					doutServer = dOutList.get(mod);
					dinServer = dInList.get(mod);
					doutServer.writeUTF("SERVER"); //Sends message as SERVER to the ServerManager to let it know that the request is from the server
					doutServer.writeUTF("PUT");				
					doutServer.writeUTF(res);
					String test2 = dinServer.readUTF();
				}
			}
	}
	else{
		DataOutputStream dTemp;
		boolean bool;
		for(Entry<Integer, DataOutputStream> entry: dOutList.entrySet()){
			dTemp = entry.getValue();
			dTemp.writeUTF("SERVER");
			dTemp.writeUTF("EXIT");
		}	
	}
}

//This serverFunction() will take requests from the server and will perform the requested operations in the DHT
//--------------------------------------------------------------------------------------------------------------
public void serverFunction(DataInputStream dsin, DataOutputStream dson, String id) throws IOException{
	String message;
	String recieve;
	String[] keyValue = new String[3];
	String key;
	String value;
	int size;
	message = dsin.readUTF();
	
	if(message.equalsIgnoreCase("PUT")){// If the message coming from the client is PUT, then this block will be executed
		
			recieve = dsin.readUTF();
			keyValue = recieve.split(",");
			key = keyValue[0];
			value = keyValue[1]+","+keyValue[2];
			System.out.println(ht.putTable(key, value));
			dson.writeUTF("TRUE");
	}
	else if(message.equalsIgnoreCase("GET")){// If the message coming from the client is GET, then this block will be executed
		String res;
		recieve = dsin.readUTF();
		res = ht.getTable(recieve);
		
		dson.writeUTF(res);
	}
	
	else if(message.equalsIgnoreCase("REPLICATE")){
		String info = ht.replicate();
		dson.writeUTF(info);	
	}
	
	else if(message.equalsIgnoreCase("DELETE")){// If the message coming from the client is DELETE, then this block will be executed		
			recieve = dsin.readUTF();
			ht.deleteTable(recieve);
			dson.writeUTF("TRUE");	
	}
	
	else if(message.equalsIgnoreCase("EXIT")){// If the message coming from the client is EXIT, then this block will be executed
		client.close();	
	}
}
}

