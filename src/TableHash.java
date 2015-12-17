/****************************************************************************************
 * NAME: SAMI AHMAD KHAN
 * CWID: A20352677
 * COURSE: CS 550: ADVANCE OPERATING SYSTEMS
 * PROGRAMMING ASSIGNMENT NO.: 03																   
 * TOPIC: "A Decentralized P2P system implementing a Distributed Hash Table"
 * @author SamAK
 * 
*****************************************************************************************/
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/*-------------------------------------------------------------------------------------------------------------------------------------------
 * CLASS NAME: TableHash
 * FUNCTIONS: 1. This function creates a private table for each server where they can store their Key-Value pairs
 * 			  2. And then perform the PUT, GET and DELETE operations in the ConcurrentHashMap.
 * METHODS USED: 1. putTable() - (Boolean)
 * 				 2. getTable() - (String)
 * 				 3. deleteTable() - (Boolean)
 * 				 4. setPrivateTable() - (Accessors)
 * 				 5. getPrivateTable() - (Mutators)
 *-------------------------------------------------------------------------------------------------------------------------------------------
 */	
public class TableHash {
	
	private Hashtable<String, String> peerInfo; 
	private ConcurrentHashMap<String, ArrayList <String>> privateTable;
	
	public void addPeer (Socket S, int port){	
		String hostAdd, peerPort;
		hostAdd = S.getInetAddress().getHostAddress();//.getRemoteSocketAddress().toString();
		peerPort = String.valueOf(port);
		getPeerInfo().put(hostAdd, peerPort); 
	}
	
	public Boolean putTable (String fileName, String peerInfo){// Performs PUT operation in the table and inserts Key-Value pairs
		
		boolean blnExists = privateTable.containsKey(fileName);
		ArrayList<String> str = new ArrayList<String>();
		if(blnExists){
			str= privateTable.get(fileName);
			str.add(peerInfo);
			privateTable.put(fileName, str);
		}
		else {
			str.add(peerInfo);
			privateTable.put(fileName, str);
		}	
		return true;
	}
	
	public String getTable (String fileName){// Performs GET operation in the table and retrieves Key-Value pairs
		String temp, res = "";
		int count = 0;
		ArrayList<String> str = new ArrayList<String>();
		String hash = null;
		System.out.println(getPrivateTable().size());
		for(Entry<String, ArrayList<String>> entry: getPrivateTable().entrySet()){
			if(entry.getKey().equals(fileName)){   ////equalsIgnoreCase()){
				str = entry.getValue();
				count ++;
			}
		}
		String st = String.valueOf(count);
		for(String peerInfo : str){
			res += peerInfo +";";
		}
		return res;
	}
	
	public Boolean deleteTable(String key){// Performs DELETE operation in the table and deletes Key-Value pairs
		getPrivateTable().remove(key);
		return true;
	}

	public ConcurrentHashMap<String, ArrayList<String>> getPrivateTable() {
		return privateTable;
	}

	public void setPrivateTable(ConcurrentHashMap<String, ArrayList<String>> privateTable) {
		this.privateTable = privateTable;
	}
	
	public TableHash(){
		setPrivateTable(new ConcurrentHashMap<String, ArrayList<String>>());
		setPeerInfo(new Hashtable<String, String>());
	}

	public Hashtable<String, String> getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(Hashtable<String, String> peerInfo) {
		this.peerInfo = peerInfo;
	}
	
	public String replicate(){
		String info = "";
		for(Entry<String, String> entry: getPeerInfo().entrySet()){
			info+= entry.getKey()+","+entry.getValue()+";";
		}
		return info;
	}

}
