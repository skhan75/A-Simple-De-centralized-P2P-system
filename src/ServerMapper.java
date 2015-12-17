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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Properties;

/*-------------------------------------------------------------------------------------------------------------------------------------------
 * CLASS NAME: ServerMapper
 * FUNCTIONS: 1. This function will first read again from the "config.properties" file
 * 			  2. And then creates socket connections of all the servers and connects them
 * METHODS USED: 1. serversRead() - (String)
 * 				 2. serversConnect() 
 *-------------------------------------------------------------------------------------------------------------------------------------------
 */	
public class ServerMapper {

	public String serversInfo[] = new String[8];
	
	public String[] serversRead(int id) throws NumberFormatException, UnknownHostException, IOException{

		Properties prop = new Properties();
		String k;
		String[] server;
		String ip;
		int count = 0;
		String port;
		for(int i=1;i<=8;i++){
			InputStream input = null;
			String idd = "";
			server = new String[2];
			if( id != i){
			try {
				input = new FileInputStream("config.properties");
			
				// load a properties file
				prop.load(input);
				// get the property value and print it out
				k = String.valueOf(i);
				idd = prop.getProperty(k);
				server = idd.split(",");
				ip = server[0];
				port = server[1];
				serversInfo[count] = ip+","+port;
				count++;
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
		}
		}
		return serversInfo;		
	}
		
	public void serversConnect(int id, Hashtable<Integer, DataInputStream> dataInputStream, Hashtable<Integer, DataOutputStream> dataOutputStream) throws NumberFormatException, UnknownHostException, IOException{
		
		String[] serverInfo, server;
		serverInfo = serversRead(id);
		Socket socket;
		DataInputStream dIn;
		DataOutputStream dOut;
		int count = 0;
		int key;
		for(int i=1;i<=8;i++){
			if(id != i){ // If id is not of the same server then the following block will execute
			server = serverInfo[count].split(",");
			socket = new Socket(server[0], Integer.parseInt(server[1]));
			dIn = new DataInputStream(socket.getInputStream());
			dataInputStream.put(i, dIn);
			dOut = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.put(i, dOut);
			count++;
			}
		}
	}	
}
