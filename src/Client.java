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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*----------------------------------------------------------------------------------------------
 * CLASS NAME: Client
 * FUNCTIONS: 1. Makes a socket connection with the server
 * 			  2. Ask the user for selection of choice
 * 			  3. Sends the required operation to the Server to perform the required operation
 * METHODS USED: main() function 
 *----------------------------------------------------------------------------------------------
 */
public class Client {
	
	public static void main(String[] args) throws IOException {
		
		Scanner input = new Scanner(System.in);
		ClientManager cManager;
		
		String message = "CLIENT"; // A message that will notify the ServerManager() class that the incoming request if from Client or other Servers
		Scanner in = new Scanner(System.in);
		
		System.out.println("*************************************************");
		System.out.println("  !! WELCOME TO DECENTRALIZED DHT P2P SYSTEM !!  ");
		System.out.println("*************************************************");
		System.out.println("\nEnter the Server's IP you want to connect.");
		String ipAdd = in.nextLine(); //Ask's the user for the Server's IP address it wants to connect
		System.out.println("\nEnter the Server Port you want to connect (Between 9111 - 9118)");
		int cport = in.nextInt(); //Asks the user for the Server's port it wants to connect
		
		Socket clientSocket = new Socket(ipAdd, cport); //Client socket connection with the server
		
		int miniport = clientSocket.getLocalPort(); //miniport is a separate port for peer to peer communication
        miniport++;
        
        ServerSocket mini = new ServerSocket(miniport);
        cManager = new ClientManager(mini); //miniport i.e. the port of the mini client to which the mini server will connect is sent to peerSend()
   	 	Thread t = new Thread(cManager);// a thread of peerSend() starts
   	 	t.start();
		
   	 	/*Instantiating IO stream objects*/
		InputStream inFromServer = clientSocket.getInputStream();
        DataInputStream inputstream = new DataInputStream(inFromServer);
        OutputStream outToServer = clientSocket.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        
        InetAddress cAdd = InetAddress.getLocalHost();
        String ip = cAdd.getHostAddress();
        
        out.writeUTF("CLIENT");
        out.writeInt(miniport);
        
        TableHash th = new TableHash();   
	    String keyVal;
	    int choice;
	    int exkey = 0;
	    String temp;
	   
	    
	    // Menu that will be displayed to the user for entering his/her's selection
	    while(exkey != 5)
	    {
		    System.out.println("\n---------------------------------------------");
		    System.out.println("            Enter a selection                ");
		    System.out.println("---------------------------------------------");
		    System.out.println("If new to the Network. First Press 1 to register yourself on the network.");
		    System.out.println("\n1. UPDATE YOURSELF ON THE SERVER");
		    System.out.println("2. DOWNLOAD A FILE");
		    System.out.println("3. REPLICATE FILES");
		    System.out.println("4. DELETE A FILE");
		    System.out.println("5. EXIT THE SYSTEM");
		    choice = input.nextInt();
		    exkey = choice;
		    
		    switch (choice) {
			case 1:
				System.out.println("Wait while UPDATING is under process.. !!");		    	
		    	out.writeUTF("PUT");	    	
		    	String fileName = "UPLOAD";
		    	BufferedReader reader = null;
			    File[] files = new File(fileName).listFiles();
			    int fileNum = files.length;
			    int div = fileNum/8;
			    out.writeInt(fileNum);//Sending File length to systemManager() 
			    
			    long lStartTime = System.currentTimeMillis();
			    for (File file : files) { 	// this loop will send all the name of files in the peer to the server
			    	out.writeUTF(file.getName());
			    	//System.out.println("IN LOOP: "+ file);
			    	//System.out.println(inputstream.readUTF());  
			    	inputstream.readUTF();
			    }
			    long lEndTime = System.currentTimeMillis();//Stopping timer
		    	long difference = lEndTime - lStartTime;
		    	System.out.println("Elapsed time for UPDATING: " + difference+"milli-seconds");
		    	System.out.println("\nUPDATE SUCCESSFULL .. !!\n"); 
				
				break;
				
			case 2:
				out.writeUTF("CLIENT");
		    	out.writeUTF("GET");
		    	String[] temp1;
		    	String[] fileInfo = new String[2];
		    	int size, count = 0, portNo;
		    	String hostAdd, port, address;
		    	long lStartTime2 = 0;
		    	
		    	
		    	System.out.println("Enter the name of File you want to Download");
		    	String searchFile = input.next();	//Taking file name to be searched from user
		    	out.writeUTF(searchFile);	//Sending file name to server
		    	System.out.println("\nWait for few minutes while SEARCHING is under process.. !!");
		    	
		    	temp = inputstream.readUTF(); //Getting search results from server
		    	temp1 = temp.split(";");
		    	size = temp1.length;
		    	
		    	long lStartTime1 = System.currentTimeMillis();
		    	
		    	if(!temp.isEmpty()){
			    	System.out.println("\nSEARCH SUCCESSFULL...!!\n");
			    	//Displaying the list of peers having the searched file
			    	System.out.println("FILE AVAILABLE AT THE FOLLOWING PEER:-\n--------------------------------------\n");
			    	System.out.println("SNO."+"  IP ADDRESS  "+"   PORT NO.  ");
			    	System.out.println("-------------------------------------");
			    	
				    Hashtable<Integer, String[]> peercheck = new Hashtable<Integer, String[]>();
				    for(int i=0; i<size; i++){
				    	count++;
				    	fileInfo = temp1[i].split(",");
				    	hostAdd = fileInfo[0];
				   		port = fileInfo[1];
				   		peercheck.put(count, fileInfo);
				   		System.out.println(""+count+"     "+hostAdd+"       "+port);	
				  }
				  long lEndTime1 = System.currentTimeMillis();//Stopping timer
				  long difference1 = lEndTime1 - lStartTime1;
				  System.out.println("Elapsed time for SEARCHING: " + difference1+"milli-seconds");
    	 
				  System.out.println("\nAre you sure to Download? (Y/N)");
				  String bool = input.next();
				  
				  //Connecting to the user selected client and downloading the file
				  if(bool.equalsIgnoreCase("y")){
					  System.out.println("\nEnter the Peer No, you want to Download the file from");
					  
					  int pno = input.nextInt();   	
					  String[] peerSend = peercheck.get(pno);
					  
					  //DOWNLOADING STARTS.......
					  
					  int bytesRead;
					  int current = 0;
					  String filename = null;
					  Socket recievers;					  
					  DataOutputStream doutStream;
					  DataInputStream dinStream;
					  FileOutputStream fos = null; //Initializing OutputStream
					  BufferedOutputStream bos = null;//Initializing InputStream
					  
					  address = peerSend[0];
					  portNo = Integer.parseInt(peerSend[1]);
					  lStartTime2 = System.currentTimeMillis();
					  
					  recievers = new Socket(address,portNo+1);		//Connecting to the peer having file
					  
					  doutStream = new DataOutputStream(recievers.getOutputStream());
					  System.out.println("Connecting to peer at IP" + recievers.getInetAddress()+" at Port "+recievers.getPort());
					  dinStream = new DataInputStream(recievers.getInputStream());
					  
					  /*Sending download request to the peer with file name*/
					  doutStream.writeUTF("GET");
					  doutStream.writeUTF(searchFile);
					  int filesize = dinStream.readInt();
					  
					  /*Downloading file from the peer*/
					  try{
						  System.out.println("Accepted connection : " + recievers); 
						  byte [] mybytearray  = new byte [filesize];
					      InputStream is = recievers.getInputStream();
					      fos = new FileOutputStream(Paths.get("./DOWNLOAD").toAbsolutePath().toString()+"/"+searchFile);
					      bos = new BufferedOutputStream(fos);
					      File myFile = new File (Paths.get("./DOWNLOAD").toAbsolutePath().toString()+"/"+filename);
					      bytesRead = is.read(mybytearray,0,mybytearray.length);
					      current = bytesRead;
					      
					      do {
					    	   bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
					    	   if(bytesRead >= 0) 
					    		   current += bytesRead;
					      } while(bytesRead > 0);
					      bos.write(mybytearray);
					      bos.flush();
					      System.out.println("\nDownload Successful !!");
					  }
					  finally { 
						  	recievers.close();
							if (bos != null) bos.close();
					  }
				  }
				  else System.out.println("FILE NOT FOUND..");
		    	}  
				long lEndTime2 = System.currentTimeMillis();//Stopping timer
				long difference2 = lEndTime2 - lStartTime2;
				System.out.println("Elapsed time for DOWNLOADING: " + difference2+"milli-seconds");
				
				break;
				
			case 3:
				out.writeUTF("CLIENT");   
		    	out.writeUTF("REPLICATE");
		    	String enter, recieve;
				String[] clients;
				int portNo1;
				String address1;
				String[] nodes = new String[2];
				String[] concat= new String[2];
		    	int no, size1, fileLength;
		    	
		    	System.out.println("Enter the filename with its extension, that you want to replicate");
		    	enter = input.next();
		    	System.out.println("\nHow many Replication instances do you want?");
		    	no = input.nextInt();
		    	
		    	System.out.println("Wait while REPLICATING is under process.. !!");
		    	
		    	out.writeInt(no);
		    	recieve = inputstream.readUTF(); //Receiving the peer list
		    	
		    	if(recieve != null){
		    		
			    	clients = recieve.split(";");
			    	size1 = clients.length;
			    	long lStartTime3 = System.currentTimeMillis();
			    	
			    	/*Sending the file to be replicated to the peers*/
			    	for(int i=0; i<size1; i++){
			    		nodes = clients[i].split(",");

			    		Socket replicants;
			    		DataOutputStream doutStream1;
			    		DataInputStream dinStream1;
			    		FileInputStream fis1 = null;
			    	    BufferedInputStream bis1 = null;
			    	    OutputStream os1 = null;
			    	    		    		
			    		address1 = nodes[0];
			    		portNo1 = Integer.parseInt(nodes[1]);
			    		replicants = new Socket(address1, portNo1);		//Creating connection to the peer
			    		doutStream1 = new DataOutputStream(replicants.getOutputStream());
			    		dinStream1 = new DataInputStream(replicants.getInputStream());
			    		
			    		doutStream1.writeUTF("REPLICATE");	//Sending replicate request
			    		doutStream1.writeUTF(enter);	    		
			    		os1 = replicants.getOutputStream();
			    		
			    		/*Sending replicate file to the peer*/
			    		File myFile = new File (Paths.get("./UPLOAD").toAbsolutePath().toString()+"/"+enter);			    		
			    		fileLength = (int)myFile.length();
			    		
			    		byte [] mybytearray  = new byte [fileLength];			    		
			    		fis1 = new FileInputStream(myFile);
				        bis1 = new BufferedInputStream(fis1);
				        bis1.read(mybytearray,0,mybytearray.length);
				        os1 = replicants.getOutputStream();
				        doutStream1.writeInt(fileLength); //Sending file legth to client to prepare it to set it'sincoming file size
				        System.out.println("Sending " + enter + "(" + mybytearray.length + " bytes)");
				        os1.write(mybytearray,0,mybytearray.length); // Sending/Writing file
				        os1.flush();	
				        
				        bis1.close();
				        fis1.close();
				        os1.close();
				        replicants.close();
			    	}
			    	System.out.println("REPLICATION SUCCESSFULL..!!");	
			    	long lEndTime3 = System.currentTimeMillis();//Stopping timer
					long difference3 = lEndTime3 - lStartTime3;
					System.out.println("\nElapsed time for DOWNLOADING: " + difference3+"milli-seconds");
		    	}  
		    	break;
		    	
			case 4:
				out.writeUTF("CLIENT");
		    	out.writeUTF("DELETE");		//Sending delete request to the server 
		    	
		    	System.out.println("Enter the name of File you want to Delete");
		    	String deleteFile = input.next();
		    	out.writeUTF(deleteFile);	//sending file to be deleted to the server
		    	System.out.println("\nWait while DELETE is under process.. !!");
		    	inputstream.readUTF(); 
		    	
		    	System.out.println("\nFILENAME DELETED FROM THE NETWORK SUCCESSFULLY !!\n");
		    	System.out.println("< You can search again the same file name to Test for the deleted file\n >");
		    	break;
		    	
			case 5:
				
				out.writeUTF("EXIT");
		    	System.out.println("CONNECTION OUT..! You are out of network.");
		    	break;
		    	
			default:
				break;
			}
	    }
	}
}
		
		
	
